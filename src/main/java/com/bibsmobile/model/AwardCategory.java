package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
public class AwardCategory implements Comparable{

	public static final int MIN_AGE = 0;
	public static final int MAX_AGE = 109;
	public static final int DEFAULT_LIST_SIZE = 3;
	public static final int DEFAULT_AGE_SPAN = 4;
	public static final int MASTERS_MIN = 40;
	public static final int MASTERS_MAX = 49;
	public static final int GRANDMASTERS_MIN = 50;
	public static final int GRANDMASTERS_MAX = MAX_AGE;

    @ManyToOne
    private EventType eventType;
    
    private int sortOrder;
	private String name;
	private String gender;
	private int ageMin;
	private int ageMax;
	private int listSize;
	
	
	private boolean medal;
	private boolean master;
	public boolean isMedal(){
		return this.medal;
	}
	public void setMedal(boolean bool){
		this.medal = bool;
	}
	
	public boolean isMaster(){
		return this.master;
	}
	public void setMaster(boolean bool){
		this.master = bool;
	}

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("event", "sortOrder", "name", "gender", "ageMin", "ageMax", "listSize");

	public static final EntityManager entityManager() {
        EntityManager em = new AwardCategory().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
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
        if (id == null) return null;
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
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            AwardCategory attached = AwardCategory.findAwardCategory(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public AwardCategory merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
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

	public EventType getEventType() {
        return this.eventType;
    }

	public void setEventType(EventType eventType) {
        this.eventType = eventType;
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
	public String toJson() {
        return new JSONSerializer().exclude("*.class","eventType").serialize(this);
    }

	public String toJson(boolean full) {
        return new JSONSerializer().serialize(this);
    }

	public static AwardCategory fromJson(String json) {
        return new JSONDeserializer<AwardCategory>().use(null, AwardCategory.class).deserialize(json);
    }

	public static String toJsonArray(Collection<AwardCategory> collection) {
        return new JSONSerializer().exclude("*.class", "eventType").serialize(collection);
    }

	public static Collection<AwardCategory> fromJsonArray(String json) {
        return new JSONDeserializer<List<AwardCategory>>().use(null, ArrayList.class).use("values", AwardCategory.class).deserialize(json);
    }
	
	public static List<AwardCategory> createAgeGenderRankings(final EventType eventType, int ageMin, int ageMax, int ageSpan, int listSize){
		List<AwardCategory> list = new ArrayList<AwardCategory>();
		String[] genders = {"M","F"};
		int i = 0;
		while(ageMin <= ageMax){
			int _ageMax = ageMin + ageSpan;
			for(String gender:genders){
				AwardCategory c = new AwardCategory();
				c.setEventType(eventType);
				c.setAgeMin(ageMin);
				c.setAgeMax(_ageMax);
				c.setGender(gender);
				c.setListSize(listSize);
				c.setSortOrder(++i);
				String title = "Overall ";
				if(gender=="M") title = "Male ";
				if(gender=="F") title = "Female ";
				title += "Ages " + ageMin +" to "+_ageMax;
				c.setName(title);
				c.persist();
				list.add(c);
			}
			ageMin = _ageMax+1;
		}
		return list;
	}
	
	public static List<AwardCategory> createDefaultMedals(final EventType eventType){
		List<AwardCategory> list = new ArrayList<AwardCategory>();
		int i = 0;
		AwardCategory c = new AwardCategory();
		c.setName("Top Males Overall");
		c.setEventType(eventType);
		c.setAgeMin(MIN_AGE);
		c.setAgeMax(MAX_AGE);
		c.setGender("M");
		c.setListSize(DEFAULT_LIST_SIZE);
		c.setSortOrder(++i);
		c.setMedal(true);
		c.persist();
		list.add(c);
		
		c = new AwardCategory();
		c.setName("Top Females Overall");
		c.setEventType(eventType);
		c.setAgeMin(MIN_AGE);
		c.setAgeMax(MAX_AGE);
		c.setGender("F");
		c.setListSize(DEFAULT_LIST_SIZE);
		c.setSortOrder(++i);
		c.setMedal(true);
		c.persist();
		list.add(c);
		
		c = new AwardCategory();
		c.setName("Top Male Masters");
		c.setEventType(eventType);
		c.setAgeMin(MASTERS_MIN);
		c.setAgeMax(MASTERS_MAX);
		c.setGender("M");
		c.setListSize(DEFAULT_LIST_SIZE);
		c.setSortOrder(++i);
		c.setMedal(true);
		c.persist();
		list.add(c);
		
		c = new AwardCategory();
		c.setName("Top Female Masters");
		c.setEventType(eventType);
		c.setAgeMin(MASTERS_MIN);
		c.setAgeMax(MASTERS_MAX);
		c.setGender("F");
		c.setListSize(DEFAULT_LIST_SIZE);
		c.setSortOrder(++i);
		c.setMedal(true);
		c.persist();
		list.add(c);
		
		c = new AwardCategory();
		c.setName("Top Male Grand Masters");
		c.setEventType(eventType);
		c.setAgeMin(GRANDMASTERS_MIN);
		c.setAgeMax(GRANDMASTERS_MAX);
		c.setGender("M");
		c.setListSize(DEFAULT_LIST_SIZE);
		c.setSortOrder(++i);
		c.setMedal(true);
		c.persist();
		list.add(c);
		
		c = new AwardCategory();
		c.setName("Top Female Grand Masters");
		c.setEventType(eventType);
		c.setAgeMin(GRANDMASTERS_MIN);
		c.setAgeMax(GRANDMASTERS_MAX);
		c.setGender("F");
		c.setListSize(DEFAULT_LIST_SIZE);
		c.setSortOrder(++i);
		c.setMedal(true);
		c.persist();
		list.add(c);
		
		return list;
	}

	public static List<AwardCategory> findByEventType(EventType eventType) {
        EntityManager em = AwardCategory.entityManager();
        String jpaQuery = "SELECT o FROM AwardCategory AS o WHERE o.eventType = :eventType";
        jpaQuery = jpaQuery + " ORDER BY o.sortOrder ASC";
        TypedQuery<AwardCategory> q = em.createQuery(jpaQuery, AwardCategory.class);
        q.setParameter("eventType", eventType);
        return q.getResultList();
    }

	@Transactional
	public int removeAgeGenderRankingsByEvent(Event event) {
        EntityManager em = AwardCategory.entityManager();
        String jpaQuery = "DELETE FROM AwardCategory AS o WHERE o.eventType = :eventType AND o.medal = 0";
        Query q = em.createQuery(jpaQuery);
        q.setParameter("event", event);
        return q.executeUpdate();
    }

	@Override
	public int compareTo(Object o) {
		if(o instanceof AwardCategory){
			return getSortOrder() - ((AwardCategory)o).getSortOrder();
		}
		return 0;
	}
	
}
