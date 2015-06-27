package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemGenderEnum;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/eventitemspricechanges")
@Controller
public class EventCartItemPriceChangeController {
	
	private static final Logger log = LoggerFactory.getLogger(EventCartItemPriceChangeController.class);

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "eventitem", required = true) Long eventitem, Model uiModel) {
        EventCartItemPriceChange i = new EventCartItemPriceChange();
        EventCartItem e = EventCartItem.findEventCartItem(eventitem);
        List <EventCartItemPriceChange> pricechanges = EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(e).getResultList();
        i.setEventCartItem(e);
        List<EventCartItem> l = new ArrayList<>();
        l.add(e);
        uiModel.addAttribute("eventcartitems", l);
        uiModel.addAttribute("pricechanges", pricechanges);
        this.populateEditForm(uiModel, i);
        return "eventitemspricechanges/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventCartItemPriceChange i = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        List<EventCartItem> l = new ArrayList<>();
        l.add(i.getEventCartItem());
        uiModel.addAttribute("eventcartitems", l);
        this.populateEditForm(uiModel, i);
        return "eventitemspricechanges/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "eventitem", required = true) Long eventitem, Model uiModel) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(eventitem);
        uiModel.addAttribute("eventitem", eventCartItem);
        uiModel.addAttribute("eventcartitempricechanges", EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(eventCartItem).getResultList());
        this.addDateTimeFormatPatterns(uiModel);
        return "eventitemspricechanges/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        long eventItem;
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        eventItem = eventCartItemPriceChange.getEventCartItem().getId();
        eventCartItemPriceChange.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventitemspricechanges?eventitem=" + eventItem;
    }

    void populateEditForm(Model uiModel, EventCartItemPriceChange eventCartItemPriceChange) {
        uiModel.addAttribute("eventCartItemPriceChange", eventCartItemPriceChange);
        this.addDateTimeFormatPatterns(uiModel);
    }

    /*
     * JSON
     */
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.fromJsonToEventCartItemPriceChange(json);
        long id = eventCartItemPriceChange.getEventCartItem().getId();
        EventCartItem eci = EventCartItem.findEventCartItem(id);
        if (eci != null) {
            eventCartItemPriceChange.setEventCartItem(eci);
            eventCartItemPriceChange.persist();
            return eventCartItemPriceChange.toJson();
        } else {
            return "{\"error\": \"invalid event cart item\"}";
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventCartItemPriceChange == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventCartItemPriceChange.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventCartItemPriceChange> result = EventCartItemPriceChange.findAllEventCartItemPriceChanges();
        return new ResponseEntity<>(EventCartItemPriceChange.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserProfile currentUser = UserProfileUtil.getLoggedInUserProfile();

        for (EventCartItemPriceChange eventCartItemPriceChange : EventCartItemPriceChange.fromJsonArrayToEventCartItemPriceChanges(json)) {
            long id = eventCartItemPriceChange.getEventCartItem().getId();
            EventCartItem eci = EventCartItem.findEventCartItem(id);
            Event event = eci.getEvent();
            if (!PermissionsUtil.isEventAdmin(currentUser, event)) {
                return SpringJSONUtil.returnErrorMessage("not authorized for this event", HttpStatus.FORBIDDEN);
            }
            if (eci != null) {
                eventCartItemPriceChange.setEventCartItem(eci);
                eventCartItemPriceChange.persist();
            } else {
                return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            try {
            	//MM/DD/YYYY HH:mm:ss.SSS a
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a");
                format.setTimeZone(event.getTimezone());
                Calendar timeStart = new GregorianCalendar();
                Calendar timeEnd = new GregorianCalendar();
    			timeStart.setTime(format.parse(eventCartItemPriceChange.getDateStartLocal()));
    			timeEnd.setTime(format.parse(eventCartItemPriceChange.getDateEndLocal()));
    			eventCartItemPriceChange.setStartDate(timeStart.getTime());
    			eventCartItemPriceChange.setEndDate(timeEnd.getTime());
    			log.info("Updating price changes for Event " + event.getId() + " - " + event.getName() + " Item: " + eci.getId() + " - " + eci.getName() + 
    					" Category: "+ eventCartItemPriceChange.getCategoryName() + " Price: " + eventCartItemPriceChange.getPrice() +
    					". Setting start time " + eventCartItemPriceChange.getDateStartLocal() + "(" + eventCartItemPriceChange.getStartDate() + ")" +
    					". Setting end time " + eventCartItemPriceChange.getDateEndLocal() + "(" + eventCartItemPriceChange.getEndDate() + ").");
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			log.error("Cannot process price change - error processing time");
    			e.printStackTrace();
    			return new ResponseEntity<> (headers, HttpStatus.UNPROCESSABLE_ENTITY);
    		}
            eventCartItemPriceChange.persist();
        }
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJsonArray(@RequestBody String json) {
        UserProfile currentUser = UserProfileUtil.getLoggedInUserProfile();
        for (EventCartItemPriceChange tmp : EventCartItemPriceChange.fromJsonArrayToEventCartItemPriceChanges(json)) {
            EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(tmp.getId());
            if(eventCartItemPriceChange.getEventCartItem() != null) {
                if (!PermissionsUtil.isEventAdmin(currentUser, eventCartItemPriceChange.getEventCartItem().getEvent())) {
                    return SpringJSONUtil.returnErrorMessage("not authorized for this event", HttpStatus.FORBIDDEN);
                }
            }
            eventCartItemPriceChange.setEventCartItem(null);
            eventCartItemPriceChange.persist();
            eventCartItemPriceChange.remove();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.fromJsonToEventCartItemPriceChange(json);
        eventCartItemPriceChange.setId(id);
        if (eventCartItemPriceChange.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventCartItemPriceChange == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        eventCartItemPriceChange.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEventCartItem", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemPriceChangesByEventCartItem(@RequestParam("eventCartItem") EventCartItem eventCartItem) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(
                EventCartItemPriceChange.toJsonArray(EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(eventCartItem).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemPriceChangesByEvent(@RequestParam("event") long eventId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        List<EventCartItem> ecis = EventCartItem.findEventCartItemsByEvent(event).getResultList();
        List<EventCartItemPriceChange> ecipcs = new ArrayList();
        for(EventCartItem eci : ecis) {
            ecipcs.addAll(eci.getPriceChanges());
        }
        return new ResponseEntity<>(
                EventCartItemPriceChange.toJsonArray(ecipcs), headers, HttpStatus.OK);
    }   

    @RequestMapping(value = "/search", params = "find=FullByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindFullEventCartItemsByEvent(@RequestParam("event") Long eventId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        List<EventCartItem> resultList;
        if (event == null) {
            resultList = Collections.emptyList();
        } else {
            resultList = EventCartItem.findEventCartItemsByEvent(event).getResultList();
            String resultString = "";
            // for each eventcartitem, make a json entity and append it to a megastring
            int itr = 0;
            List <EventCartItem> returnList = new ArrayList();
            for(EventCartItem eci : resultList) {
                EventCartItem neweci = eci;
                neweci.setPriceChanges(new HashSet(EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(eci).getResultList()));
                returnList.add(neweci);
            }
            return new ResponseEntity<>(EventCartItem.toJsonArray(returnList), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(EventCartItem.toJsonArray(resultList), headers, HttpStatus.OK);
    } 

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventCartItemPriceChange eventCartItemPriceChange, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventCartItemPriceChange);
            return "eventitemspricechanges/create";
        }
        uiModel.asMap().clear();
        eventCartItemPriceChange.persist();
        return "redirect:/eventitemspricechanges/" + this.encodeUrlPathSegment(eventCartItemPriceChange.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("eventcartitempricechange", EventCartItemPriceChange.findEventCartItemPriceChange(id));
        uiModel.addAttribute("itemId", id);
        return "eventitemspricechanges/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventCartItemPriceChange eventCartItemPriceChange, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventCartItemPriceChange);
            return "eventitemspricechanges/update";
        }
        uiModel.asMap().clear();
        eventCartItemPriceChange.merge();
        return "redirect:/eventitemspricechanges?eventitem=" + this.encodeUrlPathSegment(eventCartItemPriceChange.getEventCartItem().getId().toString(), httpServletRequest);
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("eventCartItemPriceChange_startdate_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("eventCartItemPriceChange_enddate_date_format", "MM/dd/yyyy h:mm:ss a");
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

    @ModelAttribute("eventcartitempricechangegenderenums")
    public List<EventCartItemGenderEnum> getEventCartItemGenderEnums() {
        return Arrays.asList(EventCartItemGenderEnum.values());
    }
}
