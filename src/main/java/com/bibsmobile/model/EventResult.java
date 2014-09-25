package com.bibsmobile.model;
import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;
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

@Configurable
@Entity
public class EventResult {

    /**
     */
    private String text;

    @ManyToOne
    private Event event;

    @JSON(include = false)
    public Event getEvent(){
        return event;
    }

    public static TypedQuery<EventResult> findEventResultsByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventResult> q = em.createQuery("SELECT er FROM EventResult AS er WHERE er.event.id = :eventId", EventResult.class);
        q.setParameter("eventId", eventId);
        return q;
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static EventResult fromJsonToEventResult(String json) {
        return new JSONDeserializer<EventResult>()
        .use(null, EventResult.class).deserialize(json);
    }

	public static String toJsonArray(Collection<EventResult> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<EventResult> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<EventResult> fromJsonArrayToEventResults(String json) {
        return new JSONDeserializer<List<EventResult>>()
        .use("values", EventResult.class).deserialize(json);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("text", "event");

	public static final EntityManager entityManager() {
        EntityManager em = new EventResult().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countEventResults() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventResult o", Long.class).getSingleResult();
    }

	public static List<EventResult> findAllEventResults() {
        return entityManager().createQuery("SELECT o FROM EventResult o", EventResult.class).getResultList();
    }

	public static List<EventResult> findAllEventResults(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventResult o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventResult.class).getResultList();
    }

	public static EventResult findEventResult(Long id) {
        if (id == null) return null;
        return entityManager().find(EventResult.class, id);
    }

	public static List<EventResult> findEventResultEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventResult o", EventResult.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<EventResult> findEventResultEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventResult o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventResult.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventResult attached = EventResult.findEventResult(this.id);
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
    public EventResult merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        EventResult merged = this.entityManager.merge(this);
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

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public static Long countFindEventResultsByEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventResult.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM EventResult AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return ((Long) q.getSingleResult());
    }

	public static TypedQuery<EventResult> findEventResultsByEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventResult.entityManager();
        TypedQuery<EventResult> q = em.createQuery("SELECT o FROM EventResult AS o WHERE o.event = :event", EventResult.class);
        q.setParameter("event", event);
        return q;
    }

	public static TypedQuery<EventResult> findEventResultsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventResult.entityManager();
        String jpaQuery = "SELECT o FROM EventResult AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventResult> q = em.createQuery(jpaQuery, EventResult.class);
        q.setParameter("event", event);
        return q;
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
}
