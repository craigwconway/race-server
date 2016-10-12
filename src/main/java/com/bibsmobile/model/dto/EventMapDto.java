package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventMap;
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
public class EventMapDto {
	
	EventMapDto(EventMap eventMap) {
		this.id = eventMap.getId();
		this.url = eventMap.getUrl();
		this.name = eventMap.getName();
	}
	
	private Long id;
	
	private String url;
	
	private String name;
	
	
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

	public static String fromEventMapToDto(EventMap eventMap ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new EventMapDto(eventMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromEventsToDtoArray(Collection<EventMap> eventMaps ) {
		List <EventMapDto> dtos = new ArrayList <EventMapDto>();
		for(EventMap map : eventMaps) {
			dtos.add(new EventMapDto(map));
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}	
}
