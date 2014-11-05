package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class EventUserGroup {

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventId", referencedColumnName = "id", insertable = false, updatable = false)
    private Event event;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userGroupId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserGroup userGroup;

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @EmbeddedId
    private EventUserGroupId id;

    @Version
    @Column(name = "version")
    private Integer version;

    public EventUserGroupId getId() {
        return this.id;
    }

    public void setId(EventUserGroupId id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        EventUserGroup rhs = (EventUserGroup) obj;
        return new EqualsBuilder().append(this.event, rhs.event).append(this.id, rhs.id).append(this.userGroup, rhs.userGroup).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.event).append(this.id).append(this.userGroup).toHashCode();
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("event", "userGroup");

    public static EntityManager entityManager() {
        EntityManager em = new EventUserGroup().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEventUserGroups() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventUserGroup o", Long.class).getSingleResult();
    }

    public static List<EventUserGroup> findAllEventUserGroups() {
        return entityManager().createQuery("SELECT o FROM EventUserGroup o", EventUserGroup.class).getResultList();
    }

    public static List<EventUserGroup> findAllEventUserGroups(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventUserGroup o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventUserGroup.class).getResultList();
    }

    public static EventUserGroup findEventUserGroup(EventUserGroupId id) {
        if (id == null)
            return null;
        return entityManager().find(EventUserGroup.class, id);
    }

    public static List<EventUserGroup> findEventUserGroupEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventUserGroup o", EventUserGroup.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<EventUserGroup> findEventUserGroupEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM EventUserGroup o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, EventUserGroup.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            EventUserGroup attached = EventUserGroup.findEventUserGroup(this.id);
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
    public EventUserGroup merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventUserGroup merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static Long countFindEventUserGroupsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventUserGroup.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventUserGroup AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

    public static Long countFindEventUserGroupsByUserGroup(UserGroup userGroup) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        EntityManager em = EventUserGroup.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM EventUserGroup AS o WHERE o.userGroup = :userGroup", Long.class);
        q.setParameter("userGroup", userGroup);
        return q.getSingleResult();
    }

    public static TypedQuery<EventUserGroup> findEventUserGroupsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventUserGroup.entityManager();
        TypedQuery<EventUserGroup> q = em.createQuery("SELECT o FROM EventUserGroup AS o WHERE o.event = :event", EventUserGroup.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventUserGroup> findEventUserGroupsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventUserGroup.entityManager();
        String jpaQuery = "SELECT o FROM EventUserGroup AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventUserGroup> q = em.createQuery(jpaQuery, EventUserGroup.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<EventUserGroup> findEventUserGroupsByUserGroup(UserGroup userGroup) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        EntityManager em = EventUserGroup.entityManager();
        TypedQuery<EventUserGroup> q = em.createQuery("SELECT o FROM EventUserGroup AS o WHERE o.userGroup = :userGroup", EventUserGroup.class);
        q.setParameter("userGroup", userGroup);
        return q;
    }

    public static TypedQuery<EventUserGroup> findEventUserGroupsByUserGroup(UserGroup userGroup, String sortFieldName, String sortOrder) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        EntityManager em = EventUserGroup.entityManager();
        String jpaQuery = "SELECT o FROM EventUserGroup AS o WHERE o.userGroup = :userGroup";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventUserGroup> q = em.createQuery(jpaQuery, EventUserGroup.class);
        q.setParameter("userGroup", userGroup);
        return q;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static EventUserGroup fromJsonToEventUserGroup(String json) {
        return new JSONDeserializer<EventUserGroup>().use(null, EventUserGroup.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventUserGroup> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventUserGroup> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<EventUserGroup> fromJsonArrayToEventUserGroups(String json) {
        return new JSONDeserializer<List<EventUserGroup>>().use("values", EventUserGroup.class).deserialize(json);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
