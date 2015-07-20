package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
public class UserGroupUserAuthority {

    @MapsId("id")
    @ManyToOne
    @JoinColumn(name = "userGroupId", insertable = false, updatable = false)
    private UserGroup userGroup;

    @MapsId("id")
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "user_profile", insertable = false, updatable = false),
            @JoinColumn(name = "user_authorities", insertable = false, updatable = false) })
    private UserAuthorities userAuthorities;

    @EmbeddedId
    private UserGroupUserAuthorityID id;

    @Version
    @Column(name = "version")
    private Integer version;

    public UserGroupUserAuthorityID getId() {
        return this.id;
    }

    public void setId(UserGroupUserAuthorityID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public UserAuthorities getUserAuthorities() {
        return this.userAuthorities;
    }

    public void setUserAuthorities(UserAuthorities userAuthorities) {
        this.userAuthorities = userAuthorities;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserGroupUserAuthority fromJsonToUserGroupUserAuthority(String json) {
        return new JSONDeserializer<UserGroupUserAuthority>().use(null, UserGroupUserAuthority.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserGroupUserAuthority> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserGroupUserAuthority> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserGroupUserAuthority> fromJsonArrayToUserGroupUserAuthoritys(String json) {
        return new JSONDeserializer<List<UserGroupUserAuthority>>().use("values", UserGroupUserAuthority.class).deserialize(json);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("userGroup", "userAuthorities");

    public static EntityManager entityManager() {
        EntityManager em = new UserGroupUserAuthority().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countUserGroupUserAuthoritys() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserGroupUserAuthority o", Long.class).getSingleResult();
    }

    public static List<UserGroupUserAuthority> findAllUserGroupUserAuthoritys() {
        return entityManager().createQuery("SELECT o FROM UserGroupUserAuthority o", UserGroupUserAuthority.class).getResultList();
    }

    public static List<UserGroupUserAuthority> findAllUserGroupUserAuthoritys(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserGroupUserAuthority o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserGroupUserAuthority.class).getResultList();
    }

    public static UserGroupUserAuthority findUserGroupUserAuthority(UserGroupUserAuthorityID id) {
        if (id == null)
            return null;
        return entityManager().find(UserGroupUserAuthority.class, id);
    }

    public static List<UserGroupUserAuthority> findUserGroupUserAuthorityEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserGroupUserAuthority o", UserGroupUserAuthority.class).setFirstResult(firstResult).setMaxResults(maxResults)
                .getResultList();
    }

    public static List<UserGroupUserAuthority> findUserGroupUserAuthorityEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserGroupUserAuthority o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserGroupUserAuthority.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            UserGroupUserAuthority attached = UserGroupUserAuthority.findUserGroupUserAuthority(this.id);
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
    public UserGroupUserAuthority merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        UserGroupUserAuthority merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        UserGroupUserAuthority rhs = (UserGroupUserAuthority) obj;
        return new EqualsBuilder().append(this.id, rhs.id).append(this.userAuthorities, rhs.userAuthorities).append(this.userGroup, rhs.userGroup).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.id).append(this.userAuthorities).append(this.userGroup).toHashCode();
    }

    public static Long countFindUserGroupUserAuthoritysByUserAuthorities(UserAuthorities userAuthorities) {
        if (userAuthorities == null)
            throw new IllegalArgumentException("The userAuthorities argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserGroupUserAuthority AS o WHERE o.userAuthorities = :userAuthorities", Long.class);
        q.setParameter("userAuthorities", userAuthorities);
        return q.getSingleResult();
    }

    public static Long countFindUserGroupUserAuthoritysByUserGroup(UserGroup userGroup) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserGroupUserAuthority AS o WHERE o.userGroup = :userGroup", Long.class);
        q.setParameter("userGroup", userGroup);
        return q.getSingleResult();
    }

    public static Long countFindUserGroupUserAuthoritysByUserGroupAndUserAuthorities(UserGroup userGroup, UserAuthorities userAuthorities) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        if (userAuthorities == null)
            throw new IllegalArgumentException("The userAuthorities argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserGroupUserAuthority AS o WHERE o.userGroup = :userGroup AND o.userAuthorities = :userAuthorities", Long.class);
        q.setParameter("userGroup", userGroup);
        q.setParameter("userAuthorities", userAuthorities);
        return q.getSingleResult();
    }

    public static TypedQuery<UserGroupUserAuthority> findUserGroupUserAuthoritysByUserAuthorities(UserAuthorities userAuthorities) {
        if (userAuthorities == null)
            throw new IllegalArgumentException("The userAuthorities argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        TypedQuery<UserGroupUserAuthority> q = em.createQuery("SELECT o FROM UserGroupUserAuthority AS o WHERE o.userAuthorities = :userAuthorities", UserGroupUserAuthority.class);
        q.setParameter("userAuthorities", userAuthorities);
        return q;
    }

    public static TypedQuery<UserGroupUserAuthority> findUserGroupUserAuthoritysByUserAuthorities(UserAuthorities userAuthorities, String sortFieldName, String sortOrder) {
        if (userAuthorities == null)
            throw new IllegalArgumentException("The userAuthorities argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        String jpaQuery = "SELECT o FROM UserGroupUserAuthority AS o WHERE o.userAuthorities = :userAuthorities";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserGroupUserAuthority> q = em.createQuery(jpaQuery, UserGroupUserAuthority.class);
        q.setParameter("userAuthorities", userAuthorities);
        return q;
    }

    public static TypedQuery<UserGroupUserAuthority> findUserGroupUserAuthoritysByUserGroup(UserGroup userGroup) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        TypedQuery<UserGroupUserAuthority> q = em.createQuery("SELECT o FROM UserGroupUserAuthority AS o WHERE o.userGroup = :userGroup", UserGroupUserAuthority.class);
        q.setParameter("userGroup", userGroup);
        return q;
    }

    public static TypedQuery<UserGroupUserAuthority> findUserGroupUserAuthoritysByUserGroup(UserGroup userGroup, String sortFieldName, String sortOrder) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        String jpaQuery = "SELECT o FROM UserGroupUserAuthority AS o WHERE o.userGroup = :userGroup";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserGroupUserAuthority> q = em.createQuery(jpaQuery, UserGroupUserAuthority.class);
        q.setParameter("userGroup", userGroup);
        return q;
    }

    public static TypedQuery<UserGroupUserAuthority> findUserGroupUserAuthoritysByUserGroupAndUserAuthorities(UserGroup userGroup, UserAuthorities userAuthorities) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        if (userAuthorities == null)
            throw new IllegalArgumentException("The userAuthorities argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        TypedQuery<UserGroupUserAuthority> q = em.createQuery("SELECT o FROM UserGroupUserAuthority AS o WHERE o.userGroup = :userGroup AND o.userAuthorities = :userAuthorities",
                UserGroupUserAuthority.class);
        q.setParameter("userGroup", userGroup);
        q.setParameter("userAuthorities", userAuthorities);
        return q;
    }

    public static TypedQuery<UserGroupUserAuthority> findUserGroupUserAuthoritysByUserGroupAndUserAuthorities(UserGroup userGroup, UserAuthorities userAuthorities,
            String sortFieldName, String sortOrder) {
        if (userGroup == null)
            throw new IllegalArgumentException("The userGroup argument is required");
        if (userAuthorities == null)
            throw new IllegalArgumentException("The userAuthorities argument is required");
        EntityManager em = UserGroupUserAuthority.entityManager();
        String jpaQuery = "SELECT o FROM UserGroupUserAuthority AS o WHERE o.userGroup = :userGroup AND o.userAuthorities = :userAuthorities";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserGroupUserAuthority> q = em.createQuery(jpaQuery, UserGroupUserAuthority.class);
        q.setParameter("userGroup", userGroup);
        q.setParameter("userAuthorities", userAuthorities);
        return q;
    }
}
