package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
public class UserAuthority implements GrantedAuthority {
    public static final String SYS_ADMIN = "ROLE_SYS_ADMIN";
    public static final String EVENT_ADMIN = "ROLE_EVENT_ADMIN";
    public static final String USER_ADMIN = "ROLE_USER_ADMIN";
    public static final String USER = "ROLE_USER";

    @NotNull
    private String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public boolean isAuthority(String authority) { return this.authority.equals(authority); }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userAuthority", cascade = CascadeType.ALL)
    private Set<UserAuthorities> userAuthorities;

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserAuthority fromJsonToUserAuthority(String json) {
        return new JSONDeserializer<UserAuthority>().use(null, UserAuthority.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserAuthority> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserAuthority> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserAuthority> fromJsonArrayToUserAuthoritys(String json) {
        return new JSONDeserializer<List<UserAuthority>>().use("values", UserAuthority.class).deserialize(json);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("authority", "userAuthorities");

    public static EntityManager entityManager() {
        EntityManager em = new UserAuthority().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countUserAuthoritys() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserAuthority o", Long.class).getSingleResult();
    }

    public static List<UserAuthority> findAllUserAuthoritys() {
        return entityManager().createQuery("SELECT o FROM UserAuthority o", UserAuthority.class).getResultList();
    }

    public static List<UserAuthority> findAllUserAuthoritys(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserAuthority o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserAuthority.class).getResultList();
    }

    public static UserAuthority findUserAuthority(Long id) {
        if (id == null)
            return null;
        return entityManager().find(UserAuthority.class, id);
    }

    public static List<UserAuthority> findUserAuthorityEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserAuthority o", UserAuthority.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<UserAuthority> findUserAuthorityEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserAuthority o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserAuthority.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            UserAuthority attached = UserAuthority.findUserAuthority(this.id);
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
    public UserAuthority merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        UserAuthority merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static Long countFindUserAuthoritysByAuthorityEquals(String authority) {
        if (authority == null || authority.isEmpty())
            throw new IllegalArgumentException("The authority argument is required");
        EntityManager em = UserAuthority.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM UserAuthority AS o WHERE o.authority = :authority", Long.class);
        q.setParameter("authority", authority);
        return q.getSingleResult();
    }

    public static TypedQuery<UserAuthority> findUserAuthoritysByAuthorityEquals(String authority) {
        if (authority == null || authority.isEmpty())
            throw new IllegalArgumentException("The authority argument is required");
        EntityManager em = UserAuthority.entityManager();
        TypedQuery<UserAuthority> q = em.createQuery("SELECT o FROM UserAuthority AS o WHERE o.authority = :authority", UserAuthority.class);
        q.setParameter("authority", authority);
        return q;
    }

    public static TypedQuery<UserAuthority> findUserAuthoritysByAuthorityEquals(String authority, String sortFieldName, String sortOrder) {
        if (authority == null || authority.isEmpty())
            throw new IllegalArgumentException("The authority argument is required");
        EntityManager em = UserAuthority.entityManager();
        String jpaQuery = "SELECT o FROM UserAuthority AS o WHERE o.authority = :authority";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserAuthority> q = em.createQuery(jpaQuery, UserAuthority.class);
        q.setParameter("authority", authority);
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

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Set<UserAuthorities> getUserAuthorities() {
        return this.userAuthorities;
    }

    public void setUserAuthorities(Set<UserAuthorities> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }
}
