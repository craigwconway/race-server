package com.bibsmobile.controller;

import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserGroup;

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
                    Map <String, Map<String, Long>> dailyQuantity = new HashMap();
                    Map <String, Map<String, Long>> dailyMoney = new HashMap();
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
                    	if (currentQuantity != null) {
                    		currentQuantity += ci.getQuantity();
                    		totalMoney.put(type, currentQuantity);
                    	} else {
                    		currentQuantity = (long) ci.getQuantity();
                    		totalMoney.put(type, currentQuantity);
                    	}
                    	// Now update Daily section, get time at midnight:
                    	DateTime checkoutTime = new DateTime(ci.getCreated());
                    	checkoutTime = checkoutTime.withTimeAtStartOfDay();
                    	Map<String, Long> dailyPrices = dailyMoney.get(checkoutTime.toString("YYYY/MM/dd"));
                    	if ( dailyPrices == null) {
                    		dailyPrices = new HashMap<String, Long>();
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
                    	dailyMoney.put(checkoutTime.toString("YYYY/MM/dd"), dailyPrices);
                    	
                    }
                    return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
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
