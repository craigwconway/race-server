package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

import java.io.UnsupportedEncodingException;

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
     * @api {post} /eventtypes
     * @apiGroup eventtypes
     * @apiName post
     * @apiParam {Object} event Event object this event type belongs to, must contain id
     * @apiParam {String} racetype Format of eventtype
     * @apiParam {String} distance Distance of eventtype
     * @apiParam {Number} startTime Timestamp of event start time
     * @apiParam {String} [typeName] Custom Description of eventtype. This will be autogenerated if left blank.
     * @apiParamExample {json} Request-Example:
     * 		{
     * 			"event": {"id": 1},
     * 			"racetype": "Cycling",
     * 			"distance": "5mi",
     * 			"startTime":1427389493
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
        if(StringUtils.endsWith(eventType.getDistance(), "k")) {

        } else if(StringUtils.endsWith(eventType.getDistance(), "mi")) {
        	
        } else if(StringUtils.endsWith(eventType.getDistance(), "meters")) {
        	
        } else {
        	eventType.setMeters(null);
        }
        if(eventType.getTypeName() != null) {
        	eventType.setTypeName(eventType.getRacetype() + " - " + eventType.getDistance());
        }
        eventType.persist();
        return new ResponseEntity<>(eventType.toJson(), headers, HttpStatus.OK);
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
