/**
 * 
 */
package com.bibsmobile.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.CustomRegField;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;

/**
 * @author galen
 *
 */
@RequestMapping("/regfields")
@Controller
public class CustomRegFieldController {

    @RequestMapping( method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        CustomRegField customRegField = CustomRegField.fromJsonToCustomRegField(json);
        customRegField.setEvent(Event.findEvent(customRegField.getEvent().getId()));
        customRegField.persist();
        return customRegField.toJson();
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        CustomRegField customRegField = CustomRegField.findCustomRegField(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (customRegField == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(customRegField.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson(@RequestParam(value = "event") Long event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event e = Event.findEvent(event);
        List <CustomRegField> result = CustomRegField.findCustomRegFieldsByEvent(e).getResultList();
        return new ResponseEntity<>(CustomRegField.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (CustomRegField customRegField : CustomRegField.fromJsonArrayToCustomRegFields(json)) {
        	customRegField.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        CustomRegField customRegField = CustomRegField.fromJsonToCustomRegField(json);
        customRegField.setId(id);
        customRegField.setEvent(Event.findEvent(customRegField.getEvent().getId()));
        if (customRegField.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
    	CustomRegField customRegField = CustomRegField.findCustomRegField(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (customRegField == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        customRegField.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

}
