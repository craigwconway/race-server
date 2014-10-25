package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class AwardCategory {

    @ManyToOne
    private Event event;

    private int sortOrder;
    private String name;
    private String gender;
    private int ageMin;
    private int ageMax;
    private int listSize;

    public static List<AwardCategory> eventDefaults() {
        List<AwardCategory> awardCategories = new ArrayList<>();
        AwardCategory awardCategory = new AwardCategory();
        awardCategory.setName("Overall Winners");
        awardCategory.setListSize(5);
        awardCategories.add(awardCategory);
        return awardCategories;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("event", "sortOrder", "name", "gender", "ageMin", "ageMax", "listSize");

    public static EntityManager entityManager() {
        EntityManager em = new AwardCategory().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countAwardCategorys() {
        return entityManager().createQuery("SELECT COUNT(o) FROM AwardCategory o", Long.class).getSingleResult();
    }

    public static List<AwardCategory> findAllAwardCategorys() {
        return entityManager().createQuery("SELECT o FROM AwardCategory o", AwardCategory.class).getResultList();
    }

    public static List<AwardCategory> findAllAwardCategorys(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM AwardCategory o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, AwardCategory.class).getResultList();
    }

    public static AwardCategory findAwardCategory(Long id) {
        if (id == null)
            return null;
        return entityManager().find(AwardCategory.class, id);
    }

    public static List<AwardCategory> findAwardCategoryEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM AwardCategory o", AwardCategory.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<AwardCategory> findAwardCategoryEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM AwardCategory o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, AwardCategory.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            AwardCategory attached = AwardCategory.findAwardCategory(this.id);
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
    public AwardCategory merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        AwardCategory merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
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

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAgeMin() {
        return this.ageMin;
    }

    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    public int getAgeMax() {
        return this.ageMax;
    }

    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    public int getListSize() {
        return this.listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
}
