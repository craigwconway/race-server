package com.bibsmobile.controller;

import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserGroup;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("/rest/cartitems")
@Controller
public class CartItemRestController {

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<String> search(
            @RequestParam(required = false) Long userGroupId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) EventCartItemTypeEnum eventCartItemType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
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
