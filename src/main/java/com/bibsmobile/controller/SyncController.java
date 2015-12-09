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
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.EventDto;
import com.bibsmobile.model.dto.EventSyncDto;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.UserProfileUtil;

@RequestMapping("/sync")
@Controller
public class SyncController {

    /**
     * @api {post} /eventlist/byuser Get Sync List
     * @apiName Get Sync List
     * @apiGroup sync
     * @apiDescription Get a list of events that a user can sync to.
     * @apiParam {String} username Username to pull event list for
     * @apiUser eventDto
     * @return
     */
    @RequestMapping(value = "/eventlist/byuser", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> syncForUser(@RequestParam("username") String username) {
        UserProfile user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
        List<Event> events = Event.findEventsForUser(user);
        return new ResponseEntity<String>(EventDto.fromEventsToDtoArray(events), HttpStatus.OK);
    }
    
    /**
     * @api {get} /sync/artifact/:id Sync Event Artifact
     * @apiName Sync Event Artifact
     * @apiGroup sync
     * @apiDescription Load up the event artifact of a particular event to put into a system.
     * @return
     */
    @RequestMapping(value = "/artifact/{id}")
    @ResponseBody
    public ResponseEntity<String> generateSyncObject(@PathVariable("id") Long id) {
    	Event event = Event.findEvent(id);
    	//TODO: Add security
    	return new ResponseEntity<String> (EventSyncDto.fromEventToDto(event), HttpStatus.OK);
    }

}
