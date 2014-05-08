package com.bibsmobile.controller;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventMap;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/eventmaps")
@Controller
@RooWebScaffold(path = "eventmaps", formBackingObject = EventMap.class)
@RooWebJson(jsonObject = EventMap.class)
public class EventMapController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventMap eventMap = new EventMap();
        Event event = Event.findEvent(eventId);
        eventMap.setEvent(event);
        List<Event> events = new ArrayList<>();
        events.add(event);
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventMap);
        return "eventmaps/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventMap eventMap = EventMap.findEventMap(id);
        List<Event> events = new ArrayList<>();
        events.add(eventMap.getEvent());
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventMap);
        return "eventmaps/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventmaps", EventMap.findEventMapsByEvent(event).getResultList());
        return "eventmaps/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EventMap eventMap = EventMap.findEventMap(id);
        eventMap.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventmaps?event=" + eventMap.getEvent().getId();
    }

    public void populateEditForm(Model uiModel, EventMap eventMap) {
        uiModel.addAttribute("eventMap", eventMap);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsMaps(@RequestParam(value = "event") Long eventId) {
        return EventMap.toJsonArray(EventMap.findEventMapsByEventId(eventId).getResultList());
    }
}
