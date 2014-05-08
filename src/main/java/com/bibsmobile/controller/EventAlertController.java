package com.bibsmobile.controller;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAlert;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/eventalerts")
@Controller
@RooWebScaffold(path = "eventalerts", formBackingObject = EventAlert.class)
@RooWebJson(jsonObject = EventAlert.class)
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
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
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
}
