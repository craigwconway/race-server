package com.bibsmobile.controller;

import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserGroup;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@RequestMapping("/rest/cartitems")
@Controller
public class CartItemRestController {

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
                    List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                    Map <String, Long> totalMoney = new HashMap();
                    Map <String, Long> totalQuantity = new HashMap();
                    Map <Long, Map<String, Long>> dailyQuantity = new HashMap();
                    Map <Long, Map<String, Long>> dailyMoney = new HashMap();
                    // now lets fill up dailyMoney
            		//Fill this with zeroes
            		HashMap <String, Long> fillMap = new HashMap <String, Long> ();
            		for (EventCartItemTypeEnum type : EventCartItemTypeEnum.values()) {
            			fillMap.put(type.toString(), new Long(0));
            		}
                    SimpleDateFormat fmt = new SimpleDateFormat("YYYY-MM-dd");
                    for(CartItem ci : cartItems) {
                    	// First get type. Store as a string instead of a enum to support refunds
                    	String type = ci.getEventCartItem().getType().toString();
                    	Long currentPrice = totalMoney.get(type);
                    	Long currentQuantity = totalQuantity.get(type);
                    	if (currentPrice != null) {
                    		currentPrice += ci.getPrice() * ci.getQuantity();
                    		totalMoney.put(type, currentPrice);
                    	} else {
                    		currentPrice = ci.getPrice() * ci.getQuantity();
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
                    	DateTime checkoutTime = new DateTime(ci.getCreated());
                    	checkoutTime = checkoutTime.withTimeAtStartOfDay();
                    	Map<String, Long> dailyPrices = dailyMoney.get(checkoutTime.getMillis());
                    	if ( dailyPrices == null) {
                    		dailyPrices = new HashMap <String, Long>();
                    		for (EventCartItemTypeEnum ecit : EventCartItemTypeEnum.values()) {
                    			dailyPrices.put(ecit.toString(), new Long(0));
                    		}
                    		System.out.println("new daily prices");
                    		System.out.println(dailyPrices);
                    		
                    	}
                    	// Check if the type has existing entries:
                    	Long currentDailyPrice = dailyPrices.get(type);
                    	if(currentDailyPrice != null) {
                    		currentDailyPrice += ci.getPrice() * ci.getQuantity();
                    		dailyPrices.put(type, currentDailyPrice);
                    	} else {
                    		currentDailyPrice = ci.getPrice() * ci.getQuantity();
                    		dailyPrices.put(type, currentDailyPrice);
                    	}
                    	dailyMoney.put(checkoutTime.getMillis(), dailyPrices);
                    	
                    }
                    
                    if(!dailyMoney.isEmpty()) {
                        // filling the data for sos0:
                        DateTime minDate = null;
                        DateTime maxDate = null;
                        for(Long dateLong : dailyMoney.keySet()) {
                        	DateTime dt = new DateTime(dateLong);
                        	if(null == minDate || null == maxDate) {
                        		minDate = dt;
                        		maxDate = dt;
                        	} else {
                        		minDate = minDate.isBefore(dt) ? minDate : dt;
                        		maxDate = maxDate.isAfter(dt) ? maxDate : dt;
                        	}
                        	
                        }
                        for(DateTime dateIterator = minDate; dateIterator.isBefore(maxDate); dateIterator = dateIterator.plusDays(1)) {
                        	Long fillLong = dateIterator.getMillis();
                        	if(!dailyMoney.containsKey(fillLong)) {
                        		dailyMoney.put(fillLong, fillMap);
                        	}
                        }
                    }

                    
                    System.out.println("TotalMoney:");
                    System.out.println(totalMoney);
                    System.out.println("Total Quantity:");
                    System.out.println(totalQuantity);
                    System.out.println("dailyMoneh:");
                    System.out.println(dailyMoney);
                   
                    JsonObject responseObj = new JsonObject();
                    Gson gson = new Gson();
                    JsonElement moneyTree = gson.toJsonTree(totalMoney);
                    responseObj.add("totalMoney", moneyTree);
                    JsonElement quantityTree = gson.toJsonTree(totalQuantity);
                    responseObj.add("totalQuantity", quantityTree);
                    JsonElement dailyMoneyTree = gson.toJsonTree(dailyMoney);
                    responseObj.add("dailyMoney", dailyMoneyTree);
                    //JsonElement cartItemTree = gson.toJsonTree(cartItems);
                    //responseObj.add("cartItems", cartItemTree);
                    //return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
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
