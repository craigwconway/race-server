package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a data transfer object used in conjunction with {@link Event} objects. 
 * This is for use in list views, and has information associated 
 * with selecting an event to learn more about.
 * @author galen
 *
 */
public class EventSyncDto {
	
	/**
	 * @apiDefine eventSyncDto
	 * @apiSuccess (200) {Number} id Id of event
	 * @apiSuccess (200) {String} name Name of event
	 * @apiSuccess (200) {String} timeStartLocal String containing local start time as: MM/dd/yyyy hh:mm:ss a
	 * @apiSuccess (200) {String} timezone String containing the timezoneID of the event
	 * @apiSuccess (200) {Object[]} eventTypes Set of event types in this event
	 * @apiSuccess (200) {Number} eventTypes.id of this event type
	 * @apiSuccess (200) {String} eventTypes.typeName name of this event type
	 * @apiSuccess (200) {String} eventTypes.distance distance of this event type
	 * @apiSuccess (200) {String} eventTypes.racetype type of this event type
	 * @apiSuccessExample Single Result Found
	 * [
	 * 	{
	 * 		"id":1,
	 * 		"name":"Kings Canyon Critical Mass",
	 * 		"timeStartLocal":"12/01/2015 12:44:20 AM",
	 * 		"timezone":"America/Los_Angeles",
	 * 		"eventTypes":
	 * 		[
	 * 			{
	 * 				"id":1,
	 * 				"typeName":"Run with the Lizards 5k",
	 * 				"distance":"5k",
	 * 				"racetype":"Running",
	 * 				"timeStartLocal":"12/01/2015 12:44:21 AM"
	 * 			}
	 * 		],
	 * 		"organizer":null
	 * 	}
	 * ]
	 */
	
	/**
	 * Constructs a DTO object from a raw {@link Event} object.
	 * @param event
	 */
	EventSyncDto(Event event) {
		this.id = event.getId();
		this.name = event.getName();
		this.timeStartLocal = event.getTimeStartLocal();
		this.timeStart = event.getTimeStart();
		this.syncId = event.getSyncId();
		this.timezone  = event.getTimezoneID();
		this.eventTypes = EventTypeSyncDto.fromEventTypes(event.getEventTypes());
	}
	
	EventSyncDto(Event event, boolean brief) {
		this.id = event.getId();
		this.name = event.getName();
		this.timeStartLocal = event.getTimeStartLocal();
		this.timeStart = event.getTimeStart();
		this.syncId = event.getSyncId();
		this.timezone  = event.getTimezoneID();
	}
	
	private Long id;
	
	private String name;
	
	private String timeStartLocal;
	
	private String syncId;
	
	private Date timeStart;
	
	private String timezone;
	
	private Set <EventTypeSyncDto> eventTypes;
	
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String fromEventToDto(Event event ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new EventSyncDto(event));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String fromEventToViewDto(Event event) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new EventSyncDto(event));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String fromEventsToDtoArray(Collection<Event> events ) {
		List <EventSyncDto> dtos = new ArrayList <EventSyncDto>();
		for(Event event : events) {
			dtos.add(new EventSyncDto(event));
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(dtos);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the timeStartLocal
	 */
	public String getTimeStartLocal() {
		return timeStartLocal;
	}

	/**
	 * @return the syncId
	 */
	public String getSyncId() {
		return syncId;
	}

	/**
	 * @return the timeStart
	 */
	public Date getTimeStart() {
		return timeStart;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @return the eventTypes
	 */
	public Set<EventTypeSyncDto> getEventTypes() {
		return eventTypes;
	}
	
	
}
