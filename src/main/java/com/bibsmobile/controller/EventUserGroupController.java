package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.EventUserGroupId;
import com.bibsmobile.model.UserGroup;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.TypedQuery;
import javax.validation.Valid;
import java.util.Arrays;

@RequestMapping("/eventusergroups")
@Controller
@RooWebScaffold(path = "eventusergroups", formBackingObject = EventUserGroup.class)
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
        EventUserGroup eventUserGroup = EventUserGroup.findEventUserGroup(
                new EventUserGroupId(Event.findEvent(eventId), UserGroup.findUserGroup(userGroupId)));
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
}
