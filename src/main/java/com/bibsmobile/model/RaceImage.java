package com.bibsmobile.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
public class RaceImage {

    @NotNull
    String filePath;

    @ManyToOne
    RaceResult raceResult;

    @ManyToOne
    Event event;

    @ManyToOne
    UserProfile userProfile;

    boolean nonPublic;

    @ManyToMany
    Set<PictureType> pictureTypes = new HashSet<>();

    public RaceImage() {
        super();
    }

    public RaceImage(String filePath, long eventId) {
        super();
        this.filePath = filePath;
        this.event = Event.findEvent(eventId);
    }

    public RaceImage(String filePath, RaceResult raceResult, Event event) {
        super();
        this.filePath = filePath;
        this.raceResult = raceResult;
        this.event = event;
    }

    public RaceImage(String filePath, long eventId, long bib) {
        super();
        this.filePath = filePath;
        this.event = Event.findEvent(eventId);
        this.raceResult = RaceResult.findRaceResultsByEventAndBibEquals(this.event, bib).getSingleResult();
    }

    public RaceImage(String filePath, long eventId, List<Long> bibs) {
        this(filePath, eventId);
        if (CollectionUtils.isNotEmpty(bibs)) {
        	System.out.println("non empty bibs found in request");
            List<RaceResult> raceResults = RaceResult.findRaceResultsByEventAndMultipleBibs(this.event, bibs);
            for (RaceResult tmpRaceResult : raceResults) {
            	System.out.println("persisting for race result: " + tmpRaceResult.getBib());
                new RaceImage(filePath, tmpRaceResult, this.event).persist();
            }
        }
    }

    public RaceImage(String filePath, long eventId, List<Long> bibs, List<String> types) {
        this(filePath, eventId, bibs);
        if (CollectionUtils.isNotEmpty(types)) {
            for (String type : types) {
                PictureType pictureType;
                if (PictureType.countFindPictureTypesByPictureTypeEquals(type) > 0) {
                    pictureType = PictureType.findPictureTypesByPictureTypeEquals(type).getSingleResult();
                } else {
                    pictureType = new PictureType();
                    pictureType.setPictureType(type);
                    pictureType.persist();
                }
                this.pictureTypes.add(pictureType);
            }
        }
        this.persist();
    }

    public static TypedQuery<RaceImage> findRaceImagesByRaceResults(List<RaceResult> raceResults) {
        if (raceResults == null)
            throw new IllegalArgumentException("The raceResults argument is required");
        EntityManager em = RaceImage.entityManager();
        TypedQuery<RaceImage> q = em.createQuery("SELECT o FROM RaceImage AS o WHERE o.raceResult IN (:raceResults)", RaceImage.class);
        q.setParameter("raceResults", raceResults);
        return q;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("filePath", "raceResult", "event", "userProfile", "nonPublic", "pictureTypes");

    public static EntityManager entityManager() {
        EntityManager em = new RaceImage().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countRaceImages() {
        return entityManager().createQuery("SELECT COUNT(o) FROM RaceImage o", Long.class).getSingleResult();
    }

    public static List<RaceImage> findAllRaceImages() {
        return entityManager().createQuery("SELECT o FROM RaceImage o", RaceImage.class).getResultList();
    }

    public static List<RaceImage> findAllRaceImages(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RaceImage o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RaceImage.class).getResultList();
    }

    public static RaceImage findRaceImage(Long id) {
        if (id == null)
            return null;
        return entityManager().find(RaceImage.class, id);
    }

    public static List<RaceImage> findRaceImageEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM RaceImage o", RaceImage.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<RaceImage> findRaceImageEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RaceImage o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RaceImage.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            RaceImage attached = RaceImage.findRaceImage(this.id);
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
    public RaceImage merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        RaceImage merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static RaceImage fromJsonToRaceImage(String json) {
        return new JSONDeserializer<RaceImage>().use(null, RaceImage.class).deserialize(json);
    }

    public static String toJsonArray(Collection<RaceImage> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<RaceImage> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<RaceImage> fromJsonArrayToRaceImages(String json) {
        return new JSONDeserializer<List<RaceImage>>().use("values", RaceImage.class).deserialize(json);
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public RaceResult getRaceResult() {
        return this.raceResult;
    }

    public void setRaceResult(RaceResult raceResult) {
        this.raceResult = raceResult;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public boolean isNonPublic() {
        return this.nonPublic;
    }

    public void setNonPublic(boolean nonPublic) {
        this.nonPublic = nonPublic;
    }

    public Set<PictureType> getPictureTypes() {
        return this.pictureTypes;
    }

    public void setPictureTypes(Set<PictureType> pictureTypes) {
        this.pictureTypes = pictureTypes;
    }

    public static Long countFindRaceImagesByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceImage.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceImage AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }

    public static TypedQuery<RaceImage> findRaceImagesByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceImage.entityManager();
        TypedQuery<RaceImage> q = em.createQuery("SELECT o FROM RaceImage AS o WHERE o.event = :event", RaceImage.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<RaceImage> findRaceImagesByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceImage.entityManager();
        String jpaQuery = "SELECT o FROM RaceImage AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RaceImage> q = em.createQuery(jpaQuery, RaceImage.class);
        q.setParameter("event", event);
        return q;
    }
}