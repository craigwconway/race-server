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

	private TimeSyncEnum mode;
	
	private long timestamp;
	
	private String macAddress;

	private String syncId;
	
	private String timezone;
	
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
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
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
}
