// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.EventCartItemPriceChangeController;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemPriceChange;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

privileged aspect EventCartItemPriceChangeController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EventCartItemPriceChangeController.showJson(@PathVariable("id") Long id) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventCartItemPriceChange == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(eventCartItemPriceChange.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EventCartItemPriceChangeController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventCartItemPriceChange> result = EventCartItemPriceChange.findAllEventCartItemPriceChanges();
        return new ResponseEntity<String>(EventCartItemPriceChange.toJsonArray(result), headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> EventCartItemPriceChangeController.createFromJsonArray(@RequestBody String json) {
        for (EventCartItemPriceChange eventCartItemPriceChange: EventCartItemPriceChange.fromJsonArrayToEventCartItemPriceChanges(json)) {
            eventCartItemPriceChange.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> EventCartItemPriceChangeController.updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.fromJsonToEventCartItemPriceChange(json);
        eventCartItemPriceChange.setId(id);
        if (eventCartItemPriceChange.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> EventCartItemPriceChangeController.deleteFromJson(@PathVariable("id") Long id) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventCartItemPriceChange == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        eventCartItemPriceChange.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEventCartItem", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EventCartItemPriceChangeController.jsonFindEventCartItemPriceChangesByEventCartItem(@RequestParam("eventCartItem") EventCartItem eventCartItem) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventCartItemPriceChange.toJsonArray(EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(eventCartItem).getResultList()), headers, HttpStatus.OK);
    }
    
}