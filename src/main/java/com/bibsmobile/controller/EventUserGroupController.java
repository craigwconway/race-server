package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.EventUserGroupId;
import com.bibsmobile.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
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
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/eventusergroups")
@Controller
public class EventUserGroupController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventUserGroup eventUserGroup = new EventUserGroup();
        Event event = Event.findEvent(eventId);
        EventUserGroupId id = new EventUserGroupId();
        id.setEvent(event);
        eventUserGroup.setEvent(event);
        populateEditForm(uiModel, eventUserGroup);
        return "eventusergroups/createUG";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventUserGroup eventUserGroup, BindingResult bindingResult, Model uiModel) {
        EventUserGroupId id = new EventUserGroupId();
        id.setEvent(eventUserGroup.getEvent());
        id.setUserGroup(eventUserGroup.getUserGroup());
        eventUserGroup.setId(id);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventUserGroup);
            return "eventusergroups/createUG";
        }
        uiModel.asMap().clear();
        eventUserGroup.persist();
        return "redirect:/eventusergroups?event=" + eventUserGroup.getEvent().getId();
    }

    @RequestMapping(value = "/{id}/{ugid}", produces = "text/html")
    public String show(@PathVariable("id") Long eventId, @PathVariable("ugid") Long userGroupId, Model uiModel) {
        EventUserGroup eventUserGroup = EventUserGroup.findEventUserGroup(new EventUserGroupId(Event.findEvent(eventId), UserGroup.findUserGroup(userGroupId)));
        uiModel.addAttribute("eventusergroup", eventUserGroup);
        uiModel.addAttribute("itemId", eventId + "/" + userGroupId);
        return "eventusergroups/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        if (event != null) {
            TypedQuery<EventUserGroup> eventUserGroupsByEvent = EventUserGroup.findEventUserGroupsByEvent(event);
            uiModel.addAttribute("eventusergroups", eventUserGroupsByEvent.getResultList());
        }
        uiModel.addAttribute("event", event);
        return "eventusergroups/listUG";
    }

    @RequestMapping(value = "/{id}/{ugid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long eventId, @PathVariable("ugid") Long userGroupId, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Event event = Event.findEvent(eventId);
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        if (userGroup != null && event != null) {
            EventUserGroup eventUserGroup = EventUserGroup.findEventUserGroup(new EventUserGroupId(event, userGroup));
            if (eventUserGroup != null) {
                eventUserGroup.remove();
            }
        }
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventusergroups?event=" + eventId;
    }

    void populateEditForm(Model uiModel, EventUserGroup eventUserGroup) {
        uiModel.addAttribute("eventUserGroup", eventUserGroup);
        uiModel.addAttribute("events", Arrays.asList(eventUserGroup.getEvent()));
        uiModel.addAttribute("usergroups", UserGroup.findAllUserGroups());
    }

    /*
    * JSON
    * */

    @RequestMapping(params = "find=ByEventId", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventUserGroupsByEventId(@RequestParam("eventId") Long eventId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        if (event == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(EventUserGroup.toJsonArray(EventUserGroup.findEventUserGroupsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByUserGroupId", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventUserGroupsByUserGroupId(@RequestParam("userGroupId") Long userGroupId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        if (userGroup == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(EventUserGroup.toJsonArray(EventUserGroup.findEventUserGroupsByUserGroup(userGroup).getResultList()), headers, HttpStatus.OK);
    }



    @RequestMapping(value = "deleteByIds/{id}/{ugid}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteByIds(@PathVariable("id") Long eventId, @PathVariable("ugid") Long userGroupId) {
        Event event = Event.findEvent(eventId);
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userGroup != null && event != null) {
            EventUserGroup eventUserGroup = EventUserGroup.findEventUserGroup(new EventUserGroupId(event, userGroup));
            if (eventUserGroup == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            eventUserGroup.remove();
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "createByIds/{id}/{ugid}", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createByIds(@PathVariable("id") Long eventId, @PathVariable("ugid") Long userGroupId) {
        Event event = Event.findEvent(eventId);
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userGroup != null && event != null) {
            EventUserGroup eventUserGroup = new EventUserGroup();
            eventUserGroup.setId(new EventUserGroupId(event, userGroup));
            eventUserGroup.persist();
        }
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "updateByIds/{id}/{ugid}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateByIds(@RequestBody String json, @PathVariable("id") Long eventId, @PathVariable("ugid") Long userGroupId) {
        Event event = Event.findEvent(eventId);
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userGroup != null && event != null) {
            EventUserGroupId eventUserGroupId = new EventUserGroupId(event, userGroup);
            EventUserGroup eventUserGroupToDelete = EventUserGroup.findEventUserGroup(eventUserGroupId);
            if (eventUserGroupToDelete != null) {
                eventUserGroupToDelete.remove();
            }
            EventUserGroup eventUserGroup = EventUserGroup.fromJsonToEventUserGroup(json);
            eventUserGroup.setId(eventUserGroupId);
            eventUserGroup.persist();
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        }
        return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
    }


	private ConversionService conversionService;

	@Autowired
    public EventUserGroupController(ConversionService conversionService) {
        super();
        this.conversionService = conversionService;
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventUserGroup eventUserGroup, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventUserGroup);
            return "eventusergroups/update";
        }
        uiModel.asMap().clear();
        eventUserGroup.merge();
        return "redirect:/eventusergroups/" + encodeUrlPathSegment(conversionService.convert(eventUserGroup.getId(), String.class), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") EventUserGroupId id, Model uiModel) {
        populateEditForm(uiModel, EventUserGroup.findEventUserGroup(id));
        return "eventusergroups/update";
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") EventUserGroupId id) {
        EventUserGroup eventUserGroup = EventUserGroup.findEventUserGroup(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventUserGroup == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(eventUserGroup.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventUserGroup> result = EventUserGroup.findAllEventUserGroups();
        return new ResponseEntity<String>(EventUserGroup.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        EventUserGroup eventUserGroup = EventUserGroup.fromJsonToEventUserGroup(json);
        eventUserGroup.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
        headers.add("Location",uriBuilder.path(a.value()[0]+"/"+eventUserGroup.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (EventUserGroup eventUserGroup: EventUserGroup.fromJsonArrayToEventUserGroups(json)) {
            eventUserGroup.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") EventUserGroupId id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventUserGroup eventUserGroup = EventUserGroup.fromJsonToEventUserGroup(json);
        eventUserGroup.setId(id);
        if (eventUserGroup.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") EventUserGroupId id) {
        EventUserGroup eventUserGroup = EventUserGroup.findEventUserGroup(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventUserGroup == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        eventUserGroup.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventUserGroupsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventUserGroup.toJsonArray(EventUserGroup.findEventUserGroupsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }

	@RequestMapping(params = "find=ByUserGroup", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventUserGroupsByUserGroup(@RequestParam("userGroup") UserGroup userGroup) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventUserGroup.toJsonArray(EventUserGroup.findEventUserGroupsByUserGroup(userGroup).getResultList()), headers, HttpStatus.OK);
    }
}
