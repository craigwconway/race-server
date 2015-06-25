package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.util.SpringJSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RequestMapping("/rest/cartitems")
@Controller
public class CartItemRestController {
	
	private static final Logger log = LoggerFactory.getLogger(CartItemRestController.class);

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<String> search(@RequestParam(required = false) Long userGroupId, @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) EventCartItemTypeEnum eventCartItemType, @RequestParam(required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date toDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userGroupId != null) {
            UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
            List<Event> events = new ArrayList<>();
            if (userGroup != null) {
                for (EventUserGroup eventUserGroup : userGroup.getEventUserGroups()) {
                    events.add(eventUserGroup.getEvent());
                }
            }
            if (!events.isEmpty()) {
                List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvents(events).getResultList();
                if (!eventCartItems.isEmpty()) {
                    List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                    return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
                }
            }
        } else if (eventId != null) {
            Event event = Event.findEvent(eventId);
            if (event != null) {
                List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
                if (!eventCartItems.isEmpty()) {
                    List<Cart> carts = Cart.findCompletedCartsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                    Map <String, Long> totalMoney = new HashMap();
                    Map <String, Long> totalQuantity = new HashMap();
                    Map <String, Map<String, Long>> dailyQuantity = new HashMap();
                    Map <String, Map<String, Long>> dailyMoney = new HashMap();
                    Map <String, Map<String, Long>> sortedDailyMoney = new LinkedHashMap();
                    // now lets fill up dailyMoney
            		//Fill this with zeroes
            		HashMap <String, Long> fillMap = new HashMap <String, Long> ();
            		for (EventCartItemTypeEnum type : EventCartItemTypeEnum.values()) {
            			fillMap.put(type.toString(), new Long(0));
            		}
            		fillMap.put("COUPON", new Long(0));
                    SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");
                    fmt.setTimeZone(event.getTimezone());
                    for(Cart c : carts) {
                    	long noncoupon = 0;
                        for(CartItem ci : c.getCartItems()) {
                    		noncoupon += ci.getPrice() * ci.getQuantity() * 100;
                        	// First get type. Store as a string instead of a enum to support refunds
                        	String type = ci.getEventCartItem().getType().toString();
                        	Long currentPrice = totalMoney.get(type);
                        	Long currentQuantity = totalQuantity.get(type);
                        	if (currentPrice != null) {
                        		currentPrice += ci.getPrice() * ci.getQuantity() * 100;
                        		totalMoney.put(type, currentPrice);
                        	} else {
                        		currentPrice = ci.getPrice() * ci.getQuantity() * 100;
                        		totalMoney.put(type, currentPrice);
                        	}
                        	if(ci.getEventCartItem().getType() != EventCartItemTypeEnum.DONATION) {
                            	if (currentQuantity != null) {
                            		currentQuantity += ci.getQuantity();
                            		totalQuantity.put(type, currentQuantity);
                            	} else {
                            		currentQuantity = (long) ci.getQuantity();
                            		totalQuantity.put(type, currentQuantity);
                            	}                   		
                        	} else {
                            	if (currentQuantity != null) {
                            		currentQuantity += 1;
                            		totalQuantity.put(type, currentQuantity);
                            	} else {
                            		currentQuantity = (long) 1;
                            		totalQuantity.put(type, currentQuantity);
                            	}                    		
                        	}
                        	// Now update Daily section, get time at midnight:
                        	Map<String, Long> dailyPrices = dailyMoney.get(fmt.format(ci.getCreated()));
                        	if ( dailyPrices == null) {
                        		dailyPrices = new HashMap <String, Long>();
                        		for (EventCartItemTypeEnum ecit : EventCartItemTypeEnum.values()) {
                        			dailyPrices.put(ecit.toString(), new Long(0));
                        		}
                        		dailyPrices.put("COUPON", new Long(0));                        		
                        	}
                        	// Check if the type has existing entries:
                        	Long currentDailyPrice = dailyPrices.get(type);
                        	if(currentDailyPrice != null) {
                        		currentDailyPrice += ci.getPrice() * ci.getQuantity() * 100;
                        		dailyPrices.put(type, currentDailyPrice);
                        	} else {
                        		currentDailyPrice = ci.getPrice() * ci.getQuantity() * 100;
                        		dailyPrices.put(type, currentDailyPrice);
                        	}
                        	dailyMoney.put(fmt.format(ci.getCreated()), dailyPrices);
                        	
                        }
                        if(c.getCoupon() != null) {
                        	Long coupon = noncoupon - c.getTotalPreFee();
                        	Map<String, Long> dailyPrices = dailyMoney.get(fmt.format(c.getCreated()));
                        	Long currentCoupon = dailyPrices.get("COUPON");
                        	if(currentCoupon != null) {
                        		currentCoupon += coupon;
                        		dailyPrices.put("COUPON", currentCoupon);
                        	} else {
                        		currentCoupon = coupon;
                        		dailyPrices.put("COUPON", currentCoupon);
                        	}
                        	dailyMoney.put(fmt.format(c.getCreated()), dailyPrices);
                        	Long totalCouponQuantity = totalQuantity.get("COUPON");
                        	if (totalCouponQuantity == null) {
                        		totalQuantity.put("COUPON", new Long(1));
                        	} else {
                        		totalQuantity.put("COUPON", totalCouponQuantity + 1);
                        	}
                        	Long totalCoupon = totalMoney.get("COUPON");
                        	if (totalCoupon == null) {
                        		totalMoney.put("COUPON", coupon);
                        	} else {
                        		totalMoney.put("COUPON", totalCoupon + coupon);
                        	}
                        }
                        
                    }
                    
                    if(!dailyMoney.isEmpty()) {
                        // filling the data for sos0:
                        DateTime minDate = null;
                        DateTime maxDate = null;
                        Calendar minCal = null;
                        //minCal.setTimeZone(event.getTimezone());
                        //maxCal.setTimezone(event.getTimezone());
                        Calendar maxCal = null;
                        for(String dateString : dailyMoney.keySet()) {
                        	Date date;
							try {
								date = fmt.parse(dateString);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return SpringJSONUtil.returnErrorMessage("Server Date Parse Error", HttpStatus.INTERNAL_SERVER_ERROR);
							}
                        	Calendar cal = new GregorianCalendar();
                        	cal.setTime(date);
                        	DateTime dt = new DateTime(date);
                        	if(null == minDate || null == maxDate) {
                        		minDate = dt;
                        		maxDate = dt;
                        		minCal = cal;
                        		maxCal = cal;
                        	} else {
                        		minDate = minDate.isBefore(dt) ? minDate : dt;
                        		maxDate = maxDate.isAfter(dt) ? maxDate : dt;
                        		minCal = minCal.before(cal) ? minCal : cal;
                        		maxCal = maxCal.after(cal) ? maxCal : cal;
                        	}
                        	
                        }
                        //DateTimeFormatter dtf = DateTimeFormat.forPattern("MM-dd-yyyy");
                        //DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yy")
                        //	    .withLocale(Locale.US);
                        for(Calendar calIterator = minCal; !calIterator.after(maxCal); calIterator.add(Calendar.DATE, 1)) {
                        	String fillString = fmt.format(calIterator.getTime());
                        	if(!dailyMoney.containsKey(fillString)) {
                        		dailyMoney.put(fillString, fillMap);
                        	}
                        	sortedDailyMoney.put(fillString, dailyMoney.get(fillString));
                        }
                    }
                    //Find Refunded Carts:
                    Long refundTotal = new Long(0);
                    Long refundCount = new Long(0);
                    for(Cart refundCart : Cart.findRefundedCartsByEventCartItems(eventCartItems, fromDate, toDate).getResultList()) {
                    	refundCount++;
                    	refundTotal += refundCart.getTotalPreFee();
                    }
                    totalMoney.put("REFUND", refundTotal);
                    totalQuantity.put("REFUND", refundCount);
                    
                    log.info("Reports generated for event id: " + event.getId() + " quantities: " + totalQuantity);
                    log.info("Reports generated for event id: " + event.getId() + " money: " + totalMoney);
                    
                    JsonObject responseObj = new JsonObject();
                    Gson gson = new Gson();
                    JsonElement moneyTree = gson.toJsonTree(totalMoney);
                    responseObj.add("totalMoney", moneyTree);
                    JsonElement quantityTree = gson.toJsonTree(totalQuantity);
                    responseObj.add("totalQuantity", quantityTree);
                    JsonElement dailyMoneyTree = gson.toJsonTree(sortedDailyMoney);
                    responseObj.add("dailyMoney", dailyMoneyTree);
                    return new ResponseEntity<>(responseObj.toString(), headers, HttpStatus.OK);
                }
            }
        } else if (eventCartItemType != null) {
            List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByType(eventCartItemType).getResultList();
            if (!eventCartItems.isEmpty()) {
                List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
            }
        } else {
            List<CartItem> cartItems = CartItem.findAllCartItems(fromDate, toDate);
            return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(CartItem.toJsonArray(new ArrayList<CartItem>()), headers, HttpStatus.OK);
    }

}
