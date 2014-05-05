package com.bibsmobile.controller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bibsmobile.model.EventCartItemGenderEnum;
import com.bibsmobile.model.EventCartItemTypeEnum;
import flexjson.JSON;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;

@RequestMapping("/eventitems")
@Controller
@RooWebScaffold(path = "eventitems", formBackingObject = EventCartItem.class)
@RooWebJson(jsonObject = EventCartItem.class)
@RooWebFinder
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

    @RequestMapping(value="/search", params = "find=ByNameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventCartItemsByNameEquals(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(EventCartItem.toJsonArray(EventCartItem.findEventCartItemsByNameEquals(name).getResultList()), headers, HttpStatus.OK);
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
}
