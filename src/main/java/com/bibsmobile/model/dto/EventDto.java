package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventDto {
	
	EventDto(Event event) {
		this.id = event.getId();
		this.name = event.getName();
		this.timeStartLocal = event.getTimeStartLocal();
		this.organization = event.getOrganization();
		this.charity = event.getCharity();
		this.city = event.getCity();
		this.state = event.getState();
		this.address = event.getAddress();
		this.country = event.getCountry();
		this.eventTypes = EventTypeDto.fromEventTypes(event.getEventTypes());
	}
	
	private Long id;
	
	private String name;
	
	private String timeStartLocal;
	
	private String organization;
	
	private String charity;
	
	private String city;
	
	private String state;
	
	private String address;
	
	private String country;
	
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
	 * @return the timeStartLocal
	 */
	public String getTimeStartLocal() {
		return timeStartLocal;
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
	 * @return the eventTypes
	 */
	public Set<EventTypeDto> getEventTypes() {
		return eventTypes;
	}
	
	
}
