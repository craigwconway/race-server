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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class SeriesAlert {

    /**
     */
    private String text;

    @ManyToOne
    private Series series;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @PrePersist
    protected void onPersist() {
        if (this.created == null)
            this.created = new Date();
        this.created = new Date();
    }    
    
    public static TypedQuery<SeriesAlert> findSeriesAlertsBySeriesId(Long seriesId) {
        EntityManager em = SeriesAlert.entityManager();
        TypedQuery<SeriesAlert> q = em.createQuery("SELECT sa FROM SeriesAlert AS sa WHERE sa.series.id = :seriesId", SeriesAlert.class);
        q.setParameter("seriesId", seriesId);
        return q;
    }

    public static TypedQuery<SeriesAlert> findLatestSeriesAlertBySeriesId(Long seriesId) {
        EntityManager em = SeriesAlert.entityManager();
        TypedQuery<SeriesAlert> q = em.createQuery("SELECT ea FROM SeriesAlert AS sa WHERE sa.series.id = :seriesId order by sa.created DESC", SeriesAlert.class);
        q.setParameter("seriesId", seriesId);
        q.setMaxResults(1);
        return q;
    }    
    
    @JSON(include = false)
    public Series getSeries() {
        return this.series;
    }

    public static Long countFindSeriesAlertsBySeries(Series series) {
        if (series == null)
            throw new IllegalArgumentException("The series argument is required");
        EntityManager em = SeriesAlert.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM Series AS o WHERE o.series = :series", Long.class);
        q.setParameter("series", series);
        return q.getSingleResult();
    }

    public static TypedQuery<SeriesAlert> findSeriesAlertsBySeries(Series series) {
        if (series == null)
            throw new IllegalArgumentException("The series argument is required");
        EntityManager em = SeriesAlert.entityManager();
        TypedQuery<SeriesAlert> q = em.createQuery("SELECT o FROM SeriesAlert AS o WHERE o.series = :series", SeriesAlert.class);
        q.setParameter("series", series);
        return q;
    }

    public static TypedQuery<SeriesAlert> findLatestSeriesAlertBySeries(Series series) {
        if (series == null)
            throw new IllegalArgumentException("The series argument is required");
        EntityManager em = SeriesAlert.entityManager();
        TypedQuery<SeriesAlert> q = em.createQuery("SELECT o FROM SeriesAlert AS o WHERE o.series = :series ORDER BY created DESC", SeriesAlert.class);
        q.setParameter("series", series);
        q.setMaxResults(1);
        return q;
    }    
    
    public static TypedQuery<SeriesAlert> findSeriesAlertBySeries(Series series, String sortFieldName, String sortOrder) {
        if (series == null)
            throw new IllegalArgumentException("The series argument is required");
        EntityManager em = SeriesAlert.entityManager();
        String jpaQuery = "SELECT o FROM SeriesAlert AS o WHERE o.series = :series";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<SeriesAlert> q = em.createQuery(jpaQuery, SeriesAlert.class);
        q.setParameter("series", series);
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

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    /**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	@PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("text", "series");

    public static EntityManager entityManager() {
        EntityManager em = new SeriesAlert().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countSeriesAlerts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SeriesAlert o", Long.class).getSingleResult();
    }

    public static SeriesAlert findSeriesAlert(Long id) {
        if (id == null)
            return null;
        return entityManager().find(SeriesAlert.class, id);
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
            SeriesAlert attached = SeriesAlert.findSeriesAlert(this.id);
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
    public SeriesAlert merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        SeriesAlert merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

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

    public static SeriesAlert fromJsonToSeriesAlert(String json) {
        return new JSONDeserializer<SeriesAlert>().use(null, SeriesAlert.class).deserialize(json);
    }

    public static String toJsonArray(Collection<SeriesAlert> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<SeriesAlert> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<SeriesAlert> fromJsonArrayToSeriesAlerts(String json) {
        return new JSONDeserializer<List<SeriesAlert>>().use("values", SeriesAlert.class).deserialize(json);
    }
}
