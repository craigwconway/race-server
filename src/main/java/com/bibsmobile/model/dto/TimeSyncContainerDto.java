package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.TimeSyncEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This object is used to sync a set of raw time data between devices. In test mode,
 * no actual changes are applied. Otherwise, there are a set of {@link TimeSyncDto}
 * objects linked.
 * @author galen
 *
 */
public class TimeSyncContainerDto {

	/**
	 * @apiDefine timeSyncContainerDto
	 * @apiParam {String=TEST,TIMER,MANUAL} mode Type of data synched to server
	 * @apiParam {Number} timestamp ms timestamp from local system
	 * @apiParam {String} macAddress MAC Address from local system
	 * @apiParam {String} deviceName User configured device name for local system
	 * @apiParam {Number} syncEventId Id of event to sync into
	 * @apiParam {String} syncId code associated with this event used for synching
	 * @apiParam {String} timezone string containing local timezone
	 * @apiParam {Object[]} times A set of time objects to sync into this event
	 * @apiParam {Number} times.bib bib to sync into
	 * @apiParam {Number} times.time timestamp recorded
	 * @apiParam {Number=0,1,2,...} times.position Split position of reader. 0=start, 1=finish, 2 = Split1, 3= Split 2, etc.
	 */
	
	/**
	 * Represents the format of data synched.
	 */
	private TimeSyncEnum mode;
	
	/**
	 * Represents the timestamp from the origin system of the sync's creation
	 */
	private long timestamp;
	
	/**
	 * Last time synced in this packet.
	 */
	private long lastTime;
	
	/**
	 * Mac address of the synching system
	 */
	private String macAddress;
	
	/**
	 * Given device name of the syncing system
	 */
	private String deviceName;
	
	/**
	 * Event ID of the syncing system
	 */
	private Long syncEventId;

	/**
	 * Generated Sync code for the system
	 */
	private String syncId;
	
	/**
	 * String listing timezone of the syncing system
	 */
	private String timezone;
	
	/**
	 * A set of times collected from the syncing system
	 */
	private List<TimeSyncDto> times;

	/**
	 * @return return the mode
	 */
	public TimeSyncEnum getMode() {
		return mode;
	}
	
	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @return the lastTime
	 */
	public long getLastTime() {
		return lastTime;
	}
	
	/**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}
	
	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}
	
	/**
	 * @return the event ID to sync into
	 */
	public Long getSyncEventId() {
		return syncEventId;
	}

	/**
	 * @return the syncId
	 */
	public String getSyncId() {
		return syncId;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @return the times
	 */
	public List<TimeSyncDto> getTimes() {
		return times;
	}
	
	public void setTimes(List<TimeSyncDto> times) {
		this.times=times;
	}
}
