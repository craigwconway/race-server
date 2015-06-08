package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event events) {
        this.event = events;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * @return the racetype
	 */
	public String getRacetype() {
		return racetype;
	}

	/**
	 * @param racetype the racetype to set
	 */
	public void setRacetype(String racetype) {
		this.racetype = racetype;
	}

	/**
	 * @return the meters
	 */
	public Long getMeters() {
		return meters;
	}

	/**
	 * @param meters the meters to set
	 */
	public void setMeters(Long meters) {
		this.meters = meters;
	}
}
