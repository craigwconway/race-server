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
	 * @apiSuccess (200) {Number} timestamp ms timestamp of recorded time
	 * @apiSuccessExample Single Time
	 * {
	 * 	"bib":1,
	 * 	"timestamp":144244242
	 * }
	 */
	
	
	public TimeSyncDto(RaceResult result, long position, long time, Long eventTypeId) {
		this.bib = result.getBib();
		this.time = time;
		this.position = position;
		this.eventTypeId = eventTypeId;
	}

	public TimeSyncDto(long bib, long position, long time, Long eventTypeId) {
		this.bib = bib;
		this.time = time;
		this.position = position;
		this.eventTypeId = eventTypeId;
	}
	
	private long bib;
	private long position;
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

	public static String fromRaceResultToDto(RaceResult result, long position, long time, Long eventTypeId ) {
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
	public long getPosition() {
		return position;
	}
	
	/**
	 * @return ID of cloud based event type to sync into
	 */
	public Long getEventTypeId() {
		return eventTypeId;
	}
}
