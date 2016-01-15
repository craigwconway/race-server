package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.dto.RaceResultDetailDto;

/**
 * This is a controller for unauthenticated webapp access of the main site.
 * The header bar is removed here.
 * @author galen
 * @since 2016-1-11
 *
 */
@RequestMapping("/r")
@Controller
public class WebController {

	/**
	 * Render a webapp homepage.
	 * @param id ID of organization to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/", produces = "text/html")
    public String home(Model uiModel) {
    	List<Event> events = new ArrayList<Event>();
        uiModel.addAttribute("events", events);
        return "r/home";
    }	
	
	/**
	 * Render a view of an organizer profile.
	 * @param id ID of organization to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/o/{id}", produces = "text/html")
    public String org(@PathVariable("id") Long id, Model uiModel) {
    	try{
	    	UserGroup userGroup = UserGroup.findUserGroup(id);
	    	if(userGroup == null) {
	    		return notFound();
	    	}
	    	List<Event> events = Event.findLiveEventsByUserGroup(userGroup);
	    	System.out.println(events);
	        uiModel.addAttribute("usergroup", userGroup);
	        uiModel.addAttribute("events", events);
	        return "r/org";
    	} catch (Exception e) {
    		return notFound();
    	}
    	
    }
    
	/**
	 * Render a view of an event.
	 * @param id Event to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/e/{id}", produces = "text/html")
    public String event(@PathVariable("id") Long id, Model uiModel) {
    	try{
	    	Event event = Event.findEvent(id);
	    	if(event == null) {
	    		return notFound();
	    	}
	        uiModel.addAttribute("event", event);
	        return "r/event";
    	} catch (Exception e) {
    		return notFound();
    	}
    }

	/**
	 * Render detailed info for event.
	 * @param id Event to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/e/{id}/info", produces = "text/html")
    public String eventInfo(@PathVariable("id") Long id, Model uiModel) {
    	try{
        	Event event = Event.findEvent(id);
        	if(event == null) {
        		return notFound();
        	}
            uiModel.addAttribute("event", event);
            return "r/eventinfo";    		
    	} catch (Exception e) {
    		return notFound();
    	}
    }    

	/**
	 * Render detailed info for event.
	 * @param id Event to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/e/{id}/results", produces = "text/html")
    public String eventResults(@PathVariable("id") Long id, Model uiModel) {
    	try{
        	Event event = Event.findEvent(id);
        	if(event == null) {
        		return notFound();
        	}
            uiModel.addAttribute("event", event);
            return "r/eventresults";    		
    	} catch (Exception e) {
    		return notFound();
    	}
    }     
    
	/**
	 * Render a view of a result.
	 * @param id Result to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/r/{id}", produces = "text/html")
    public String result(@PathVariable("id") Long id, Model uiModel) {
    	RaceResult result = RaceResult.findRaceResult(id);
        uiModel.addAttribute("result", new RaceResultDetailDto(result));
        return "r/result";
    }    
    
	/**
	 * Render a view of an image.
	 * @param id Image to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/i/{id}", produces = "text/html")
    public String photo(@PathVariable("id") Long id, Model uiModel) {
    	RaceImage image = RaceImage.findRaceImage(id);
        uiModel.addAttribute("image", image);
        return "r/image";
    }
    
    public String notFound() {
    	return "r/notfound";
    }
    
}
