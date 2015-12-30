/**
 * 
 */
package com.bibsmobile.model.wrapper;

import java.text.SimpleDateFormat;

import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;

/**
 * Generated wrapper for view of event types and tickets.
 * @author galen
 *
 */
public class EventTypeTicketWrapper {
	
	private EventCartItem ticket;
	private EventType type;
	
	public EventTypeTicketWrapper(EventType type) {
		this.type = type;
	}
	
	public EventTypeTicketWrapper(EventType type, EventCartItem ticket) {
		this.type = type;
		this.ticket = ticket;
	}
	
	/**
	 * @return the ticket
	 */
	public EventCartItem getTicket() {
		return ticket;
	}
	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(EventCartItem ticket) {
		this.ticket = ticket;
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
		return "EventTypeTicketWrapper [ticket=" + ticket + ", type=" + type
				+ "]";
	}
	
}
