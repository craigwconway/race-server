package com.bibsmobile.controller;

import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/eventtypes")
@Controller
public class EventTypeController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventType eventType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventType);
            return "eventtypes/create";
        }
        uiModel.asMap().clear();
        eventType.persist();
        return "redirect:/eventtypes/" + this.encodeUrlPathSegment(eventType.getId().toString(), httpServletRequest);
    }

    
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new EventType());
        return "eventtypes/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("eventtype", EventType.findEventType(id));
        uiModel.addAttribute("itemId", id);
        return "eventtypes/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("eventtypes", EventType.findEventTypeEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) EventType.countEventTypes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("eventtypes", EventType.findAllEventTypes(sortFieldName, sortOrder));
        }
        this.addDateTimeFormatPatterns(uiModel);
        return "eventtypes/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventType eventType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventType);
            return "eventtypes/update";
        }
        uiModel.asMap().clear();
        eventType.merge();
        return "redirect:/eventtypes/" + this.encodeUrlPathSegment(eventType.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, EventType.findEventType(id));
        return "eventtypes/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        EventType eventType = EventType.findEventType(id);
        eventType.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventtypes";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("eventType_starttime_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

    void populateEditForm(Model uiModel, EventType eventType) {
        uiModel.addAttribute("eventType", eventType);
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("events", Event.findAllEvents());
    }

    /**
     * @api {post} /eventtypes Create
     * @apiGroup eventtypes
     * @apiName postEventType
     * @apiParam {Object} event Event object this event type belongs to, must contain id
     * @apiParam {String} racetype Format of eventtype
     * @apiParam {String} distance Distance of eventtype, must end in: "mi", "k" or "m"
     * @apiParam {String} startTime Timestamp of event start time
     * @apiParam {Number} [lowBib] Low bib number to map (inclusive)
     * @apiParam {Number} [highBib] High bib number to map (inclusive)
     * @apiParam {Boolean} [autoMapReg=false] Automatically map CartItems with this event type to this event.
     * @apiParam {Date} [typeName] Custom Description of eventtype. This will be autogenerated if left blank.
     * @apiParamExample {json} Sample Post 1:
     * 		{
     * 			"event": {"id": 1},
     * 			"racetype": "Cycling",
     * 			"distance": "5mi",
     * 			"startTime":"2015-05-22T13:20:00.000Z"
     * 		}
     *		* @apiParamExample {json} Sample Post 2:
     * 		{
     * 			"event": {"id": 4},
     * 			"racetype": "Running",
     * 			"distance": "5k",
     * 			"startTime":1427389493,
     * 			"lowBib": 1,
     * 			"highBib": 2000,
     * 			"autoMapReg": "true"
     * 		}
     * @apiSuccess (200) {Object} eventType created eventtype object
     */
    @RequestMapping(method = RequestMethod.POST, headers="Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody EventType eventType) {
    	Event event = Event.findEvent(eventType.getEvent().getId());
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        // check the rights the user has for event
        if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), event)) {
            return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
        }
        eventType.setEvent(event); // Map to true event
        // Compute meters:
        Long meters;
        try {
            if(StringUtils.endsWith(eventType.getDistance(), "k")) {
            	meters = (Long) (1000 * Float.valueOf(eventType.getDistance().replace("k", "")).longValue());
            } else if(StringUtils.endsWith(eventType.getDistance(), "mi")) {
            	meters = (Long) (1760 * Float.valueOf(eventType.getDistance().replace("mi", "")).longValue());
            } else if(StringUtils.endsWith(eventType.getDistance(), "m")) {
            	meters = Long.valueOf(eventType.getDistance());
            } else {
            	meters = null;
            }        	
        } catch (Exception e) {
        	meters = null;
        }
        eventType.setMeters(meters);

        if(eventType.getTypeName() != null && !eventType.getTypeName().isEmpty()) {
        	System.out.println("Using Custom Typename");
        } else {
        	eventType.setTypeName(eventType.getRacetype() + " - " + eventType.getDistance());
        }
        eventType.persist();
        return new ResponseEntity<>(eventType.toJson(), headers, HttpStatus.OK);
    }

    /**
     * @api {put} /eventtypes/:id Update
     * @apiGroup eventtypes
     * @apiName putEventType
     * @apiParam {Number} id ID of event type to modify. Contained in URL.
     * @apiParam {Object} event Event object of containing race. If set to null, this will detach the event type (effective delete).
     * @apiParam {String} racetype Format of eventtype
     * @apiParam {String} distance Distance of eventtype, must end in: "mi", "k" or "m"
     * @apiParam {Date} startTime Timestamp of event start time
     * @apiParam {String} [typeName] Custom Description of eventtype. This will be autogenerated if left blank.
     * @apiParamExample {json} Sample Update:
     * 		{
     * 			"event": {"id": 1},
     * 			"racetype": "Cycling",
     * 			"distance": "5mi",
     * 			"startTime":"2015-05-22T13:20:00.000Z"
     * 			"lowBib": 1,
     * 			"highBib": 797,
     * 			"autoMapReg": "true"
     * 		}
     * @apiParamExample {json} Sample Delete:
     * 		{
     * 			"event": null,
     * 			"racetype": "Cycling",
     * 			"distance": "5mi",
     * 			"startTime":1427389493
     * 		}
     * @apiSuccess (200) No Response
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers="Accept=application/json")
    public ResponseEntity<String> updateFromJson(@PathVariable("id") Long id, @RequestBody EventType eventType) {
    	EventType existing = EventType.findEventType(id);
    	Event event = Event.findEvent(existing.getEvent().getId());
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        // check the rights the user has for event
        if (!PermissionsUtil.isEventAdmin(UserProfileUtil.getLoggedInUserProfile(), event)) {
            return SpringJSONUtil.returnErrorMessage("no rights for this event", HttpStatus.UNAUTHORIZED);
        }
        // Compute meters:
        Long meters;
        try {
            if(StringUtils.endsWith(eventType.getDistance(), "k")) {
            	meters = (Long) (1000 * Float.valueOf(eventType.getDistance().replace("k", "")).longValue());
            } else if(StringUtils.endsWith(eventType.getDistance(), "mi")) {
            	meters = (Long) (1760 * Float.valueOf(eventType.getDistance().replace("mi", "")).longValue());
            } else if(StringUtils.endsWith(eventType.getDistance(), "m")) {
            	meters = Long.valueOf(eventType.getDistance());
            } else {
            	meters = null;
            }        	
        } catch (Exception e) {
        	meters = null;
        }
        // Copy over allowed fields for update:
        existing.setMeters(meters);
        existing.setDistance(eventType.getDistance());
        existing.setRacetype(eventType.getRacetype());
        existing.setStartTime(eventType.getStartTime());
        
        if(eventType.getTypeName() != null && !eventType.getTypeName().isEmpty()) {
        	existing.setTypeName(eventType.getTypeName());
        } else {
        	existing.setTypeName(eventType.getRacetype() + " - " + eventType.getDistance());
        }
        
        //Handle Detach Event Type from PUT request (DELETE BEHAVIOR):
        if(eventType.getEvent() == null) {
        	existing.setEvent(null);
        }
        
        // This guys can't live without each other.
        if(eventType.getLowBib() == null || eventType.getHighBib() == null || eventType.isAutoMapReg() == false) {
        	eventType.setLowBib(null);
        	eventType.setHighBib(null);
        	eventType.setAutoMapReg(false);
        }
        existing.merge();
        return new ResponseEntity<>(eventType.toJson(), headers, HttpStatus.OK);
    }
    
    /**
     * @api {get} /eventtypes/byevent/:eventId Get By Event
     * @apiGroup eventtypes
     * @apiName Get By Event
     * @apiDescription Get a list of event types belonging to an event by that event's ID.
     * @apiParam {Number} eventId ID of event to pull types from as url Param
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/byevent/{eventId}", method = RequestMethod.GET, headers="Accept=application/json")
    public ResponseEntity<String> updateFromJson(@PathVariable("eventId") Long eventId) {
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
    	Event event = Event.findEvent(eventId);
    	List <EventType> eventTypes = EventType.findEventTypesByEvent(event);
    	return new ResponseEntity<>(EventType.toJsonArray(eventTypes), headers, HttpStatus.OK); 
    }

    /**
     * @api {get} /eventtypes/associate/:id Associate Registrations
     * @apiName Associate Registrations
     * @apiGroup eventtypes
     * @apiParam {Number} id Id of event type to associate as URL Param
     * @apiParam {Number} [lowbib=1] low bib number to map to
     * @apiParam {Number} [highbib=100000] high bib number to map to
     * @apiParam {Boolean} [force=false] force re-map of all athletes with this event type
     */
    @RequestMapping(value = "associate/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> associateCarts(
    		@RequestParam(value = "lowbib", defaultValue = "1") long lowbib,
    		@RequestParam(value = "highbib", defaultValue = "100000") long highbib,
    		@RequestParam(value = "force", defaultValue = "false") boolean force,
    		@PathVariable(value = "id") Long id) {
    	System.out.println("entered controller");
    	EventType eventType = EventType.findEventType(id);
    	Event event = eventType.getEvent();
        UserProfile user = UserProfileUtil.getLoggedInUserProfile();
        if (id != null && !PermissionsUtil.isEventAdmin(user, event)) {
        	return SpringJSONUtil.returnErrorMessage("unauthorized", HttpStatus.FORBIDDEN);
        }
        HashSet <Long> bibsUsed = new HashSet<Long>(RaceResult.findBibsUsedInEvent(event));
        Long currentbib = lowbib;
        //TODO: optimize this
        List<CartItem> cartItems;
        if(!force) {
        	cartItems = CartItem.findUnmappedCompletedCartItemsByEventType(eventType).getResultList();
        } else {
        	cartItems = CartItem.findCompletedCartItemsByEventType(eventType).getResultList();
        	if(cartItems.size() > (1+highbib-lowbib)) {
        		return SpringJSONUtil.returnErrorMessage("Range Insufficient", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        	}
        }
        int needsMapping = cartItems.size();
        int mapped = 0;
        System.out.println("found " + cartItems.size() + "cart items");
        for(CartItem ci : cartItems) {
        	System.out.println("Found item: " + ci.getId() + " bib: " + ci.getBib());
        	if(ci.getBib() == null) {
        		// This is not yet exported, allow user to export the bib to another event type
        		while(bibsUsed.contains(currentbib) && currentbib <= highbib) {
        			System.out.println("collision at bib: " + currentbib + ", mapping up to" + highbib);
        			currentbib++;
        		}
        		if(currentbib > highbib) {
        			return SpringJSONUtil.returnErrorMessage(mapped + " of " + needsMapping + " bibs mapped", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        		}
        		System.out.println("current bib:" + currentbib + " high bib: " + highbib);
        		RaceResult rr = new RaceResult();
        		UserProfile mapUser = ci.getUserProfile();
        		rr.setAge(mapUser.getAge());
        		rr.setBib(currentbib);
        		rr.setCity(mapUser.getCity());
        		rr.setState(mapUser.getState());
        		rr.setFirstname(mapUser.getFirstname());
        		rr.setLastname(mapUser.getLastname());
        		rr.setGender(mapUser.getGender());
        		rr.setEvent(event);
        		rr.setEventType(eventType);
        		rr.setUserProfile(mapUser);
        		rr.persist();
        		bibsUsed.add(rr.getBib());
        		currentbib++;
        		System.out.println(rr);
        	} 
        }
        // Create post in Slack reports channel to say what happened:
        return new ResponseEntity<>(HttpStatus.OK);
    }    
    
    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }
}
