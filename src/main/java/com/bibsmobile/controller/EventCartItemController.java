package com.bibsmobile.controller;

import com.bibsmobile.model.CustomRegField;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemCoupon;
import com.bibsmobile.model.EventCartItemGenderEnum;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.EventCartItemTypeEnum;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.SlackUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/eventitems")
@Controller
public class EventCartItemController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long event, Model uiModel) {
        EventCartItem i = new EventCartItem();
        Event e = Event.findEvent(event);
        i.setEvent(e);
        List<Event> l = new ArrayList<>();
        l.add(e);
        uiModel.addAttribute("events", l);
        uiModel.addAttribute("eventTypes", e.getEventTypes());
        uiModel.addAttribute("event", e);
        this.populateEditForm(uiModel, i);
        return "eventitems/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventCartItem i = EventCartItem.findEventCartItem(id);
        List<Event> l = new ArrayList<>();
        l.add(i.getEvent());
        uiModel.addAttribute("events", l);
        uiModel.addAttribute("eventTypes", i.getEvent().getEventTypes());
        uiModel.addAttribute("event", i.getEvent());
        this.populateEditForm(uiModel, i);
        return "eventitems/update";
    }
    
    @RequestMapping(value ="/{ticket")

    void populateEditForm(Model uiModel, EventCartItem eventCartItem) {
        uiModel.addAttribute("eventCartItem", eventCartItem);
        this.addDateTimeFormatPatterns(uiModel);
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long event, Model uiModel) {
        Event e = Event.findEvent(event);
        List <Long> eventCartItemIds = new ArrayList<Long>();
        for(EventCartItem eci : EventCartItem.findEventCartItemsByEvent(e).getResultList()) {
        	eventCartItemIds.add(eci.getId());
        }
        List<EventCartItem> eciList = EventCartItem.findEventCartItemsByEvent(e).getResultList();
        boolean tshirt = false;
        for(EventCartItem eci : eciList) {
        	if( eci.getType() == EventCartItemTypeEnum.T_SHIRT) {
        		tshirt = true;
        	}
        }
        List<CustomRegField> fields = CustomRegField.findCustomRegFieldsByEvent(e).getResultList();
        for(CustomRegField field : fields) {
        	if(field.getResponses() != null) {
        		field.setResponses(field.getResponses().replaceAll("\"", "qFq"));
        	}
        }
        uiModel.addAttribute("shirt", tshirt);
        uiModel.addAttribute("event", e);
        uiModel.addAttribute("eventcartitemids", eventCartItemIds);
        uiModel.addAttribute("eventcartitems", EventCartItem.findEventCartItemsByEvent(e).getResultList());
        uiModel.addAttribute("customregfields", CustomRegField.findCustomRegFieldsByEvent(e).getResultList());
        uiModel.addAttribute("eventcoupons", EventCartItemCoupon.findEventCartItemCouponsByEvent(e).getResultList());
        this.addDateTimeFormatPatterns(uiModel);
        return "eventitems/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(id);
        eventCartItem.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventitems?event=" + eventCartItem.getEvent().getId();
    }

    /*
     * JSON
     */
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        EventCartItem eventCartItem = EventCartItem.fromJsonToEventCartItem(json);
        eventCartItem.persist();
        return eventCartItem.toJson();
    }
    
    @RequestMapping(value = "/search", params = "find=ByNameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemsByNameEquals(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(EventCartItem.toJsonArray(EventCartItem.findEventCartItemsByNameEquals(name).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemsByEvent(@RequestParam("event") Long eventId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        List<EventCartItem> resultList;
        if (event == null) {
            resultList = Collections.emptyList();
        } else {
            resultList = EventCartItem.findEventCartItemsByEvent(event).getResultList();
        }
        return new ResponseEntity<>(EventCartItem.toJsonArray(resultList), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/search", params = "find=FullByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindFullEventCartItemsByEvent(@RequestParam("event") Long eventId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        List<EventCartItem> resultList;
        List<CustomRegField> customRegFields;
        if (event == null) {
            resultList = Collections.emptyList();
            customRegFields = Collections.emptyList();
        } else {
            resultList = EventCartItem.findEventCartItemsByEvent(event).getResultList();
            customRegFields = CustomRegField.findVisibleCustomRegFieldsByEvent(event).getResultList();
        }
        return new ResponseEntity<>("{\"eventitems\":" + EventCartItem.toJsonArrayForReg(resultList) + ", \"regfields\":" + CustomRegField.toJsonArray(customRegFields) + "}", headers, HttpStatus.OK);
    }    
    /*
     * Model attributes
     */
    @ModelAttribute("eventcartitemtypeenums")
    public List<EventCartItemTypeEnum> getEventCartItemTypeEnums() {
        return Arrays.asList(EventCartItemTypeEnum.values());
    }

    @ModelAttribute("eventcartitemgenderenums")
    public List<EventCartItemGenderEnum> getEventCartItemGenderEnums() {
        return Arrays.asList(EventCartItemGenderEnum.values());
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(EventCartItem.toJsonArray(EventCartItem.findEventCartItemsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByType", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemsByType(@RequestParam("type") EventCartItemTypeEnum type) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(EventCartItem.toJsonArray(EventCartItem.findEventCartItemsByType(type).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventCartItem == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventCartItem.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson(@RequestParam(value = "event") Long event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event e = Event.findEvent(event);
        List <EventCartItem> result = EventCartItem.findEventCartItemsByEvent(e).getResultList();
        return new ResponseEntity<>(EventCartItem.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (EventCartItem eventCartItem : EventCartItem.fromJsonArrayToEventCartItems(json)) {
            eventCartItem.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventCartItem eventCartItem = EventCartItem.fromJsonToEventCartItem(json);
        eventCartItem.setId(id);
        if (eventCartItem.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventCartItem == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        eventCartItem.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/fullremove/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deletefully(@PathVariable("id") Long id) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventCartItem == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        EventType type = eventCartItem.getEventType();
        if (type != null) {
        	type.setEvent(null);
        	type.merge();
        }
        eventCartItem.setEventType(null);
        eventCartItem.setEvent(null);
        eventCartItem.merge();
        //eventCartItem.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/ticketremove/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteticket(@PathVariable("id") Long id) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventCartItem == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        eventCartItem.setEventType(null);
        eventCartItem.setEvent(null);
        eventCartItem.merge();
        //eventCartItem.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }    
    
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventCartItem eventCartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventCartItem);
            return "eventitems/create";
        }
        uiModel.asMap().clear();
        Event event = eventCartItem.getEvent();
        try {

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            format.setTimeZone(event.getTimezone());
            Calendar timeStart = new GregorianCalendar();
            Calendar timeEnd = new GregorianCalendar();
			timeStart.setTime(format.parse(eventCartItem.getTimeStartLocal()));
			timeEnd.setTime(format.parse(eventCartItem.getTimeEndLocal()));
			eventCartItem.setTimeStart(timeStart.getTime());
			eventCartItem.setTimeEnd(timeEnd.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:/eventitems?event="+event.getId();
		}
        eventCartItem.persist();
        SlackUtil.logRegAddECI(eventCartItem, eventCartItem.getEvent().getName(), UserProfileUtil.getLoggedInUserProfile().getUsername());
        return "redirect:/eventitems/" + this.encodeUrlPathSegment(eventCartItem.getId().toString(), httpServletRequest);
    }

    
    
    /**
     * @api {post} /eventitems/typecreate Create
     * @apiGroup eventitems
     * @apiName postEventItemWithType
     * @apiParam {Object} event Event containing type
     * @apiParam {Number} event.id Id of event
     * @apiParam {Number} price Price in dollars
     * @apiParam {String} timeStartLocal Registration open date of ticket, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {String} timeEndLocal Registration close date of ticket, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {Number} quantity
     * @apiParam {Object} eventType
     * @apiParam {Number} [eventType.id] If null, creates a new eventtype. If present, adds item to existing eventtype.
     * @apiParam {String} eventType.racetype Format of eventtype
     * @apiParam {String} eventType.distance Distance of eventtype, must end in: "mi", "k" or "m"
     * @apiParam {String} eventType.timeStartLocal Timestamp of event start time, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {Number} [eventType.lowBib] Low bib number to map (inclusive)
     * @apiParam {Number} [eventType.highBib] High bib number to map (inclusive)
     * @apiParam {Boolean} [autoMapReg=false] Automatically map CartItems with this event type to this event.
     * @apiParam {Date} [eventType.typeName] Custom Description of eventtype, used for ECI name. This will be autogenerated if left blank.
     * @apiParamExample {json} Sample Post 1:
     * 		{
     * 			"event": {"id": 1},
     * 			"price": 35,
     * 			"timeStartLocal": "10/15/2015 12:30:00 pm"
     * 			"timeEndLocal": "11/12/2015 11:55:00 pm"
     * 			"quantity": 200,
     * 			"eventType": {
     * 				"racetype": "Cycling",
     * 				"distance": "5mi",
     * 				"timeStartLocal":"11/15/2015 12:19:00 pm",
     * 				"typeName":"Tokyo Drift"
     * 			}
     * 		}
     * @apiSuccess (200) {Object} eventType created eci object
     */    
    @RequestMapping(value = "/typecreate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createJsonWithType(@RequestBody EventCartItem eventCartItem) {

        Event event = Event.findEvent(eventCartItem.getEvent().getId());
        EventType eventType = eventCartItem.getEventType();
        if(event == null) {
        	return SpringJSONUtil.returnErrorMessage("EventNull", HttpStatus.BAD_REQUEST);
        }
        eventType.setEvent(eventCartItem.getEvent());
        if(eventType.getId() == null) {
        	System.out.println("Create a new event type");
        	if(eventType.getDistance() == null || eventType.getRacetype() == null || eventType.getTimeStartLocal() == null) {
        		return SpringJSONUtil.returnErrorMessage("InvalidEventType", HttpStatus.BAD_REQUEST);
        	}
        	eventType.setEvent(Event.findEvent(eventType.getEvent().getId()));
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
            	//System.out.println("The event type " + eventType.getTypeName() +  " has been created in "+ event.getName());
            } else {
            	eventType.setTypeName(eventType.getRacetype() + " - " + eventType.getDistance());
            	//System.out.println("The event type " + eventType.getTypeName() +  " has been created in "+ event.getName() + " with an automatic name");
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                format.setTimeZone(event.getTimezone());
                Calendar timeStart = new GregorianCalendar();
    			timeStart.setTime(format.parse(eventType.getTimeStartLocal()));
    			eventType.setStartTime(timeStart.getTime());
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("Recieved malformed start time from frontend in create event type");
    			return SpringJSONUtil.returnErrorMessage("Malformed Start Time", HttpStatus.BAD_REQUEST);
    		}
            
            eventType.persist();
        }
        try {

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            format.setTimeZone(event.getTimezone());
            Calendar timeStart = new GregorianCalendar();
            Calendar timeEnd = new GregorianCalendar();
			timeStart.setTime(format.parse(eventCartItem.getTimeStartLocal()));
			timeEnd.setTime(format.parse(eventCartItem.getTimeEndLocal()));
			eventCartItem.setTimeStart(timeStart.getTime());
			eventCartItem.setTimeEnd(timeEnd.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("InvalidDate", HttpStatus.BAD_REQUEST);
		}
        eventCartItem.persist();
        SlackUtil.logRegAddECI(eventCartItem, eventCartItem.getEvent().getName(), UserProfileUtil.getLoggedInUserProfile().getUsername());
        return SpringJSONUtil.returnStatusMessage(eventCartItem.getId().toString(), HttpStatus.OK);
    }    

    /**
     * @api {post} /eventitems/typeupdate Update
     * @apiGroup eventitems
     * @apiName updateEventItemWithType
     * @apiParam {Number} id ID of eventitem
     * @apiParam {Object} event Event containing type
     * @apiParam {Number} event.id Id of event
     * @apiParam {Number} price Price in dollars
     * @apiParam {String} timeStartLocal Registration open date of ticket, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {String} timeEndLocal Registration close date of ticket, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {Number} quantity
     * @apiParam {Object} eventType
     * @apiParam {Number} [eventType.id] If null, creates a new eventtype. If present, adds item to existing eventtype.
     * @apiParam {String} eventType.racetype Format of eventtype
     * @apiParam {String} eventType.distance Distance of eventtype, must end in: "mi", "k" or "m"
     * @apiParam {String} eventType.timeStartLocal Timestamp of event start time, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {Number} [eventType.lowBib] Low bib number to map (inclusive)
     * @apiParam {Number} [eventType.highBib] High bib number to map (inclusive)
     * @apiParam {Boolean} [autoMapReg=false] Automatically map CartItems with this event type to this event.
     * @apiParam {Date} [eventType.typeName] Custom Description of eventtype, used for ECI name. This will be autogenerated if left blank.
     * @apiParamExample {json} Sample Post 1:
     *	{
     *		"event":
     *		{
     *			"id":2
     *		},
     *		"type":"TICKET",
     *		"name":"tiiiiiket",
     *		"price":"123",
     *		"timeStartLocal":"10/31/2020 06:00:00 am",
     *		"timeEndLocal":"10/31/2020 06:00:00 am",
     *		"eventType":
     *		{
     *			"event":
     *			{
     *				"id":2
     *			},
     *			"typeName":"aa",
     *			"distance":"3 mi",
     *			"timeStartLocal":"10/31/2020 06:00:00 am",
     *			"racetype":"Trail Run"
     *		}
     *	}
     * @apiSuccess (200) {Object} eventType created eci object
     */    
    @RequestMapping(value = "/typeupdate", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> generateJsonWithType(@RequestBody EventCartItem eventCartItem) {

        Event event = Event.findEvent(eventCartItem.getEvent().getId());
        EventType eventType = eventCartItem.getEventType();
        System.out.println("Incoming ET: [ID: " + eventType.getId() +", Racetype: " + eventType.getRacetype() + "]");
        EventType trueEventType ;
        EventCartItem trueEventCartItem;
        if(eventCartItem.getId() != null) {
        	return SpringJSONUtil.returnErrorMessage("EventItemPresent", HttpStatus.BAD_REQUEST);
        }
        if(eventType.getId() != null) {
        	trueEventType = EventType.findEventType(eventType.getId());
	        
	        if(trueEventType == null) {
	        	return SpringJSONUtil.returnErrorMessage("EventTypeMissing", HttpStatus.BAD_REQUEST);
	        }
        } else {
        	trueEventType = new EventType();
        }
        System.out.println("true ET: [ID: " + trueEventType.getId() +", Racetype: " + trueEventType.getRacetype() + "]");
        if(event == null) {
        	return SpringJSONUtil.returnErrorMessage("EventNull", HttpStatus.BAD_REQUEST);
        }
        
        trueEventType.setEvent(event);
        	if(eventType.getDistance() == null || eventType.getRacetype() == null || eventType.getTimeStartLocal() == null) {
        		return SpringJSONUtil.returnErrorMessage("InvalidEventType", HttpStatus.BAD_REQUEST);
        	}
        	System.out.println("Enclosed event:" + trueEventType.getEvent());
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
            	System.out.println("Error Parsing distance string");
            	meters = null;
            }
            trueEventType.setDistance(eventType.getDistance());
            trueEventType.setMeters(meters);
            trueEventType.setRacetype(eventType.getRacetype());
            if(eventType.getTypeName() != null && !eventType.getTypeName().isEmpty()) {
            	trueEventType.setTypeName(eventType.getTypeName());
            	System.out.println("The event type " + eventType.getTypeName() +  " has been created in "+ event.getName());
            } else {
            	trueEventType.setTypeName(eventType.getRacetype() + " - " + eventType.getDistance());
            	System.out.println("The event type " + eventType.getTypeName() +  " has been created in "+ event.getName() + " with an automatic name");
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                format.setTimeZone(event.getTimezone());
                Calendar timeStart = new GregorianCalendar();
    			timeStart.setTime(format.parse(eventType.getTimeStartLocal()));
    			trueEventType.setStartTime(timeStart.getTime());
    			trueEventType.setTimeStartLocal(eventType.getTimeStartLocal());
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("Recieved malformed start time from frontend in create event type");
    			return SpringJSONUtil.returnErrorMessage("Malformed Start Time", HttpStatus.BAD_REQUEST);
    		}
            System.out.println("True Event Type [ID: " +trueEventType.getId() + ", Distance: " + trueEventType.getDistance()
            		+", racetype: " + trueEventType.getRacetype() + ", meters: " + trueEventType.getMeters() 
            		+ ", Start:"+ trueEventType.getTimeStartLocal() + "]");
            trueEventType = trueEventType.merge();
        try {

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            format.setTimeZone(event.getTimezone());
            eventCartItem.setName(trueEventType.getTypeName());
            Calendar timeStart = new GregorianCalendar();
            Calendar timeEnd = new GregorianCalendar();
			timeStart.setTime(format.parse(eventCartItem.getTimeStartLocal()));
			timeEnd.setTime(format.parse(eventCartItem.getTimeEndLocal()));
			eventCartItem.setTimeStart(timeStart.getTime());
			eventCartItem.setTimeEnd(timeEnd.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("InvalidDate", HttpStatus.BAD_REQUEST);
		}
        System.out.println("TrueEventType ID: " + trueEventType.getId());
        eventCartItem.setEventType(EventType.findEventType(trueEventType.getId()));
        eventCartItem.setEvent(Event.findEvent(event.getId()));
        if(eventCartItem.getId() == null)
        {
        	System.out.println("Persisting EventCartItem");
        	eventCartItem.persist();
        }
        return SpringJSONUtil.returnStatusMessage(eventCartItem.getId().toString(), HttpStatus.OK);
    }    
    
    /**
     * @api {post} /eventitems/typeupdate Update
     * @apiGroup eventitems
     * @apiName updateEventItemWithType
     * @apiParam {Number} id ID of eventitem
     * @apiParam {Object} event Event containing type
     * @apiParam {Number} event.id Id of event
     * @apiParam {Number} price Price in dollars
     * @apiParam {String} timeStartLocal Registration open date of ticket, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {String} timeEndLocal Registration close date of ticket, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {Number} quantity
     * @apiParam {Object} eventType
     * @apiParam {Number} [eventType.id] If null, creates a new eventtype. If present, adds item to existing eventtype.
     * @apiParam {String} eventType.racetype Format of eventtype
     * @apiParam {String} eventType.distance Distance of eventtype, must end in: "mi", "k" or "m"
     * @apiParam {String} eventType.timeStartLocal Timestamp of event start time, in time format (MM/dd/yyyy hh:mm:ss a)
     * @apiParam {Number} [eventType.lowBib] Low bib number to map (inclusive)
     * @apiParam {Number} [eventType.highBib] High bib number to map (inclusive)
     * @apiParam {Boolean} [autoMapReg=false] Automatically map CartItems with this event type to this event.
     * @apiParam {Date} [eventType.typeName] Custom Description of eventtype, used for ECI name. This will be autogenerated if left blank.
     * @apiParamExample {json} Sample Post 1:
     *	{
     *		"event":
     *		{
     *			"id":2
     *		},
     *		"type":"TICKET",
     *		"name":"tiiiiiket",
     *		"price":"123",
     *		"timeStartLocal":"10/31/2020 06:00:00 am",
     *		"timeEndLocal":"10/31/2020 06:00:00 am",
     *		"eventType":
     *		{
     *			"event":
     *			{
     *				"id":2
     *			},
     *			"typeName":"aa",
     *			"distance":"3 mi",
     *			"timeStartLocal":"10/31/2020 06:00:00 am",
     *			"racetype":"Trail Run"
     *		}
     *	}
     * @apiSuccess (200) {Object} eventType created eci object
     */    
    @RequestMapping(value = "/typeupdate", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> updateJsonWithType(@RequestBody EventCartItem eventCartItem) {

        Event event = Event.findEvent(eventCartItem.getEvent().getId());
        EventType eventType = eventCartItem.getEventType();
        System.out.println("Incoming ET: [ID: " + eventType.getId() +", Racetype: " + eventType.getRacetype() + "]");
        EventType trueEventType ;
        EventCartItem trueEventCartItem;
        if(eventCartItem.getId() != null) {
        	trueEventCartItem = EventCartItem.findEventCartItem(eventCartItem.getId());
        } else {
        	return SpringJSONUtil.returnErrorMessage("EventItemMissing", HttpStatus.BAD_REQUEST);
        }
        if(eventType.getId() != null) {
        	trueEventType = EventType.findEventType(eventType.getId());
	        
	        if(trueEventType == null) {
	        	return SpringJSONUtil.returnErrorMessage("EventTypeMissing", HttpStatus.BAD_REQUEST);
	        }
        } else {
        	trueEventType = new EventType();
        }
        System.out.println("true ET: [ID: " + trueEventType.getId() +", Racetype: " + trueEventType.getRacetype() + "]");
        if(event == null) {
        	return SpringJSONUtil.returnErrorMessage("EventNull", HttpStatus.BAD_REQUEST);
        }
        
        trueEventType.setEvent(event);
        	if(eventType.getDistance() == null || eventType.getRacetype() == null || eventType.getTimeStartLocal() == null) {
        		return SpringJSONUtil.returnErrorMessage("InvalidEventType", HttpStatus.BAD_REQUEST);
        	}
        	System.out.println("Enclosed event:" + trueEventType.getEvent());
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
            	System.out.println("Error Parsing distance string");
            	meters = null;
            }
            trueEventType.setDistance(eventType.getDistance());
            trueEventType.setMeters(meters);
            trueEventType.setRacetype(eventType.getRacetype());
            if(eventType.getTypeName() != null && !eventType.getTypeName().isEmpty()) {
            	trueEventType.setTypeName(eventType.getTypeName());
            	System.out.println("The event type " + eventType.getTypeName() +  " has been created in "+ event.getName());
            } else {
            	trueEventType.setTypeName(eventType.getRacetype() + " - " + eventType.getDistance());
            	System.out.println("The event type " + eventType.getTypeName() +  " has been created in "+ event.getName() + " with an automatic name");
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                format.setTimeZone(event.getTimezone());
                Calendar timeStart = new GregorianCalendar();
    			timeStart.setTime(format.parse(eventType.getTimeStartLocal()));
    			trueEventType.setStartTime(timeStart.getTime());
    			trueEventType.setTimeStartLocal(eventType.getTimeStartLocal());
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("Recieved malformed start time from frontend in create event type");
    			return SpringJSONUtil.returnErrorMessage("Malformed Start Time", HttpStatus.BAD_REQUEST);
    		}
            System.out.println("True Event Type [ID: " +trueEventType.getId() + ", Distance: " + trueEventType.getDistance()
            		+", racetype: " + trueEventType.getRacetype() + ", meters: " + trueEventType.getMeters() 
            		+ ", Start:"+ trueEventType.getTimeStartLocal() + "]");
            trueEventType = trueEventType.merge();
        try {

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            format.setTimeZone(event.getTimezone());
            eventCartItem.setName(trueEventType.getTypeName());
            Calendar timeStart = new GregorianCalendar();
            Calendar timeEnd = new GregorianCalendar();
			timeStart.setTime(format.parse(eventCartItem.getTimeStartLocal()));
			timeEnd.setTime(format.parse(eventCartItem.getTimeEndLocal()));
			eventCartItem.setTimeStart(timeStart.getTime());
			eventCartItem.setTimeEnd(timeEnd.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("InvalidDate", HttpStatus.BAD_REQUEST);
		}
        System.out.println("TrueEventType ID: " + trueEventType.getId());
        eventCartItem.setEventType(EventType.findEventType(trueEventType.getId()));
        eventCartItem.setEvent(Event.findEvent(event.getId()));
        if(eventCartItem.getId() == null)
        {
        	System.out.println("Persisting EventCartItem");
        	eventCartItem.persist();
        } else {
        	System.out.println("Merging EventCartItem");
        	trueEventCartItem.setEventType(trueEventType);
        	trueEventCartItem.setTimeEndLocal(eventCartItem.getTimeEndLocal());
        	trueEventCartItem.setTimeStartLocal(eventCartItem.getTimeStartLocal());
        	trueEventCartItem.setTimeEnd(eventCartItem.getTimeEnd());
        	trueEventCartItem.setTimeStart(eventCartItem.getTimeStart());
        	trueEventCartItem.setPrice(eventCartItem.getPrice());
        	trueEventCartItem.setName(eventCartItem.getName());
        	if(eventCartItem.getAvailable() != trueEventCartItem.getAvailable() + trueEventCartItem.getPurchased()) {
        		trueEventCartItem.setAvailable(Math.max(0,eventCartItem.getAvailable() - trueEventCartItem.getPurchased()));
        	}

        	//if(eventCartItem.getAvailable() - eventCartItem.getPurchased() < 0) eventCartItem.setAvailable(0); 
            trueEventCartItem.merge();
        }
        return SpringJSONUtil.returnStatusMessage(eventCartItem.getId().toString(), HttpStatus.OK);
    } 
    
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("eventcartitem", EventCartItem.findEventCartItem(id));
        EventCartItem eci = EventCartItem.findEventCartItem(id);
        if(eci.getType() == EventCartItemTypeEnum.TICKET) {
        	uiModel.addAttribute("eventtype", eci.getEventType());
        	uiModel.addAttribute("event", eci.getEvent());
        	uiModel.addAttribute("eventitempricechanges", EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(eci).getResultList());
        	return "eventitems/ticket";
        }
        uiModel.addAttribute("itemId", id);
        return "eventitems/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventCartItem eventCartItem, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventCartItem);
            return "eventitems/update";
        }
        Event event = eventCartItem.getEvent();
        System.out.println("Incoming timeStart: " + eventCartItem.getTimeStartLocal());
        System.out.println("Incoming timeEnd: "  + eventCartItem.getTimeEndLocal());
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            format.setTimeZone(event.getTimezone());
            Calendar timeStart = new GregorianCalendar();
            Calendar timeEnd = new GregorianCalendar();
			timeStart.setTime(format.parse(eventCartItem.getTimeStartLocal()));
			timeEnd.setTime(format.parse(eventCartItem.getTimeEndLocal()));
			eventCartItem.setTimeStart(timeStart.getTime());
			eventCartItem.setTimeEnd(timeEnd.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:/eventitems?event="+event.getId();
		}        
        uiModel.asMap().clear();
        eventCartItem.merge();
        return "redirect:/eventitems/" + this.encodeUrlPathSegment(eventCartItem.getId().toString(), httpServletRequest);
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("eventCartItem_timestart_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("eventCartItem_timeend_date_format", "MM/dd/yyyy h:mm:ss a");
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
