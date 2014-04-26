package com.bibsmobile.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;

@RequestMapping("/eventitems")
@Controller
@RooWebScaffold(path = "eventitems", formBackingObject = EventCartItem.class)
public class EventCartItemController { 

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long event, 
    		Model uiModel) {
    	 EventCartItem i =  new EventCartItem();
    	 Event e = Event.findEvent(event);
    	 i.setEvent(e);
    	 List<Event> l = new ArrayList<Event>();
    	 l.add(e);
    	 uiModel.addAttribute("events", l);
        populateEditForm(uiModel,i);
        return "eventitems/create";
    }

    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
    	EventCartItem i = EventCartItem.findEventCartItem(id);
    	List<Event> l = new ArrayList<Event>();
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
    public String list(
    		@RequestParam(value = "event", required = true) Long event, 
    		Model uiModel) {
    	Event e = Event.findEvent(event);
        uiModel.addAttribute("event", e);
        uiModel.addAttribute("eventcartitems", EventCartItem.findEventCartItemsByEvent(e).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "eventitems/list";
    }
    
}
