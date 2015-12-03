package com.bibsmobile.model.dto;

import java.util.HashSet;
import java.util.Set;

import com.bibsmobile.model.EventType;

/**
 * This is a data transfer object used to give information about event types. 
 * It may be used to represent many event types in a list view, or under an event.
 * @author galen
 *
 */
public class EventTypeDto {
	
	EventTypeDto(EventType eventType) {
		this.id = eventType.getId();
		this.typeName = eventType.getTypeName();
		this.distance = eventType.getDistance();
		this.racetype = eventType.getRacetype();
		this.timeStartLocal = eventType.getTimeStartLocal();
	}
	
	public static Set<EventTypeDto> fromEventTypes(Set<EventType> eventTypes) {
		Set <EventTypeDto> returnSet = new HashSet<EventTypeDto>();
		for(EventType eventType: eventTypes) {
			returnSet.add(new EventTypeDto(eventType));
		}
		return returnSet;
	}
	
	private Long id;
	private String typeName;
	private String distance;
	private String racetype;
	private String timeStartLocal;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * @return the racetype
	 */
	public String getRacetype() {
		return racetype;
	}

	/**
	 * @return the startTimeLocal
	 */
	public String getTimeStartLocal() {
		return timeStartLocal;
	}

}
