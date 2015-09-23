package com.bibsmobile.controller;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.RegistrationsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RequestMapping("/registrations")
@Controller
public class RegistrationsController {
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchForm(@RequestParam(value="event", required=false) Long eventId, Model uiModel) {
        UserProfile user = UserProfileUtil.getLoggedInUserProfile();
        if (eventId != null && !PermissionsUtil.isEventAdmin(user, Event.findEvent(eventId))) {
            return "accessDeniedFailure";
        }

        uiModel.addAttribute("eventId", eventId);
        uiModel.addAttribute("events", Event.findEventsForUser(user));
        uiModel.addAttribute("registrations", Collections.emptyList());
        uiModel.addAttribute("maxPages", 0);

        return "registrations/search";
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public String searchResults(@RequestParam("event") Long eventId,
                                @RequestParam(value = "firstName", required = false) String firstName,
                                @RequestParam(value = "lastName", required = false) String lastName,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "invoiceId", required = false) String invoiceId,
                                @RequestParam(value = "start", required = false) Integer start,
                                @RequestParam(value = "refunded", required = false) Boolean refunded,
                                @RequestParam(value = "count", required = false) Integer count,
                                Model uiModel) {
        UserProfile user = UserProfileUtil.getLoggedInUserProfile();
        Event event = Event.findEvent(eventId);
        if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), event)) {
            return "accessDeniedFailure";
        }
        if (refunded == null) refunded = false;
        if (start == null) start = 0;
        if (count == null) count = 25;

        List<Cart> registrations = RegistrationsUtil.search(event, firstName, lastName, email, invoiceId, refunded);
        Collections.sort(registrations, new Comparator<Cart>() { public int compare(Cart c1, Cart c2) { return c1.getId().compareTo(c2.getId());}});
        if (registrations == null)
            return null;
        float nrOfPages = (float) registrations.size() / count;

        uiModel.addAttribute("eventId", eventId);
        uiModel.addAttribute("events", Event.findEventsForUser(user));
        uiModel.addAttribute("firstName", firstName);
        uiModel.addAttribute("lastName", lastName);
        uiModel.addAttribute("email", email);
        uiModel.addAttribute("invoiceId", invoiceId);
        uiModel.addAttribute("registrations", registrations.subList(start, Math.min(start + count, registrations.size())));
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        
        return "registrations/search";
    }
    
    
    @RequestMapping(value = "reports", method = RequestMethod.GET)
    public String reportsForm(@RequestParam(value="event", required=false) Long eventId, Model uiModel) {
        UserProfile user = UserProfileUtil.getLoggedInUserProfile();
        if (eventId != null && !PermissionsUtil.isEventAdmin(user, Event.findEvent(eventId))) {
            return "accessDeniedFailure";
        }
        return "registrations/reports";
    }
    
}
