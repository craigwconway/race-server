package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemGenderEnum;
import com.bibsmobile.model.EventCartItemTypeEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequestMapping("/eventitems")
@Controller
@RooWebScaffold(path = "eventitems", formBackingObject = EventCartItem.class)
@RooWebJson(jsonObject = EventCartItem.class)
public class EventCartItemController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long event, Model uiModel) {
        EventCartItem i = new EventCartItem();
        Event e = Event.findEvent(event);
        i.setEvent(e);
        List<Event> l = new ArrayList<>();
        l.add(e);
        uiModel.addAttribute("events", l);
        populateEditForm(uiModel, i);
        return "eventitems/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventCartItem i = EventCartItem.findEventCartItem(id);
        List<Event> l = new ArrayList<>();
        l.add(i.getEvent());
        uiModel.addAttribute("events", l);
        populateEditForm(uiModel, i);
        return "eventitems/update";
    }

    void populateEditForm(Model uiModel, EventCartItem eventCartItem) {
        uiModel.addAttribute("eventCartItem", eventCartItem);
        addDateTimeFormatPatterns(uiModel);
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long event, Model uiModel) {
        Event e = Event.findEvent(event);
        uiModel.addAttribute("event", e);
        uiModel.addAttribute("eventcartitems", EventCartItem.findEventCartItemsByEvent(e).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "eventitems/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(id);
        eventCartItem.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventitems?event=" + eventCartItem.getEvent().getId();
    }

    /*
     * JSON
     * */
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

    /*
    * Model attributes
    * */
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
        return new ResponseEntity<String>(EventCartItem.toJsonArray(EventCartItem.findEventCartItemsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByType", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemsByType(@RequestParam("type") EventCartItemTypeEnum type) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventCartItem.toJsonArray(EventCartItem.findEventCartItemsByType(type).getResultList()), headers, HttpStatus.OK);
    }
    
}
