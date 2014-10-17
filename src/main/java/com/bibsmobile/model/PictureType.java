package com.bibsmobile.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
public class PictureType {

    @NotNull
    private String pictureType;

    @ManyToMany
    private List<RaceImage> raceImages;

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

    public static PictureType fromJsonToPictureType(String json) {
        return new JSONDeserializer<PictureType>().use(null, PictureType.class).deserialize(json);
    }

    public static String toJsonArray(Collection<PictureType> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<PictureType> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<PictureType> fromJsonArrayToPictureTypes(String json) {
        return new JSONDeserializer<List<PictureType>>().use("values", PictureType.class).deserialize(json);
    }

    public static Long countFindPictureTypesByPictureTypeEquals(String pictureType) {
        if (pictureType == null || pictureType.length() == 0)
            throw new IllegalArgumentException("The pictureType argument is required");
        EntityManager em = PictureType.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM PictureType AS o WHERE o.pictureType = :pictureType", Long.class);
        q.setParameter("pictureType", pictureType);
        return q.getSingleResult();
    }

    public static TypedQuery<PictureType> findPictureTypesByPictureTypeEquals(String pictureType) {
        if (pictureType == null || pictureType.length() == 0)
            throw new IllegalArgumentException("The pictureType argument is required");
        EntityManager em = PictureType.entityManager();
        TypedQuery<PictureType> q = em.createQuery("SELECT o FROM PictureType AS o WHERE o.pictureType = :pictureType", PictureType.class);
        q.setParameter("pictureType", pictureType);
        return q;
    }

    public static TypedQuery<PictureType> findPictureTypesByPictureTypeEquals(String pictureType, String sortFieldName, String sortOrder) {
        if (pictureType == null || pictureType.length() == 0)
            throw new IllegalArgumentException("The pictureType argument is required");
        EntityManager em = PictureType.entityManager();
        String jpaQuery = "SELECT o FROM PictureType AS o WHERE o.pictureType = :pictureType";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<PictureType> q = em.createQuery(jpaQuery, PictureType.class);
        q.setParameter("pictureType", pictureType);
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

    public String getPictureType() {
        return this.pictureType;
    }

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }

    public List<RaceImage> getRaceImages() {
        return this.raceImages;
    }

    public void setRaceImages(List<RaceImage> raceImages) {
        this.raceImages = raceImages;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("pictureType", "raceImages");

    public static final EntityManager entityManager() {
        EntityManager em = new PictureType().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countPictureTypes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PictureType o", Long.class).getSingleResult();
    }

    public static List<PictureType> findAllPictureTypes() {
        return entityManager().createQuery("SELECT o FROM PictureType o", PictureType.class).getResultList();
    }

    public static List<PictureType> findAllPictureTypes(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM PictureType o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, PictureType.class).getResultList();
    }

    public static PictureType findPictureType(Long id) {
        if (id == null)
            return null;
        return entityManager().find(PictureType.class, id);
    }

    public static List<PictureType> findPictureTypeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PictureType o", PictureType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<PictureType> findPictureTypeEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM PictureType o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, PictureType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            PictureType attached = PictureType.findPictureType(this.id);
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
    public PictureType merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        PictureType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
