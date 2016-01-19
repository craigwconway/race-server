package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class EventDto {
	
	/**
	 * @apiDefine eventDto
	 * @apiSuccess (200) {Number} id Id of event
	 * @apiSuccess (200) {String} name Name of event
	 * @apiSuccess (200) {String} timeStartLocal String containing local start time as: MM/dd/yyyy hh:mm:ss a
	 * @apiSuccess (200) {String} address Street address of event
	 * @apiSuccess (200) {String} city city of event
	 * @apiSuccess (200) {String} state state of event
	 * @apiSuccess (200) {String} country country of event
	 * @apiSuccess (200) {String} charity Charity backed by event
	 * @apiSuccess (200) {String} organization Organiziation throwing event
	 * @apiSuccess (200) {String} photo URL of display photo for event
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
	 * 		"charity":"Ancient Aliens",
	 * 		"city":"San Francisco",
	 * 		"state":"CA",
	 * 		"address":"904 Haight St",
	 * 		"country":"US",
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
	EventDto(Event event) {
		this.id = event.getId();
		this.name = event.getName();
		this.description = event.getDescription();
		this.timeStartLocal = event.getTimeStartLocal();
		this.timeStart = event.getTimeStart();
		this.organization = event.getOrganization();
		this.charity = event.getCharity();
		this.city = event.getCity();
		this.state = event.getState();
		this.address = event.getAddress();
		this.country = event.getCountry();
		this.photo = event.getPhoto();
		this.eventTypes = EventTypeDto.fromEventTypes(event.getEventTypes());
	}
	
	private Long id;
	
	private String name;
	
	private String description;
	
	private String timeStartLocal;
	
	private Date timeStart;
	
	private String organization;
	
	private String charity;
	
	private String city;
	
	private String state;
	
	private String address;
	
	private String country;
	
	private String photo;
	
	private Set <EventTypeDto> eventTypes;
	
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
			return mapper.writeValueAsString(new EventDto(event));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromEventsToDtoArray(Collection<Event> events ) {
		List <EventDto> dtos = new ArrayList <EventDto>();
		for(Event event : events) {
			dtos.add(new EventDto(event));
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the timeStartLocal
	 */
	public String getTimeStartLocal() {
		return timeStartLocal;
	}
	
	/**
	 * @return the timeStart
	 */
	public Date getTimeStart() {
		return timeStart;
	}


	/**
	 * @return the organizer
	 */
	public String getOrganizer() {
		return organization;
	}

	/**
	 * @return the charity
	 */
	public String getCharity() {
		return charity;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * @return the eventTypes
	 */
	public Set<EventTypeDto> getEventTypes() {
		return eventTypes;
	}
	
	
}
