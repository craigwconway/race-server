// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.EventUserGroupController;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.EventUserGroupId;
import com.bibsmobile.model.UserGroup;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

privileged aspect EventUserGroupController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EventUserGroupController.showJson(@PathVariable("id") EventUserGroupId id) {
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
    public ResponseEntity<String> EventUserGroupController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventUserGroup> result = EventUserGroup.findAllEventUserGroups();
        return new ResponseEntity<String>(EventUserGroup.toJsonArray(result), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> EventUserGroupController.createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        EventUserGroup eventUserGroup = EventUserGroup.fromJsonToEventUserGroup(json);
        eventUserGroup.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
        headers.add("Location",uriBuilder.path(a.value()[0]+"/"+eventUserGroup.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> EventUserGroupController.createFromJsonArray(@RequestBody String json) {
        for (EventUserGroup eventUserGroup: EventUserGroup.fromJsonArrayToEventUserGroups(json)) {
            eventUserGroup.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> EventUserGroupController.updateFromJson(@RequestBody String json, @PathVariable("id") EventUserGroupId id) {
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
    public ResponseEntity<String> EventUserGroupController.deleteFromJson(@PathVariable("id") EventUserGroupId id) {
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
    public ResponseEntity<String> EventUserGroupController.jsonFindEventUserGroupsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventUserGroup.toJsonArray(EventUserGroup.findEventUserGroupsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByUserGroup", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> EventUserGroupController.jsonFindEventUserGroupsByUserGroup(@RequestParam("userGroup") UserGroup userGroup) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventUserGroup.toJsonArray(EventUserGroup.findEventUserGroupsByUserGroup(userGroup).getResultList()), headers, HttpStatus.OK);
    }
    
}
