package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONSerializer;

@Configurable
@Entity
public class EventType {

    private String typeName;
    
    @NotNull
    private String distance;
    
    @NotNull
    private String racetype;
    
    private Long meters;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;
    
    private Long lowBib;
    
    private Long highBib;
    
    private boolean autoMapReg=false;

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("typeName", "startTime", "events");

    public static EntityManager entityManager() {
        EntityManager em = new EventType().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventTypes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventType o", Long.class).getSingleResult();
    }

    public static List<EventType> findAllEventTypes() {
        return entityManager().createQuery("SELECT o FROM EventType o", EventType.class).getResultList();
    }

    public static List<EventType> findAllEventTypes(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventType o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventType.class).getResultList();
    }

    public static EventType findEventType(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventType.class, id);
    }

    public static List<EventType> findEventTypeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventType o", EventType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<EventType> findEventTypesByEvent(Event event) {
        return entityManager().createQuery("SELECT o FROM EventType AS o WHERE o.event = :event", EventType.class).setParameter("event", event).getResultList();
    }    
    
    public static List<EventType> findEventTypeEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventType o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static String toJsonArray(Collection<EventType> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }   

    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            EventType attached = EventType.findEventType(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public EventType merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * ID of event type object. This is auto-generated by the database.
     * @return Event type ID.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * ID of the event type object. This is auto-generated by the database.
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Custom typename used in event type object.
     * @return typeName the custom typename.
     */
    public String getTypeName() {
        return this.typeName;
    }

    /**
     * Custom typename used in event type object.
     * @param typeName the custom typename to set.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Starting time of the event type. Timezone is contained in the event object
     * @param startTime the starting time to get.
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * Starting time of the event type. Timezone is contained in the event object
     * @param startTime the starting time to set.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Event containing this event type. Relationship cascades to eventtype.
     * @param event The parent event to get
     */
    public Event getEvent() {
        return this.event;
    }

    /**
     * Event containing this event type. Relationship cascades to eventtype.
     * @param event The parent event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
	 * Low bib number to associate with this event.
	 * @param lowBib Starting bib number to get (inclusive)
	 * 	 */
	public Long getLowBib() {
		return lowBib;
	}

	/**
	 * Low bib number to associate with this event.
	 * @param lowBib Starting bib number to set (inclusive)
	 */
	public void setLowBib(Long lowBib) {
		this.lowBib = lowBib;
	}

	/**
	 * High bib number to associate with this event.
	 * @param highBib ending bib number to get (inclusive)
	 */
	public Long getHighBib() {
		return highBib;
	}

	/**
	 * High bib number to associate with this event.
	 * @param highBib ending bib number to set (inclusive)
	 */
	public void setHighBib(Long highBib) {
		this.highBib = highBib;
	}

	/**
	 * Switch to determine whether registration items in this event type should be automatically mapped to athletes.
	 * If set, athletes will be automatically mapped if their bib number falls in the range of mapped bibs.
	 * @return the autoMapReg set for this event type (default false).
	 */
	public boolean isAutoMapReg() {
		return autoMapReg;
	}

	/**
	 * Switch to determine whether registration items in this event type should be automatically mapped to athletes.
	 * If set, athletes will be automatically mapped if their bib number falls in the range of mapped bibs.
	 * @param autoMapReg to set for this eventType (default false).
	 */
	public void setAutoMapReg(boolean autoMapReg) {
		this.autoMapReg = autoMapReg;
	}

	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	/**
	 * Distance string associated with this event type. The length in meters is autogenerated from this.
	 * @return the distance string associated with this event type.
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * Distance string set for this event type. The length in meters is autogenerated from this.
	 * @param distance the distance to set for this event type.
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * String describing the type of event (e.g. Running, Cycling, Triathlon).
	 * @return the racetype associated with this event.
	 */
	public String getRacetype() {
		return racetype;
	}

	/**
	 * String describing the type of event (e.g. Running, Cycling, Triathlon).
	 * @param racetype the racetype to set for this event.
	 */
	public void setRacetype(String racetype) {
		this.racetype = racetype;
	}

	/**
	 * Length in meters auto-generated from the distance string. This should only be used for automatic generation.
	 * @return meters - the distance associated with this event type.
	 */
	public Long getMeters() {
		return meters;
	}

	/**
	 * Length in meters auto-generated from the distance string. This should only be used for automatic generation.
	 * @param meters - the distance associated with this event type.
	 */
	public void setMeters(Long meters) {
		this.meters = meters;
	}
}
