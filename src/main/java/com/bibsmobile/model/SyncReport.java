/**
 * 
 */
package com.bibsmobile.model;

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
@Entity
@Configurable
public class SyncReport {
	/**
	 * Autogenerated id for search/lookup.
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	/**
	 * {@link Event} Object synched into.
	 */
	@ManyToOne
	@JsonIgnore
	private Event event;
	
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
	SyncReport() {
		
	}
	
	/**
	 * Create a SyncReport object from an incoming data object
	 * @param syncObj Incoming {@link com.bibsmobile.model.dto.TimeSyncContainerDto} object to generate report from.
	 * @param address Origin IP address of request
	 * @param event Event to sync into
	 */
	public SyncReport(TimeSyncContainerDto syncObj, Event event, String address) {
		this.deviceIpAddress = address;
		this.deviceName = syncObj.getDeviceName();
		this.mode = syncObj.getMode();
		this.event = event;
		this.numResults = syncObj.getTimes().size();
		this.received = new Date();
	}
	
	/**
	 * Used by hibernate
	 */
	@PersistenceContext
    transient EntityManager entityManager;

	/**
	 * Used by hibernate
	 */
	public static final EntityManager entityManager() {
        EntityManager em = new SyncReport().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SyncReport attached = SyncReport.findSyncReport(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public SyncReport merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SyncReport merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }	
	
	/**
	 * Find a syncreport with a particular id.
	 * Returns null if id is null or object does not exist.
	 * @param id id of sync report to retrieve
	 * @return SyncReport object from database.
	 */
	public static SyncReport findSyncReport(Long id) {
        if (id == null) return null;
        return entityManager().find(SyncReport.class, id);
    }

    /**
     * Search for sync reports by a particular event with pagination.
     * @param event event to get sync reports from
     * @param page page number, indexed from 1
     * @param size page size
     * @return A List of SyncReport objects in this event
     */
    public static List<SyncReport> findLatestReportsByEvent(Event event, int page, int size) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = SyncReport.entityManager();
        TypedQuery<SyncReport> q = em.createQuery("SELECT o FROM SyncReport AS o WHERE o.event = :event", SyncReport.class);
        q.setParameter("event", event);
        q.setFirstResult((page-1) * size);
        q.setMaxResults(size);
        return q.getResultList();
    } 

    /**
     * Search for sync reports by a particular event.
     * @param event event to get sync reports from
     * @return A TypedQuery object. To get all results, run with .getResultList()
     */
    public static TypedQuery<SyncReport> findSyncReportsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = SyncReport.entityManager();
        TypedQuery<SyncReport> q = em.createQuery("SELECT o FROM SyncReport AS o WHERE o.event = :event", SyncReport.class);
        q.setParameter("event", event);
        return q;
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
	 * @return the event
	 */
	@JsonIgnore
	public Event getEvent() {
		return event;
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
	 * @param event the event to set
	 */
	@JsonIgnore
	public void setEvent(Event event) {
		this.event = event;
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
