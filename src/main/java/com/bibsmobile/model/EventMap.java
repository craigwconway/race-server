package com.bibsmobile.model;

import java.util.Arrays;
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

@Configurable
@Entity
public class EventMap {

    /**
     */
    private String url;

    @ManyToOne
    private Event event;

    @JSON(include = false)
    public Event getEvent() {
        return this.event;
    }

    public static TypedQuery<EventMap> findEventMapsByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventMap> q = em.createQuery("SELECT em FROM EventMap AS em WHERE em.event.id = :eventId", EventMap.class);
        q.setParameter("eventId", eventId);
        return q;
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

    public static EventMap fromJsonToEventMap(String json) {
        return new JSONDeserializer<EventMap>().use(null, EventMap.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventMap> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventMap> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<EventMap> fromJsonArrayToEventMaps(String json) {
        return new JSONDeserializer<List<EventMap>>().use("values", EventMap.class).deserialize(json);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("url", "event");

    public static EntityManager entityManager() {
        EntityManager em = new EventMap().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventMaps() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventMap o", Long.class).getSingleResult();
    }

    public static List<EventMap> findAllEventMaps() {
        return entityManager().createQuery("SELECT o FROM EventMap o", EventMap.class).getResultList();
    }

    public static List<EventMap> findAllEventMaps(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventMap o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventMap.class).getResultList();
    }

    public static EventMap findEventMap(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventMap.class, id);
    }

    public static List<EventMap> findEventMapEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventMap o", EventMap.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<EventMap> findEventMapEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventMap o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventMap.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventMap attached = EventMap.findEventMap(this.id);
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
    public EventMap merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventMap merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static Long countFindEventMapsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventMap.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventMap AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

    public static TypedQuery<EventMap> findEventMapsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventMap.entityManager();
        TypedQuery<EventMap> q = em.createQuery("SELECT o FROM EventMap AS o WHERE o.event = :event", EventMap.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventMap> findEventMapsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventMap.entityManager();
        String jpaQuery = "SELECT o FROM EventMap AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventMap> q = em.createQuery(jpaQuery, EventMap.class);
        q.setParameter("event", event);
        return q;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
}
