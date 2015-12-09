package com.bibsmobile.model.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;

/**
 * This object is used to sync event types from the cloud into events in local systems.
 * This can be used with timing races.
 * @author galen
 *
 */
public class EventTypeSyncDto {
	
	public EventTypeSyncDto(EventType eventType) {
		this.id = eventType.getId();
		this.typeName = eventType.getTypeName();
		this.distance = eventType.getDistance();
		this.racetype = eventType.getRacetype();
		this.timeStartLocal = eventType.getTimeStartLocal();
		this.results = new ArrayList<RaceResultSyncDto>();
		for(RaceResult rr : RaceResult.findRaceResultsByEventType(eventType).getResultList()) {
			results.add(new RaceResultSyncDto(rr));
		}
	}
	
	public static Set<EventTypeSyncDto> fromEventTypes(Set<EventType> eventTypes) {
		Set <EventTypeSyncDto> returnSet = new HashSet<EventTypeSyncDto>();
		for(EventType eventType: eventTypes) {
			returnSet.add(new EventTypeSyncDto(eventType));
		}
		return returnSet;
	}
	
	private Long id;
	private String typeName;
	private String distance;
	private String racetype;
	private String timeStartLocal;
	private List <RaceResultSyncDto> results;
	
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
	
	/**
	 * @return the results
	 */
	public List <RaceResultSyncDto> getResults() {
		return results;
	}

}
