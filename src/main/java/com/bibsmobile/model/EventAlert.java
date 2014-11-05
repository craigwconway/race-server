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

@Entity
@Configurable
public class EventAlert {

    /**
     */
    private String text;

    @ManyToOne
    private Event event;

    public static TypedQuery<EventAlert> findEventAlertsByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventAlert> q = em.createQuery("SELECT ea FROM EventAlert AS ea WHERE ea.event.id = :eventId", EventAlert.class);
        q.setParameter("eventId", eventId);
        return q;
    }

    @JSON(include = false)
    public Event getEvent() {
        return this.event;
    }

    public static Long countFindEventAlertsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventAlert.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventAlert AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

    public static TypedQuery<EventAlert> findEventAlertsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventAlert.entityManager();
        TypedQuery<EventAlert> q = em.createQuery("SELECT o FROM EventAlert AS o WHERE o.event = :event", EventAlert.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventAlert> findEventAlertsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventAlert.entityManager();
        String jpaQuery = "SELECT o FROM EventAlert AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventAlert> q = em.createQuery(jpaQuery, EventAlert.class);
        q.setParameter("event", event);
        return q;
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

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("text", "event");

    public static EntityManager entityManager() {
        EntityManager em = new EventAlert().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventAlerts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventAlert o", Long.class).getSingleResult();
    }

    public static List<EventAlert> findAllEventAlerts() {
        return entityManager().createQuery("SELECT o FROM EventAlert o", EventAlert.class).getResultList();
    }

    public static List<EventAlert> findAllEventAlerts(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventAlert o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventAlert.class).getResultList();
    }

    public static EventAlert findEventAlert(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventAlert.class, id);
    }

    public static List<EventAlert> findEventAlertEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventAlert o", EventAlert.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<EventAlert> findEventAlertEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventAlert o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventAlert.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventAlert attached = EventAlert.findEventAlert(this.id);
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
    public EventAlert merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventAlert merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static EventAlert fromJsonToEventAlert(String json) {
        return new JSONDeserializer<EventAlert>().use(null, EventAlert.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventAlert> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventAlert> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<EventAlert> fromJsonArrayToEventAlerts(String json) {
        return new JSONDeserializer<List<EventAlert>>().use("values", EventAlert.class).deserialize(json);
    }
}
