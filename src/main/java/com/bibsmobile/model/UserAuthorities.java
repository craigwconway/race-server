package com.bibsmobile.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
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
public class UserAuthorities implements Serializable {

    private static final long serialVersionUID = 1L;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile", referencedColumnName = "id", insertable = false, updatable = false)
    private UserProfile userProfile;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_authorities", referencedColumnName = "id", insertable = false, updatable = false)
    private UserAuthority userAuthority;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userAuthorities", cascade = CascadeType.ALL)
    private Set<UserGroupUserAuthority> userGroupUserAuthorities;

    @EmbeddedId
    private UserAuthoritiesID id;

    @Version
    @Column(name = "version")
    private Integer version;

    public UserAuthoritiesID getId() {
        return this.id;
    }

    public void setId(UserAuthoritiesID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserAuthority getUserAuthority() {
        return this.userAuthority;
    }

    public void setUserAuthority(UserAuthority userAuthority) {
        this.userAuthority = userAuthority;
    }

    public Set<UserGroupUserAuthority> getUserGroupUserAuthorities() {
        return this.userGroupUserAuthorities;
    }

    public void setUserGroupUserAuthorities(Set<UserGroupUserAuthority> userGroupUserAuthorities) {
        this.userGroupUserAuthorities = userGroupUserAuthorities;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("userProfile", "userAuthority", "userGroupUserAuthorities");

    public static final EntityManager entityManager() {
        EntityManager em = new UserAuthorities().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countUserAuthoritieses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserAuthorities o", Long.class).getSingleResult();
    }

    public static List<UserAuthorities> findAllUserAuthoritieses() {
        return entityManager().createQuery("SELECT o FROM UserAuthorities o", UserAuthorities.class).getResultList();
    }

    public static List<UserAuthorities> findAllUserAuthoritieses(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserAuthorities o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserAuthorities.class).getResultList();
    }

    public static UserAuthorities findUserAuthorities(UserAuthoritiesID id) {
        if (id == null)
            return null;
        return entityManager().find(UserAuthorities.class, id);
    }

    public static List<UserAuthorities> findUserAuthoritiesEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserAuthorities o", UserAuthorities.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<UserAuthorities> findUserAuthoritiesEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserAuthorities o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserAuthorities.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            UserAuthorities attached = UserAuthorities.findUserAuthorities(this.id);
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
    public UserAuthorities merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        UserAuthorities merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserAuthorities)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        UserAuthorities rhs = (UserAuthorities) obj;
        return new EqualsBuilder().append(this.id, rhs.id).append(this.userAuthority, rhs.userAuthority).append(this.userProfile, rhs.userProfile).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.id).append(this.userAuthority).append(this.userProfile).toHashCode();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserAuthorities fromJsonToUserAuthorities(String json) {
        return new JSONDeserializer<UserAuthorities>().use(null, UserAuthorities.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserAuthorities> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserAuthorities> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserAuthorities> fromJsonArrayToUserAuthoritieses(String json) {
        return new JSONDeserializer<List<UserAuthorities>>().use("values", UserAuthorities.class).deserialize(json);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static Long countFindUserAuthoritiesesByUserAuthority(UserAuthority userAuthority) {
        if (userAuthority == null)
            throw new IllegalArgumentException("The userAuthority argument is required");
        EntityManager em = UserAuthorities.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserAuthorities AS o WHERE o.userAuthority = :userAuthority", Long.class);
        q.setParameter("userAuthority", userAuthority);
        return q.getSingleResult();
    }

    public static Long countFindUserAuthoritiesesByUserProfile(UserProfile userProfile) {
        if (userProfile == null)
            throw new IllegalArgumentException("The userProfile argument is required");
        EntityManager em = UserAuthorities.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserAuthorities AS o WHERE o.userProfile = :userProfile", Long.class);
        q.setParameter("userProfile", userProfile);
        return q.getSingleResult();
    }

    public static TypedQuery<UserAuthorities> findUserAuthoritiesesByUserAuthority(UserAuthority userAuthority) {
        if (userAuthority == null)
            throw new IllegalArgumentException("The userAuthority argument is required");
        EntityManager em = UserAuthorities.entityManager();
        TypedQuery<UserAuthorities> q = em.createQuery("SELECT o FROM UserAuthorities AS o WHERE o.userAuthority = :userAuthority", UserAuthorities.class);
        q.setParameter("userAuthority", userAuthority);
        return q;
    }

    public static TypedQuery<UserAuthorities> findUserAuthoritiesesByUserAuthority(UserAuthority userAuthority, String sortFieldName, String sortOrder) {
        if (userAuthority == null)
            throw new IllegalArgumentException("The userAuthority argument is required");
        EntityManager em = UserAuthorities.entityManager();
        String jpaQuery = "SELECT o FROM UserAuthorities AS o WHERE o.userAuthority = :userAuthority";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserAuthorities> q = em.createQuery(jpaQuery, UserAuthorities.class);
        q.setParameter("userAuthority", userAuthority);
        return q;
    }

    public static TypedQuery<UserAuthorities> findUserAuthoritiesesByUserProfile(UserProfile userProfile) {
        if (userProfile == null)
            throw new IllegalArgumentException("The userProfile argument is required");
        EntityManager em = UserAuthorities.entityManager();
        TypedQuery<UserAuthorities> q = em.createQuery("SELECT o FROM UserAuthorities AS o WHERE o.userProfile = :userProfile", UserAuthorities.class);
        q.setParameter("userProfile", userProfile);
        return q;
    }

    public static TypedQuery<UserAuthorities> findUserAuthoritiesesByUserProfile(UserProfile userProfile, String sortFieldName, String sortOrder) {
        if (userProfile == null)
            throw new IllegalArgumentException("The userProfile argument is required");
        EntityManager em = UserAuthorities.entityManager();
        String jpaQuery = "SELECT o FROM UserAuthorities AS o WHERE o.userProfile = :userProfile";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserAuthorities> q = em.createQuery(jpaQuery, UserAuthorities.class);
        q.setParameter("userProfile", userProfile);
        return q;
    }
}
