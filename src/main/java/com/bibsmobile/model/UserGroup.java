package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

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
// company or team
public class UserGroup {

    /**
     */
    private String name;

    private int bibWrites;

    /**
     */
    @NotNull
    @Enumerated
    private UserGroupType groupType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userGroup", cascade = CascadeType.ALL)
    private Set<UserGroupUserAuthority> userGroupUserAuthorities;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "userGroup")
    private List<EventUserGroup> eventUserGroups = new ArrayList<>();

    public static Long countFindUserGroupsByGroupType(UserGroupType groupType) {
        if (groupType == null)
            throw new IllegalArgumentException("The groupType argument is required");
        EntityManager em = UserGroup.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserGroup AS o WHERE o.groupType = :groupType", Long.class);
        q.setParameter("groupType", groupType);
        return q.getSingleResult();
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("name", "bibWrites", "groupType", "userGroupUserAuthorities", "eventUserGroups");

    public static EntityManager entityManager() {
        EntityManager em = new UserGroup().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countUserGroups() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserGroup o", Long.class).getSingleResult();
    }

    public static List<UserGroup> findAllUserGroups() {
        return entityManager().createQuery("SELECT o FROM UserGroup o", UserGroup.class).getResultList();
    }

    public static List<UserGroup> findAllUserGroups(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserGroup o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserGroup.class).getResultList();
    }

    public static UserGroup findUserGroup(Long id) {
        if (id == null)
            return null;
        return entityManager().find(UserGroup.class, id);
    }

    public static List<UserGroup> findUserGroupEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserGroup o", UserGroup.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<UserGroup> findUserGroupEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserGroup o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserGroup.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            UserGroup attached = UserGroup.findUserGroup(this.id);
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
    public UserGroup merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        UserGroup merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        UserGroup rhs = (UserGroup) obj;
        return new EqualsBuilder().append(this.bibWrites, rhs.bibWrites).append(this.groupType, rhs.groupType).append(this.id, rhs.id).append(this.name, rhs.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.bibWrites).append(this.groupType).append(this.id).append(this.name).toHashCode();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBibWrites() {
        return this.bibWrites;
    }

    public void setBibWrites(int bibWrites) {
        this.bibWrites = bibWrites;
    }

    public UserGroupType getGroupType() {
        return this.groupType;
    }

    public void setGroupType(UserGroupType groupType) {
        this.groupType = groupType;
    }

    public Set<UserGroupUserAuthority> getUserGroupUserAuthorities() {
        return this.userGroupUserAuthorities;
    }

    public void setUserGroupUserAuthorities(Set<UserGroupUserAuthority> userGroupUserAuthorities) {
        this.userGroupUserAuthorities = userGroupUserAuthorities;
    }

    public List<EventUserGroup> getEventUserGroups() {
        return this.eventUserGroups;
    }

    public void setEventUserGroups(List<EventUserGroup> eventUserGroups) {
        this.eventUserGroups = eventUserGroups;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserGroup fromJsonToUserGroup(String json) {
        return new JSONDeserializer<UserGroup>().use(null, UserGroup.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserGroup> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserGroup> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserGroup> fromJsonArrayToUserGroups(String json) {
        return new JSONDeserializer<List<UserGroup>>().use("values", UserGroup.class).deserialize(json);
    }

    public static Long countFindUserGroupsByNameEquals(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        EntityManager em = UserGroup.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserGroup AS o WHERE o.name = :name", Long.class);
        q.setParameter("name", name);
        return q.getSingleResult();
    }

    public static TypedQuery<UserGroup> findUserGroupsByGroupType(UserGroupType groupType) {
        if (groupType == null)
            throw new IllegalArgumentException("The groupType argument is required");
        EntityManager em = UserGroup.entityManager();
        TypedQuery<UserGroup> q = em.createQuery("SELECT o FROM UserGroup AS o WHERE o.groupType = :groupType", UserGroup.class);
        q.setParameter("groupType", groupType);
        return q;
    }

    public static TypedQuery<UserGroup> findUserGroupsByGroupType(UserGroupType groupType, String sortFieldName, String sortOrder) {
        if (groupType == null)
            throw new IllegalArgumentException("The groupType argument is required");
        EntityManager em = UserGroup.entityManager();
        String jpaQuery = "SELECT o FROM UserGroup AS o WHERE o.groupType = :groupType";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserGroup> q = em.createQuery(jpaQuery, UserGroup.class);
        q.setParameter("groupType", groupType);
        return q;
    }

    public static TypedQuery<UserGroup> findUserGroupsByNameEquals(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        EntityManager em = UserGroup.entityManager();
        TypedQuery<UserGroup> q = em.createQuery("SELECT o FROM UserGroup AS o WHERE o.name = :name", UserGroup.class);
        q.setParameter("name", name);
        return q;
    }

    public static TypedQuery<UserGroup> findUserGroupsByNameEquals(String name, String sortFieldName, String sortOrder) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        EntityManager em = UserGroup.entityManager();
        String jpaQuery = "SELECT o FROM UserGroup AS o WHERE o.name = :name";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserGroup> q = em.createQuery(jpaQuery, UserGroup.class);
        q.setParameter("name", name);
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
