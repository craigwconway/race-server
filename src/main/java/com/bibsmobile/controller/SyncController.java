package com.bibsmobile.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.bibsmobile.model.DeviceStatus;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.FuseDevice;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Split;
import com.bibsmobile.model.SyncReport;
import com.bibsmobile.model.TimeSyncStatusEnum;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.EventDto;
import com.bibsmobile.model.dto.EventSyncDto;
import com.bibsmobile.model.dto.SyncReportDto;
import com.bibsmobile.model.dto.TimeSyncContainerDto;
import com.bibsmobile.model.dto.TimeSyncDto;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.SpringJSONUtil;
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
        return new ResponseEntity<String>(EventSyncDto.fromEventsToShortDtoArray(events), HttpStatus.OK);
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
    	return new ResponseEntity<String> (EventSyncDto.fromEventToViewDto(event), HttpStatus.OK);
    }

    /**
     * @api {get} /sync/reports Unsecured Get
     * @apiName Unsecured Get
     * @apiGroup sync
     * @apiDescription unsecured get reports call for js practice and debugging
     * @apiParam {Number} eventId eventID to pull reports from
     * @apiSuccess {Object[]} A collection of syncreport objects belonging to this event.
     * @return
     */
    @RequestMapping(value = "/reports")
    @ResponseBody
    public ResponseEntity<String> getReports(@RequestParam("event") Long eventId) {
    	List<SyncReport> syncs = SyncReport.findSyncReportsByEvent(Event.findEvent(eventId)).getResultList();
		return new ResponseEntity<String>(SyncReportDto.fromSyncReportsToDtoArray(syncs), HttpStatus.OK);
    	
    }
    
    /**
     * @api {post} /sync/times Sync Times
     * @apiName Sync Times
     * @apiGroup sync
     * @apiDescription Sync time objects from client to server. NOTE: This currently blocks until the service works correctly.
     * @apiUse timeSyncContainerDto
     * @param syncObject
     * @param request
     * @return
     */
    @RequestMapping(value = "/times", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity <String> syncTimes(@RequestBody TimeSyncContainerDto syncObject,
    		HttpServletRequest request) {
    	System.out.println(request.getParameterMap());
    	System.out.println("[Sync] Recieved time sync from: " + request.getRemoteAddr() + " client: " + request.getRemoteHost()
    			+ " with mode: " + syncObject.getMode() + " and local timestamp: " + syncObject.getTimestamp());
    	
    	FuseDevice device = null;
    	// Check if the there is a valid certificate given to the FuseDevice
    	if(syncObject.getDeviceId() != null && syncObject.getSecret() != null) {
    		device = FuseDevice.findFuseDevice(syncObject.getDeviceId());
    		// Check if device is authorized
    		if(device.getSecret().contentEquals(syncObject.getSecret())) {
    			DeviceStatus deviceStatus = new DeviceStatus(syncObject.getReaderStatus(), device);
    			deviceStatus.persist();
    		}
    	}
    	
    	//First check if the the event exists, sync codes are authorized and the event has syncing enabled.
    	if(syncObject.getSyncEventId() == null) {
    		return SpringJSONUtil.returnErrorMessage("NullEvent", HttpStatus.BAD_REQUEST);
    	}
    	Event event = Event.findEvent(syncObject.getSyncEventId());
    	if(event == null) {
    		return SpringJSONUtil.returnErrorMessage("EventNotFound", HttpStatus.BAD_REQUEST);
    	}
    	SyncReport syncReport = new SyncReport(syncObject, device, event, request.getRemoteAddr());
    	if(!event.isSync()) {
    		return SpringJSONUtil.returnErrorMessage("SyncDisabled", HttpStatus.UNAUTHORIZED);
    	}
    	if(!event.getSyncId().equals(syncObject.getSyncId())) {
    		syncReport.setStatus(TimeSyncStatusEnum.INVALID_SYNC_CODE);
    		syncReport.persist();
    		return SpringJSONUtil.returnErrorMessage("InvalidSyncCode", HttpStatus.UNAUTHORIZED);
    	}
    	DateTimeZone zone;
    	try{
        	zone = DateTimeZone.forID(syncObject.getTimezone());
    	} catch (Exception e) {
    		return SpringJSONUtil.returnErrorMessage("InvalidTimezone", HttpStatus.BAD_REQUEST);
    	}

    	List <Long> biblist = new LinkedList<Long>();
    	HashMap<Long, RaceResult> resultMap = new HashMap<Long, RaceResult>();
    	if(syncObject.getTimes() == null || syncObject.getTimes().isEmpty()) {
    		syncReport.setStatus(TimeSyncStatusEnum.OK);
        	syncReport.persist();
        	// Finished with checks. Now, if it is test mode, save an entity of test type in the reports.
        	return new ResponseEntity<String> (HttpStatus.OK);
    	}
    	for(TimeSyncDto timeObject :syncObject.getTimes()) {
    		biblist.add(timeObject.getBib());
    		System.out.println("Syncing bib: " + timeObject.getBib());
    	}
    	List<RaceResult> results = RaceResult.findRaceResultsByEventAndMultipleBibs(event, biblist);
    	for(RaceResult result : results) {
    		System.out.println("Found match for bib: " + result.getBib() + " with id: " + result.getId());
    		resultMap.put(result.getBib(), result);
    	}
    	for(TimeSyncDto timeObject :syncObject.getTimes()) {
    		RaceResult result = resultMap.get(timeObject.getBib());
    		boolean unassigned = false;
    		if(result == null) {
    			result = new RaceResult();
    			result.setEvent(event);
    			result.setBib(timeObject.getBib());
    			unassigned = true;
    		}
    		DateTime convertedTime = new DateTime(timeObject.getTime(), zone);
    		long convertedTimestamp = convertedTime.getMillis();
    		if(timeObject.getPosition() == 0) {
    			result.setTimestart(convertedTimestamp);
    		} else if(timeObject.getPosition() == 1) {
    			result.setTimeofficial(convertedTimestamp);
    		} else if(timeObject.getPosition() > 1) {
    			Split splitTime = result.getSplits().get(timeObject.getPosition());
    			if(splitTime == null) {
    				splitTime = new Split();
    			}
    			splitTime.setTime(convertedTimestamp);
    			result.getSplits().put(timeObject.getPosition(), splitTime);
    		}
    		if(unassigned) {
    			System.out.println("Persisting bib: " + result.getBib());
    			result.persist();
    		} else {
    			System.out.println("Merging bib:" + result.getBib());
    			result.merge();
    		}
    	}
    	syncReport.setStatus(TimeSyncStatusEnum.OK);
    	syncReport.persist();
    	// Finished with checks. Now, if it is test mode, save an entity of test type in the reports.
    	return new ResponseEntity<String> (HttpStatus.OK);
    }

}
