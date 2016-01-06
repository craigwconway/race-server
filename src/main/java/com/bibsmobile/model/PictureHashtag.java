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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
public class PictureHashtag {

    @NotNull
    private String pictureHashtag;

    @ManyToMany
    @JoinTable(name="picture_hashtag_race_image",
    joinColumns={@JoinColumn(name="hashtag_id", referencedColumnName="id")}, 
    inverseJoinColumns={@JoinColumn(name="image_id", referencedColumnName="id")})
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

    public static PictureHashtag fromJsonToPictureHashtag(String json) {
        return new JSONDeserializer<PictureHashtag>().use(null, PictureHashtag.class).deserialize(json);
    }

    public static String toJsonArray(Collection<PictureHashtag> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<PictureHashtag> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<PictureHashtag> fromJsonArrayToPictureHashtags(String json) {
        return new JSONDeserializer<List<PictureHashtag>>().use("values", PictureHashtag.class).deserialize(json);
    }

    public static Long countFindPictureHashtagsByPictureHashtagEquals(String pictureHashtag) {
        if (pictureHashtag == null || pictureHashtag.isEmpty())
            throw new IllegalArgumentException("The pictureHashtag argument is required");
        EntityManager em = PictureHashtag.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM PictureHashtag AS o WHERE o.pictureHashtag = :pictureHashtag", Long.class);
        q.setParameter("pictureHashtag", pictureHashtag);
        return q.getSingleResult();
    }

    public static TypedQuery<PictureHashtag> findPictureHashtagsByPictureHashtagEquals(String pictureHashtag) {
        if (pictureHashtag == null || pictureHashtag.isEmpty())
            throw new IllegalArgumentException("The pictureHashtag argument is required");
        EntityManager em = PictureHashtag.entityManager();
        TypedQuery<PictureHashtag> q = em.createQuery("SELECT o FROM PictureHashtag AS o WHERE o.pictureHashtag = :pictureHashtag", PictureHashtag.class);
        q.setParameter("pictureHashtag", pictureHashtag);
        return q;
    }

    public static TypedQuery<PictureHashtag> findPictureHashtagsByPictureHashtagEquals(String pictureHashtag, String sortFieldName, String sortOrder) {
        if (pictureHashtag == null || pictureHashtag.isEmpty())
            throw new IllegalArgumentException("The pictureHashtag argument is required");
        EntityManager em = PictureHashtag.entityManager();
        String jpaQuery = "SELECT o FROM PictureHashtag AS o WHERE o.pictureHashtag = :pictureHashtag";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<PictureHashtag> q = em.createQuery(jpaQuery, PictureHashtag.class);
        q.setParameter("pictureHashtag", pictureHashtag);
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

    public String getPictureHashtag() {
        return this.pictureHashtag;
    }

    public void setPictureHashtag(String pictureHashtag) {
        this.pictureHashtag = pictureHashtag;
    }

    public List<RaceImage> getRaceImages() {
        return this.raceImages;
    }

    public void setRaceImages(List<RaceImage> raceImages) {
        this.raceImages = raceImages;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("pictureHashtag", "raceImages");

    public static EntityManager entityManager() {
        EntityManager em = new PictureHashtag().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countPictureHashtags() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PictureHashtag o", Long.class).getSingleResult();
    }

    public static List<PictureHashtag> findAllPictureHashtags() {
        return entityManager().createQuery("SELECT o FROM PictureHashtag o", PictureHashtag.class).getResultList();
    }

    public static List<PictureHashtag> findAllPictureHashtags(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM PictureHashtag o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, PictureHashtag.class).getResultList();
    }

    public static PictureHashtag findPictureHashtag(Long id) {
        if (id == null)
            return null;
        return entityManager().find(PictureHashtag.class, id);
    }

    public static List<PictureHashtag> findPictureHashtagEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PictureHashtag o", PictureHashtag.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static PictureHashtag findPictureHashtagByString(String query) {
    	List<PictureHashtag> hashtags = entityManager().createQuery("SELECT o FROM PictureHashtag o where o.pictureHashtag = :hashtag", PictureHashtag.class)
    			.setParameter("hashtag", query).setFirstResult(0).setMaxResults(1).getResultList();
    	if(hashtags == null || hashtags.isEmpty()) {
    		return null;
    	} else {
    		return hashtags.get(0);
    	}
    }

    public static List<PictureHashtag> findPictureHashtagEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM PictureHashtag o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, PictureHashtag.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            PictureHashtag attached = PictureHashtag.findPictureHashtag(this.id);
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
    public PictureHashtag merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        PictureHashtag merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
