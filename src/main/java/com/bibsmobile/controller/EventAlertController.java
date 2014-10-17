package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import com.bibsmobile.model.EventAlert;

@RequestMapping("/eventalerts")
@Controller
public class EventAlertController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventAlert eventAlert = new EventAlert();
        Event event = Event.findEvent(eventId);
        eventAlert.setEvent(event);
        List<Event> events = new ArrayList<>();
        events.add(event);
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventAlert);
        return "eventalerts/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventAlert eventAlert = EventAlert.findEventAlert(id);
        List<Event> events = new ArrayList<>();
        events.add(eventAlert.getEvent());
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventAlert);
        return "eventalerts/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventalerts", EventAlert.findEventAlertsByEvent(event).getResultList());
        return "eventalerts/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        EventAlert eventAlert = EventAlert.findEventAlert(id);
        eventAlert.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventalerts?event=" + eventAlert.getEvent().getId();
    }

    public void populateEditForm(Model uiModel, EventAlert eventAlert) {
        uiModel.addAttribute("eventAlert", eventAlert);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsAlerts(@RequestParam(value = "event", required = false) Long eventId) {
        return EventAlert.toJsonArray(EventAlert.findEventAlertsByEventId(eventId).getResultList());
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventAlert eventAlert, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventAlert);
            return "eventalerts/create";
        }
        uiModel.asMap().clear();
        eventAlert.persist();
        return "redirect:/eventalerts/" + encodeUrlPathSegment(eventAlert.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("eventalert", EventAlert.findEventAlert(id));
        uiModel.addAttribute("itemId", id);
        return "eventalerts/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventAlert eventAlert, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventAlert);
            return "eventalerts/update";
        }
        uiModel.asMap().clear();
        eventAlert.merge();
        return "redirect:/eventalerts/" + encodeUrlPathSegment(eventAlert.getId().toString(), httpServletRequest);
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        EventAlert eventAlert = EventAlert.findEventAlert(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventAlert == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(eventAlert.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventAlert> result = EventAlert.findAllEventAlerts();
        return new ResponseEntity<String>(EventAlert.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        EventAlert eventAlert = EventAlert.fromJsonToEventAlert(json);
        eventAlert.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + eventAlert.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (EventAlert eventAlert : EventAlert.fromJsonArrayToEventAlerts(json)) {
            eventAlert.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventAlert eventAlert = EventAlert.fromJsonToEventAlert(json);
        eventAlert.setId(id);
        if (eventAlert.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        EventAlert eventAlert = EventAlert.findEventAlert(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventAlert == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        eventAlert.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventAlertsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventAlert.toJsonArray(EventAlert.findEventAlertsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
}
