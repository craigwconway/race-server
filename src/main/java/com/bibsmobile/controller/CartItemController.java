package com.bibsmobile.controller;
import com.bibsmobile.model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@RequestMapping("/cartitems")
@Controller
@RooWebScaffold(path = "cartitems", formBackingObject = CartItem.class)
public class CartItemController {

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<String> search(
            @RequestParam(required = false) String userGroup,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) EventCartItemTypeEnum eventCartItemType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date toDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userGroup != null && !userGroup.isEmpty()) {
            List<UserGroup> userGroups = UserGroup.findUserGroupsByNameEquals(userGroup).getResultList();
            List<Event> events = new ArrayList<>();
            for (UserGroup group : userGroups) {
                events.addAll(group.getEvents());
            }
            if (!events.isEmpty()) {
                List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvents(events).getResultList();
                if (!eventCartItems.isEmpty()) {
                    List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                    return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
                }
            }
        }
        if (eventId != null) {
            Event event = Event.findEvent(eventId);
            if (event != null) {
                List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
                if (!eventCartItems.isEmpty()) {
                    List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                    return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
                }
            }
        }
        if (eventCartItemType != null) {
            List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByType(eventCartItemType).getResultList();
            if (!eventCartItems.isEmpty()) {
                List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(eventCartItems, fromDate, toDate).getResultList();
                return new ResponseEntity<>(CartItem.toJsonArray(cartItems), headers, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("[]", headers, HttpStatus.OK);
    }

}
