package com.bibsmobile.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class EventPhoto {

    /**
     */
    private String url;

    @ManyToOne
    private Event event;

    @JSON(include = false)
    public Event getEvent() {
        return this.event;
    }

    public static TypedQuery<EventPhoto> findEventPhotosByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventPhoto> q = em.createQuery("SELECT ep FROM EventPhoto AS ep WHERE ep.event.id = :eventId", EventPhoto.class);
        q.setParameter("eventId", eventId);
        return q;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("url", "event");

    public static final EntityManager entityManager() {
        EntityManager em = new EventPhoto().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventPhotoes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventPhoto o", Long.class).getSingleResult();
    }

    public static List<EventPhoto> findAllEventPhotoes() {
        return entityManager().createQuery("SELECT o FROM EventPhoto o", EventPhoto.class).getResultList();
    }

    public static List<EventPhoto> findAllEventPhotoes(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventPhoto o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventPhoto.class).getResultList();
    }

    public static EventPhoto findEventPhoto(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventPhoto.class, id);
    }

    public static List<EventPhoto> findEventPhotoEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventPhoto o", EventPhoto.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<EventPhoto> findEventPhotoEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventPhoto o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventPhoto.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventPhoto attached = EventPhoto.findEventPhoto(this.id);
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
    public EventPhoto merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventPhoto merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static EventPhoto fromJsonToEventPhoto(String json) {
        return new JSONDeserializer<EventPhoto>().use(null, EventPhoto.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventPhoto> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventPhoto> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<EventPhoto> fromJsonArrayToEventPhotoes(String json) {
        return new JSONDeserializer<List<EventPhoto>>().use("values", EventPhoto.class).deserialize(json);
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

    public static Long countFindEventPhotoesByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventPhoto.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventPhoto AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

    public static TypedQuery<EventPhoto> findEventPhotoesByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventPhoto.entityManager();
        TypedQuery<EventPhoto> q = em.createQuery("SELECT o FROM EventPhoto AS o WHERE o.event = :event", EventPhoto.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventPhoto> findEventPhotoesByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventPhoto.entityManager();
        String jpaQuery = "SELECT o FROM EventPhoto AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventPhoto> q = em.createQuery(jpaQuery, EventPhoto.class);
        q.setParameter("event", event);
        return q;
    }
}
