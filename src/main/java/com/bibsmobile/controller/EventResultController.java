package com.bibsmobile.controller;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventResult;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/eventresults")
@Controller
@RooWebScaffold(path = "eventresults", formBackingObject = EventResult.class)
@RooWebJson(jsonObject = EventResult.class)
public class EventResultController {


    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventResult eventResult = new EventResult();
        Event event = Event.findEvent(eventId);
        eventResult.setEvent(event);
        List<Event> events = new ArrayList<>();
        events.add(event);
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventResult);
        return "eventresults/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventResult eventResult = EventResult.findEventResult(id);
        List<Event> events = new ArrayList<>();
        events.add(eventResult.getEvent());
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventResult);
        return "eventresults/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventresults", EventResult.findEventResultsByEvent(event).getResultList());
        return "eventresults/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EventResult eventResult = EventResult.findEventResult(id);
        eventResult.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventresults?event=" + eventResult.getEvent().getId();
    }

    public void populateEditForm(Model uiModel, EventResult eventResult) {
        uiModel.addAttribute("eventResult", eventResult);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsResults(@RequestParam(value = "event") Long eventId) {
        return EventResult.toJsonArray(EventResult.findEventResultsByEventId(eventId).getResultList());
    }
}
