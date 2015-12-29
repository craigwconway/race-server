package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This object represents a single time recieved in a time sync packet.
 * @author galen
 *
 */
public class TimeSyncDto {
	
	/**
	 * @apiDefine timeSyncDto
	 * @apiSuccess (200) {Number} bib Bib number of result
	 * @apiSuccess (200) {Number} eventTypeId Id of cloud-based event type to sync into
	 * @apiSuccess (200) {Number} time ms timestamp of recorded time
	 * @apiSuccessExample Single Time
	 * {
	 * 	"bib":1,
	 * 	"time":144244242
	 * }
	 */
	
	public TimeSyncDto() {
		
	}
	public TimeSyncDto(RaceResult result, int position, long time, Long eventTypeId) {
		this.bib = result.getBib();
		this.time = time;
		this.position = position;
		this.eventTypeId = eventTypeId;
	}

	public TimeSyncDto(long bib, int position, long time, Long eventTypeId) {
		this.bib = bib;
		this.time = time;
		this.position = position;
		this.eventTypeId = eventTypeId;
	}
	
	private long bib;
	private int position;
	private long time;
	private Long eventTypeId;
		
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

	public static String fromRaceResultToDto(RaceResult result, int position, long time, Long eventTypeId ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new TimeSyncDto(result, position, time, eventTypeId));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the bib number of runner
	 */
	public long getBib() {
		return bib;
	}
	
	/**
	 * @return the time in ms timestamp
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * @return Split position of reader. 0=start, 1=finish, 2=split1, 3=split2, etc.
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * @return ID of cloud based event type to sync into
	 */
	public Long getEventTypeId() {
		return eventTypeId;
	}

	/**
	 * @param bib the bib to set
	 */
	public void setBib(long bib) {
		this.bib = bib;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @param eventTypeId the eventTypeId to set
	 */
	public void setEventTypeId(Long eventTypeId) {
		this.eventTypeId = eventTypeId;
	}
}
