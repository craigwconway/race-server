/**
 * 
 */
package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.SyncReport;
import com.bibsmobile.model.TimeSyncEnum;
import com.bibsmobile.model.TimeSyncStatusEnum;
import com.bibsmobile.model.dto.TimeSyncContainerDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This represents a status report for a time synchronization to a local device.
 * It is contained in a particular {@link Event} object.
 * @author galen {gedanziger}
 *
 */
public class SyncReportDto {
	/**
	 * Autogenerated id for search/lookup.
	 */
    private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date received;
	
	/**
	 * {@link TimeSyncEnum} object denoting the type of data transfer.
	 */
	@Enumerated
	private TimeSyncEnum mode;
	
	/**
	 * Custom user defined device name for synchronization.
	 */
	private String deviceName;
	
	/**
	 * Number of time objects synced.
	 */
	private int numResults;
	
	/**
	 * Origin ip address of the device performing the synchronization.
	 */
	private String deviceIpAddress;
	
	/**
	 * {@link TimeSyncStatusEnum} containing a status code for the sync.
	 */
	private TimeSyncStatusEnum status;
	
	/**
	 * Default Contructor
	 */
	SyncReportDto() {
		
	}
	
	/**
	 * Create a SyncReport object from an incoming data object
	 * @param syncObj Incoming {@link com.bibsmobile.model.dto.TimeSyncContainerDto} object to generate report from.
	 * @param address Origin IP address of request
	 * @param event Event to sync into
	 */
	public SyncReportDto(SyncReport syncReport) {
		this.deviceIpAddress = syncReport.getDeviceIpAddress();
		if(syncReport.getDevice() != null) {
			this.deviceName = syncReport.getDevice().getDeviceName();
		}
		this.mode = syncReport.getMode();
		this.numResults = syncReport.getNumResults();
		this.received = syncReport.getReceived();
		this.status = syncReport.getStatus();
		this.id = syncReport.getId();
	}
	
	public static String fromSyncReportsToDtoArray(Collection<SyncReport> reports ) {
		List <SyncReportDto> dtos = new ArrayList <SyncReportDto>();
		for(SyncReport report:reports) {
			dtos.add(new SyncReportDto(report));
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

	public static List<SyncReportDto> fromSyncReportsToRawDtoArray(Collection<SyncReport> reports ) {
		List <SyncReportDto> dtos = new ArrayList <SyncReportDto>();
		for(SyncReport report:reports) {
			dtos.add(new SyncReportDto(report));
		}
		return dtos;
		}	
	
	/**
	 * ID of series, autogenerated on persist.
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * ID of series, autogenerated on persist.
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the mode
	 */
	public TimeSyncEnum getMode() {
		return mode;
	}

	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @return the numResults
	 */
	public int getNumResults() {
		return numResults;
	}

	/**
	 * @return the deviceIpAddress
	 */
	public String getDeviceIpAddress() {
		return deviceIpAddress;
	}
	
	public Date getReceived() {
		return received;
	}

	/**
	 * @return the status
	 */
	public TimeSyncStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(TimeSyncEnum mode) {
		this.mode = mode;
	}

	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @param deviceIpAddress the deviceIpAddress to set
	 */
	public void setDeviceIpAddress(String deviceIpAddress) {
		this.deviceIpAddress = deviceIpAddress;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(TimeSyncStatusEnum status) {
		this.status = status;
	}
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}