package com.bibsmobile.controller;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventPhoto;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/eventphotos")
@Controller
@RooWebScaffold(path = "eventphotos", formBackingObject = EventPhoto.class)
@RooWebJson(jsonObject = EventPhoto.class)
public class EventPhotoController {
    
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventPhoto eventPhoto = new EventPhoto();
        Event event = Event.findEvent(eventId);
        eventPhoto.setEvent(event);
        List<Event> events = new ArrayList<>();
        events.add(event);
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventPhoto);
        return "eventphotos/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventPhoto eventPhoto = EventPhoto.findEventPhoto(id);
        List<Event> events = new ArrayList<>();
        events.add(eventPhoto.getEvent());
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventPhoto);
        return "eventphotos/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventphotos", EventPhoto.findEventPhotoesByEvent(event).getResultList());
        return "eventphotos/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EventPhoto eventPhoto = EventPhoto.findEventPhoto(id);
        eventPhoto.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventphotos?event=" + eventPhoto.getEvent().getId();
    }

    public void populateEditForm(Model uiModel, EventPhoto eventPhoto) {
        uiModel.addAttribute("eventPhoto", eventPhoto);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsPhotos(@RequestParam(value = "event") Long eventId) {
        return EventPhoto.toJsonArray(EventPhoto.findEventPhotosByEventId(eventId).getResultList());
    }
}
