package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
public class EventCartItemPriceChange {

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date startDate;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date endDate;

    private int lowAgeThreshold;
    private int highAgeThreshold;
    private String gender;
    private boolean team;

    /**
     */
    @ManyToOne
    private EventCartItem eventCartItem;

    /**
     */
    private double price;

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public EventCartItem getEventCartItem() {
        return this.eventCartItem;
    }

    public void setEventCartItem(EventCartItem eventCartItem) {
        this.eventCartItem = eventCartItem;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLowAgeThreshold() {
        return lowAgeThreshold;
    }

    public void setLowAgeThreshold(int lowAgeThreshold) {
        this.lowAgeThreshold = lowAgeThreshold;
    }

    public int getHighAgeThreshold() {
        return highAgeThreshold;
    }

    public void setHighAgeThreshold(int highAgeThreshold) {
        this.highAgeThreshold = highAgeThreshold;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isTeam() {
        return team;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static EventCartItemPriceChange fromJsonToEventCartItemPriceChange(String json) {
        return new JSONDeserializer<EventCartItemPriceChange>().use(null, EventCartItemPriceChange.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventCartItemPriceChange> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventCartItemPriceChange> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<EventCartItemPriceChange> fromJsonArrayToEventCartItemPriceChanges(String json) {
        return new JSONDeserializer<List<EventCartItemPriceChange>>().use("values", EventCartItemPriceChange.class).deserialize(json);
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

    public static Long countFindEventCartItemPriceChangesByEventCartItem(EventCartItem eventCartItem) {
        if (eventCartItem == null)
            throw new IllegalArgumentException("The eventCartItem argument is required");
        EntityManager em = EventCartItemPriceChange.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventCartItemPriceChange AS o WHERE o.eventCartItem = :eventCartItem", Long.class);
        q.setParameter("eventCartItem", eventCartItem);
        return q.getSingleResult();
    }

    public static TypedQuery<EventCartItemPriceChange> findEventCartItemPriceChangesByEventCartItem(EventCartItem eventCartItem) {
        if (eventCartItem == null)
            throw new IllegalArgumentException("The eventCartItem argument is required");
        EntityManager em = EventCartItemPriceChange.entityManager();
        TypedQuery<EventCartItemPriceChange> q = em.createQuery("SELECT o FROM EventCartItemPriceChange AS o WHERE o.eventCartItem = :eventCartItem",
                EventCartItemPriceChange.class);
        q.setParameter("eventCartItem", eventCartItem);
        return q;
    }

    public static TypedQuery<EventCartItemPriceChange> findEventCartItemPriceChangesByEventCartItem(EventCartItem eventCartItem, String sortFieldName, String sortOrder) {
        if (eventCartItem == null)
            throw new IllegalArgumentException("The eventCartItem argument is required");
        EntityManager em = EventCartItemPriceChange.entityManager();
        String jpaQuery = "SELECT o FROM EventCartItemPriceChange AS o WHERE o.eventCartItem = :eventCartItem";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventCartItemPriceChange> q = em.createQuery(jpaQuery, EventCartItemPriceChange.class);
        q.setParameter("eventCartItem", eventCartItem);
        return q;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("startDate", "endDate", "eventCartItem", "price");

    public static EntityManager entityManager() {
        EntityManager em = new EventCartItemPriceChange().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventCartItemPriceChanges() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventCartItemPriceChange o", Long.class).getSingleResult();
    }

    public static List<EventCartItemPriceChange> findAllEventCartItemPriceChanges() {
        return entityManager().createQuery("SELECT o FROM EventCartItemPriceChange o", EventCartItemPriceChange.class).getResultList();
    }

    public static List<EventCartItemPriceChange> findAllEventCartItemPriceChanges(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventCartItemPriceChange o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventCartItemPriceChange.class).getResultList();
    }

    public static EventCartItemPriceChange findEventCartItemPriceChange(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventCartItemPriceChange.class, id);
    }

    public static List<EventCartItemPriceChange> findEventCartItemPriceChangeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventCartItemPriceChange o", EventCartItemPriceChange.class).setFirstResult(firstResult).setMaxResults(maxResults)
                .getResultList();
    }

    public static List<EventCartItemPriceChange> findEventCartItemPriceChangeEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventCartItemPriceChange o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventCartItemPriceChange.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventCartItemPriceChange attached = EventCartItemPriceChange.findEventCartItemPriceChange(this.id);
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
    public EventCartItemPriceChange merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventCartItemPriceChange merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
