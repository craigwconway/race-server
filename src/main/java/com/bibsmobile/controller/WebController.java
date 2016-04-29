package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.dto.RaceResultDetailDto;
import com.bibsmobile.model.dto.RaceResultViewDto;
import com.bibsmobile.service.EventSearchCriteria;
import com.bibsmobile.service.EventService;

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
	public static final String SESSION_ATTR_LONGITUDE = "lon";
	public static final String SESSION_ATTR_LATITUDE = "lat";
	
	@Autowired
	EventService eventService;

    @RequestMapping(value = "", produces = "text/html")
    public String home(Model uiModel, HttpServletRequest request,
    		@RequestParam(value = "lon", required = false) Double longitude,
    		@RequestParam(value = "lat", required = false) Double latitude,
    		@RequestParam(value = "worldwide", required = false, defaultValue="false") boolean ignoreLocation,
    		@RequestParam(value = "name", required = false) String name,
    		@RequestParam(value = "page", required = false, defaultValue ="1") int page) {
    	// First, check for an existing session. We do not want to load a session for anonymous users.
    	Double searchLon = longitude;
    	Double searchLat = latitude;
    	HttpSession session = request.getSession(false);
    	if(longitude != null && latitude != null) {
    		if(session == null) {
    			session = request.getSession();
    		}
    		session.setAttribute(SESSION_ATTR_LONGITUDE, longitude);
    		session.setAttribute(SESSION_ATTR_LATITUDE, latitude);
    	}
    	if(session != null) {
    		searchLon = (Double) session.getAttribute(SESSION_ATTR_LONGITUDE);
    		searchLat = (Double) session.getAttribute(SESSION_ATTR_LATITUDE);
    	}
    	EventSearchCriteria searchCriteria = new EventSearchCriteria();
    	if(searchLon != null && searchLat != null && !ignoreLocation) {
    		searchCriteria.addGeospatialCriteria(searchLon, searchLat);
    	}
    	if(!StringUtils.isEmpty(name)) {
    		searchCriteria.addNameCriteria(name);
    	}
    	List<Event> events = eventService.compoundSearch(searchCriteria);
        uiModel.addAttribute("events", events);
        if(name != null) {
        	uiModel.addAttribute("name", name);
        }
        uiModel.addAttribute("page", page);
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
	        uiModel.addAttribute("preEvent", new Date().before(event.getTimeStart()));
	        if(new Date().before(event.getTimeStart())) {
	        	DateTime current = new DateTime();
	        	DateTime eventStart = new DateTime(event.getTimeStart());
	        	if(Months.monthsBetween(current, eventStart).getMonths() > 0) {
	        		uiModel.addAttribute("timeUnit", "months");
	        		uiModel.addAttribute("timeBefore", Months.monthsBetween(current, eventStart).getMonths());
	        	} else {
	        		uiModel.addAttribute("timeUnit", "days");
	        		uiModel.addAttribute("timeBefore", (Days.daysBetween(current, eventStart).getDays()));
	        	}
	        	
	        }
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
    public String eventResults(@PathVariable("id") Long id, Model uiModel,
    		@RequestParam(value = "gender", required = false) String gender,
    		@RequestParam(value = "type", required = false) Long eventTypeId,
    		@RequestParam(value = "search", required = false) String search) {
    	try{
        	Event event = Event.findEvent(id);
        	if(event == null) {
        		return notFound();
        	}
        	EventType eventType = null;
        	if(eventTypeId == null) {
        		if(!event.getEventTypes().isEmpty()) {
        			uiModel.addAttribute("eventType", new ArrayList<EventType>(event.getEventTypes()).get(0));
        		}
        	}
        	if(StringUtils.isEmpty(gender)) {
        		uiModel.addAttribute("gender", "ALL");
        	} else {
        		uiModel.addAttribute("gender", gender);
        	}
        	if(!StringUtils.isEmpty(search)) {
        		uiModel.addAttribute("search", search);
        	}
        	uiModel.addAttribute("results", RaceResultViewDto.fromRaceResultsToRawDtoArray(RaceResult.searchPaginated(event.getId(), eventTypeId, "", null, 1, 10, null, null)));
            uiModel.addAttribute("event", event);
            return "r/eventresults";    		
    	} catch (Exception e) {
    		e.printStackTrace();
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
