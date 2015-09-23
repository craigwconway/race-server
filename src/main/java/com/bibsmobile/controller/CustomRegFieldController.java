/**
 * 
 */
package com.bibsmobile.controller;

import java.util.HashSet;
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

	/**
     * @api {post} /regfields Create Question
     * @apiGroup regfields
	 * @apiName Create Regfield
	 * @apiDescription Custom questions to appear on registration forms. 
	 * These can be visible on all questions, or restricted to a select few. If optional is set true, the customer
	 * may choose to skip them at checkout. If hidden, they will not be returned to answer at checkout.
	 * @apiParam {Object} event Event object this CustomRegField belongs to
	 * @apiParam {Number} event.id Id of event object this CustomRegField belongs to
     * @apiParam {String} question Question mapped by this CustomRegField
     * @apiParam {String} [responseSet] Comma-delimited set of responses to use for dropdowns
     * @apiParam {Object[]} [eventItems] Array of EventCartItem objects to restrict this question to. If null, it appears on all.
     * @apiParam {Number} [eventItems.id] ID of mapped EventCartItems
     * @apiParam {Boolean} [optional=false] Whether or not the question is optional
     * @apiParam {Boolean} [hidden=false] Whether or not this question is hidden
     * @apiParamExample {json} Sample Post 1:
     * 	{
     * 		"event":{"id":2},
     * 		"question":"What is the only real dev language?",
     * 		"responseSet":"nodejs,nodejs"
     * 	}
     * @apiParamExample {json} Sample Post 2:
     * 	{
     * 		"event":{"id":2},
     * 		"optional":true,
     * 		"question":"What is the top search engine?",
     * 		"responseSet":"AOL,AOL",
     * 		"eventItems":[{"id":1},{"id":2}]
     * 	}
     * @apiSuccess (200) {Object} customRegField created CustomRegField object
     */
    @RequestMapping( method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        CustomRegField customRegField = CustomRegField.fromJson(json);
        customRegField.setEvent(Event.findEvent(customRegField.getEvent().getId()));
        HashSet <EventCartItem> eventItems = new HashSet<EventCartItem>();
        for(EventCartItem eventItem : customRegField.getEventItems()) {
        	eventItems.add(EventCartItem.findEventCartItem(eventItem.getId()));
        }
        if(eventItems.size() > 0) {
        	customRegField.setAllItems(false);
        	customRegField.setEventItems(eventItems);
        } else {
        	customRegField.setAllItems(true);
        }
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

	/**
     * @api {put} /regfields Edit Question
     * @apiGroup regfields
	 * @apiName Edit Question
	 * @apiDescription Custom questions to appear on registration forms. 
	 * These can be visible on all questions, or restricted to a select few. If optional is set true, the customer
	 * may choose to skip them at checkout. If hidden, they will not be returned to answer at checkout.
	 * @apiParam {Object} event Event object this CustomRegField belongs to
	 * @apiParam {Number} event.id Id of event object this CustomRegField belongs to
     * @apiParam {String} question Question mapped by this CustomRegField
     * @apiParam {String} [responseSet] Comma-delimited set of responses to use for dropdowns
     * @apiParam {Object[]} [eventItems] Array of EventCartItem objects to restrict this question to. If null, it appears on all.
     * @apiParam {Number} [eventItems.id] ID of mapped EventCartItems
     * @apiParam {Boolean} [optional=false] Whether or not the question is optional
     * @apiParam {Boolean} [hidden=false] Whether or not this question is hidden
     * @apiParamExample {json} Sample Hide
     * 	{
     * 		"id": 1,
     * 		"event":{"id":2},
     * 		"hidden":true,
     * 		"question":"What is the only real dev language?",
     * 		"responseSet":"nodejs,nodejs"
     * 	}
     * @apiParamExample {json} Sample Edit
     * 	{
     * 		"id": 1,
     * 		"event":{"id":2},
     * 		"optional":true,
     * 		"question":"What is the top search engine?",
     * 		"responseSet":"AOL,AOL",
     * 		"eventItems":[{"id":1},{"id":2}]
     * 	}
     * @apiSuccess (200) {Object} customRegField created CustomRegField object
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        CustomRegField customRegField = CustomRegField.fromJson(json);
        customRegField.setId(id);
        customRegField.setEvent(Event.findEvent(customRegField.getEvent().getId()));
        HashSet <EventCartItem> eventItems = new HashSet<EventCartItem>();
        for(EventCartItem eventItem : customRegField.getEventItems()) {
        	eventItems.add(EventCartItem.findEventCartItem(eventItem.getId()));
        }
        if(eventItems.size() > 0) {
        	customRegField.setAllItems(false);
        	customRegField.setEventItems(eventItems);
        } else {
        	customRegField.setEventItems(null);
        	customRegField.setAllItems(true);
        }
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
