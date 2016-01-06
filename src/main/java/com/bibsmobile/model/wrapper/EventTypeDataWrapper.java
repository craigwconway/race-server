/**
 * 
 */
package com.bibsmobile.model.wrapper;

import java.text.SimpleDateFormat;

import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;

/**
 * Generated wrapper for view of event types for analytics view.
 * Used to represent collections of {@link com.bibsmobile.model.EventType EventType} objects
 * and statistics on finishers for rendering in jspx.
 * @author galen
 * @since 2015-12-29
 *
 */
public class EventTypeDataWrapper {
	
	private EventType type;
	
	public EventTypeDataWrapper(EventType type) {
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public EventType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(EventType type) {
		this.type = type;
	}

	/**
	 * @return the athletes
	 */
	public long getAthletes() {
		if(type == null) return 0;
		if(type.getId() == null) return 0;
		return RaceResult.countFindRaceResultsByEventType(type);
	}
	
	public long getFinished() {
		if(type == null) return 0;
		if(type.getId() == null) return 0;
		return RaceResult.countRaceResultsCompleteByEventType(type);
	}
	
	public String getGunTimeLocal() {
		if (type == null) return null;
		if(type.getGunTime() == null) {
			return "Not Fired";
		}
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		if(type.getEvent().getTimezone() != null) format.setTimeZone(type.getEvent().getTimezone());
        return format.format(type.getGunTime());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventTypeTicketWrapper [type=" + type
				+ "]";
	}
	
}
