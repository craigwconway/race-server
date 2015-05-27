package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.amazonaws.services.s3.model.Bucket;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.UserProfileUtil;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.SpatialMode;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Entity
@Indexed
@Spatial(spatialMode = SpatialMode.HASH)
public class Event {

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private Set<RaceImage> raceImages;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private Set<RaceResult> raceResults;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private Set<ResultsFile> resultsFiles;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private List<AwardCategory> awardCategorys;

    @Field
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeStart;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeEnd;

    private int featured;

    private String address;

    private String city;

    private String state;
    
    private String zip;

    private String country;
    
    private String location;

    @Latitude
    private Double latitude;

    @Longitude
    private Double longitude;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private Set<EventType> eventTypes;

    private String website;

    private String phone;

    private String email;

    private String registration;

    private String general;

    private String description;

    @Field
    private String organization;

    private String parking;
    
    private String photo;

    private String photo2;

    private String photo3;
    
    /**
     * Embedded object containing the EventAwardsConfig.
     */
    @Embedded
    private EventAwardsConfig awardsConfig;

    private String donateUrl;

    private String facebookUrl1;

    private String photoUploadUrl;

    private String hashtag;

    private String courseRules;

    private int running;

    private boolean gunFired;

    private boolean sync;

    private String syncId;
    
    @Field
    private String charity;

    private boolean regEnabled;

    private boolean ticketTransferEnabled;
    
    private Date ticketTransferCutoff;
    
    private boolean live;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date regStart;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date regEnd;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date gunTime;

    private long gunTimeStart;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date updated;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private List<EventPhoto> photos = new ArrayList<>();

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private List<EventAlert> alerts = new ArrayList<>();

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private List<EventMap> maps = new ArrayList<>();

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private List<EventResult> results = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "event")
    private List<EventUserGroup> eventUserGroups = new ArrayList<>();

    /**
     */
    private String waiver = "https://s3-us-west-2.amazonaws.com/galen-shennanigans-2/standard-waiver.txt";  

    @PrePersist
    protected void onCreate() {
        this.created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static TypedQuery<Event> findEventsByFeaturedGreaterThan(int featured, int page, int size) {
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.featured > :featured order by o.featured ASC", Event.class);
        q.setParameter("featured", featured);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q;
    }

    public static TypedQuery<Event> findEventsByNameLike(String name, int page, int size) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The name argument is required");
        name = name.replace('*', '%');
        if (name.charAt(0) != '%') {
            name = "%" + name;
        }
        if (name.charAt(name.length() - 1) != '%') {
            name = name + "%";
        }
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE LOWER(o.name) LIKE LOWER(:name)", Event.class);
        q.setParameter("name", name);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q;
    }

    public static Event findEventByNameEquals(String name) {
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE LOWER(o.name) EQ LOWER(:name)", Event.class);
        q.setParameter("name", name);
        return q.getSingleResult();
    }

    public static TypedQuery<Event> findEventsByTimeStartGreaterThanAndFeaturedEquals(Date timeStart, int featured, int page, int size) {
        if (timeStart == null)
            throw new IllegalArgumentException("The timeStart argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.timeStart >= :timeStart AND o.featured = :featured " + "ORDER BY o.timeStart ASC", Event.class);
        q.setParameter("timeStart", timeStart);
        q.setParameter("featured", featured);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q;
    }

    public static TypedQuery<Event> findEventsByTimeStartLessThan(Date timeStart, int page, int size) {
        if (timeStart == null)
            throw new IllegalArgumentException("The timeStart argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.timeStart < :timeStart " + "ORDER BY o.timeStart DESC", Event.class);
        q.setParameter("timeStart", timeStart);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q;
    }

    public List<RaceResult> getAwards(String gender, int min, int max, int size) {
    	return getAwards( gender,  min,  max,  size, new ArrayList<Long>());
    }

    public List<RaceResult> getAwards(String gender, int min, int max, int size, List<Long> excludeBibs) { 
    	List<RaceResult> results = Event.findRaceResultsByAwardCategory(id,gender,min,max,1,999);
    	Collections.sort(results);
    	List<RaceResult> resultsFiltered = new ArrayList<RaceResult>();
    	for(RaceResult r : results){
    		if(!excludeBibs.contains(r.getBib())){
    			resultsFiltered.add(r);
    		}
    		if(resultsFiltered.size() == size){
    			break;
    		}
    	}
    	Collections.sort(resultsFiltered);
    	return resultsFiltered;
    }

    
    public List<AwardCategoryResults> calculateRank(Event event){
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	
		// filter medals
		List<Long> awarded = new ArrayList<Long>();
    	for(AwardCategory c:event.getAwardCategorys()){
    		if(!c.isMedal()){
    			List<RaceResult> rr = event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), 9999, awarded);
    			results.add(new AwardCategoryResults(c,rr));
    			// track mdals, only 1 medal ppn
    			for(RaceResult r:rr){
        			awarded.add(r.getBib());
    			}
    		}
    	}
    	
    	Collections.sort(results);
    	return results;
    }
    
    
    public List<AwardCategoryResults> calculateMedals(Event event){
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	List<Long> mastersBibs = new ArrayList<Long>();
    	
    	// if not allow masters in overall, collect masters bibs, pass into non-masters
    	if(!event.getAwardsConfig().isAllowMastersInNonMasters()){
        	for(AwardCategory c:event.getAwardCategorys()){
        		if(c.isMedal() && c.isMaster()){
        			List<RaceResult> masters = event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize());
        			for(RaceResult m:masters){
        				mastersBibs.add(m.getBib());
        			}
        		}
        	}
    	}
    	
		// filter medals
		List<Long> awarded = new ArrayList<Long>();
    	for(AwardCategory c:event.getAwardCategorys()){
    		if(c.isMedal()){
    			List<RaceResult> rr = (c.isMaster()) ? event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize(),awarded)
    					: event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize(), mastersBibs);
    			results.add(new AwardCategoryResults(c,rr));
    			// track mdals, only 1 medal ppn
    			for(RaceResult r:rr){
        			awarded.add(r.getBib());
        			mastersBibs.add(r.getBib());
    			}
    		}
    	}
    	
    	Collections.sort(results);
    	return results;
    }

    public static List<RaceResult> findRaceResultsByAwardCategory(long event, String gender, int min, int max, int page, int size) {
        if (min > max)
            min = max;
        String HQL = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.timeofficial > 0 ";
        if (!gender.isEmpty()) HQL += " AND o.gender = :gender ";
        if (min >= 0 && max > 0) HQL += "AND o.age >= :min AND o.age <= :max ) ";
        HQL += " order by o.timeofficialdisplay asc";
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);
        q.setParameter("event", Event.findEvent(event));
        if (!gender.isEmpty()) q.setParameter("gender", gender);
        if (min >= 0 && max > 0) {
            q.setParameter("min", min);
            q.setParameter("max", max);
        }
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public static List<RaceResult> findRaceResultsForAnnouncer(long event, int page, int size) {
        String HQL = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.timeofficial > 0 ";
        HQL += " order by (o.timeofficial - o.timestart) desc";
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);
        q.setParameter("event", Event.findEvent(event));
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public static List<RaceResult> findRecentRaceResultsForAnnouncer(long event, int page, int size) {
        String HQL = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND (o.timeofficial > 0 OR length(o.timesplit) > 0)";
        HQL += " order by o.updated desc";
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);
        q.setParameter("event", Event.findEvent(event));
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q.getResultList();
    }    
    
    public static long countRaceResults(long event) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT Count(rr) FROM RaceResult rr WHERE rr.event = :event", Long.class);
        q.setParameter("event", Event.findEvent(event));
        return q.getSingleResult();
    }

    public static long countRaceResultsStarted(long event) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT Count(rr) FROM RaceResult rr WHERE rr.event = :event and rr.timestart > 0", Long.class);
        q.setParameter("event", Event.findEvent(event));
        return q.getSingleResult();
    }

    public static long countRaceResultsComplete(long event) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT Count(rr) FROM RaceResult rr WHERE rr.event = :event and rr.timeofficial > 0", Long.class);
        q.setParameter("event", Event.findEvent(event));
        return q.getSingleResult();
    }

    public static List<RaceResult> findRaceResults(long event, int firstResult, int maxResults) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event", RaceResult.class);
        q.setParameter("event", Event.findEvent(event));
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q.getResultList();
    }

    public static List<Event> findEventsByRunning() {
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.running > 0 order by o.running asc", Event.class);
        return q.getResultList();
    }

    @Transactional
    public static int updateRaceResultsStarttimeByByEvent(Event event, long time0, long time1) {
        System.out.println("updateRaceResultsStarttimeByByEvent " + event + " " + time0 + " " + time1);
        EntityManager em = RaceResult.entityManager();
        Query q = em.createQuery("UPDATE RaceResult SET timestart = :time1 WHERE event = :event AND " + " (timestart is null OR timestart = 0 OR timestart = :time0) ");
        q.setParameter("event", event);
        q.setParameter("time0", time0);
        q.setParameter("time1", time1);
        System.out.println("updateRaceResultsStarttimeByByEvent excuteUpdate");
        return q.executeUpdate();
    }

    /*
    public static TypedQuery<Event> findEventsByTypeEquals(String type, int firstResult, int maxResults) {
        if (type == null || type.isEmpty())
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.type = :type", Event.class);
        q.setParameter("type", type);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q;
    }
*/
    public static TypedQuery<String> findAllEventsCountries() {
        EntityManager em = Event.entityManager();
        return em.createQuery("SELECT distinct event.country FROM Event AS event", String.class);
    }

    public static TypedQuery<String> findAllEventsCities() {
        EntityManager em = Event.entityManager();
        return em.createQuery("SELECT distinct event.city FROM Event AS event", String.class);
    }

    public static TypedQuery<String> findAllEventsCitiesByCountry(String country) {
        EntityManager em = Event.entityManager();
        TypedQuery<String> q = em.createQuery("SELECT distinct event.city FROM Event AS event WHERE event.country = :country", String.class);
        q.setParameter("country", country);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEqualsAndCityEquals(String state, String city, int firstResult, int maxResults) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.isEmpty())
            throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state  AND o.city = :city", Event.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEquals(String state, int firstResult, int maxResults) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state", Event.class);
        q.setParameter("state", state);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q;
    }

    public ResultsFile getLatestImportFile() {
        ResultsImport latest = null;
        if (this.resultsFiles == null)
            return null;
        for (ResultsFile rf : this.resultsFiles) {
            ResultsImport tmp = rf.getLatestImport();
            if (tmp == null)
                continue;
            if (latest == null || (latest.getRunDate() != null && tmp.getRunDate() != null && latest.getRunDate().compareTo(tmp.getRunDate()) < 0)) {
                latest = tmp;
            }
        }
        if (latest == null)
            return null;
        return latest.getResultsFile();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static Event fromJsonToEvent(String json) {
        return new JSONDeserializer<Event>().use(null, Event.class).deserialize(json);
    }

    public static String toJsonArray(Collection<Event> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<Event> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<Event> fromJsonArrayToEvents(String json) {
        return new JSONDeserializer<List<Event>>().use("values", Event.class).deserialize(json);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        Event rhs = (Event) obj;
        return new EqualsBuilder().append(this.address, rhs.address).append(this.city, rhs.city).append(this.state, rhs.state)
                .append(this.country, rhs.country).append(this.location, rhs.location).append(this.zip, rhs.zip).append(this.courseRules, rhs.courseRules).append(this.hashtag, rhs.hashtag).append(this.created, rhs.created)
                .append(this.description, rhs.description).append(this.donateUrl, rhs.donateUrl).append(this.email, rhs.email).append(this.facebookUrl1, rhs.facebookUrl1)
                .append(this.featured, rhs.featured).append(this.general, rhs.general).append(this.gunFired, rhs.gunFired)
                .append(this.gunTime, rhs.gunTime).append(this.gunTimeStart, rhs.gunTimeStart).append(this.id, rhs.id).append(this.latitude, rhs.latitude)
                .append(this.longitude, rhs.longitude).append(this.name, rhs.name).append(this.organization, rhs.organization).append(this.parking, rhs.parking).append(this.phone, rhs.phone)
                .append(this.photo, rhs.photo).append(this.photo2, rhs.photo2).append(this.photo3, rhs.photo3).append(this.photoUploadUrl, rhs.photoUploadUrl)
                .append(this.regEnabled, rhs.regEnabled).append(this.regEnd, rhs.regEnd).append(this.regStart, rhs.regStart).append(this.registration, rhs.registration)
                .append(this.running, rhs.running)
                .append(this.sync, rhs.sync).append(this.syncId, rhs.syncId).append(this.timeEnd, rhs.timeEnd)
                .append(this.timeStart, rhs.timeStart).append(this.updated, rhs.updated).append(this.waiver, rhs.waiver)
                .append(this.website, rhs.website).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.address).append(this.city).append(this.state).append(this.country).append(this.location).append(this.zip).append(this.courseRules).append(this.hashtag).append(this.created).append(this.description)
                .append(this.donateUrl).append(this.email).append(this.facebookUrl1).append(this.featured).append(this.general).append(this.gunFired)
                .append(this.gunTime).append(this.gunTimeStart).append(this.id).append(this.latitude).append(this.longitude).append(this.name).append(this.organization).append(this.parking).append(this.phone).append(this.photo).append(this.photo2)
                .append(this.photo3).append(this.photoUploadUrl).append(this.regEnabled).append(this.regEnd).append(this.regStart).append(this.registration).append(this.running).append(this.sync).append(this.syncId)
                .append(this.timeEnd).append(this.timeStart).append(this.updated).append(this.waiver).append(this.website).toHashCode();
    }

    public static Long countFindEventsByStateEquals(String state) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM Event AS o WHERE o.state = :state", Long.class);
        q.setParameter("state", state);
        return q.getSingleResult();
    }

    public static Long countFindEventsByStateEqualsAndCityEquals(String state, String city) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.isEmpty())
            throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM Event AS o WHERE o.state = :state  AND o.city = :city", Long.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        return q.getSingleResult();
    }

    /*
    public static Long countFindEventsByTypeEquals(String type) {
        if (type == null || type.isEmpty())
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM Event AS o WHERE o.type = :type", Long.class);
        q.setParameter("type", type);
        return (q.getSingleResult());
    }
*/
    public static TypedQuery<Event> findEventsByStateEquals(String state) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state", Event.class);
        q.setParameter("state", state);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEquals(String state, String sortFieldName, String sortOrder) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        String jpaQuery = "SELECT o FROM Event AS o WHERE o.state = :state";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Event> q = em.createQuery(jpaQuery, Event.class);
        q.setParameter("state", state);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEqualsAndCityEquals(String state, String city) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.isEmpty())
            throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state  AND o.city = :city", Event.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEqualsAndCityEquals(String state, String city, String sortFieldName, String sortOrder) {
        if (state == null || state.isEmpty())
            throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.isEmpty())
            throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        String jpaQuery = "SELECT o FROM Event AS o WHERE o.state = :state  AND o.city = :city";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Event> q = em.createQuery(jpaQuery, Event.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        return q;
    }
/*
    public static TypedQuery<Event> findEventsByTypeEquals(String type) {
        if (type == null || type.isEmpty())
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.type = :type", Event.class);
        q.setParameter("type", type);
        return q;
    }
*/
/*
    public static TypedQuery<Event> findEventsByTypeEquals(String type, String sortFieldName, String sortOrder) {
        if (type == null || type.isEmpty())
            throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        String jpaQuery = "SELECT o FROM Event AS o WHERE o.type = :type";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Event> q = em.createQuery(jpaQuery, Event.class);
        q.setParameter("type", type);
        return q;
    }
*/
    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("raceImages", "raceResults", "resultsFiles", "awardCategorys", "name", "timeStart",
            "timeEnd", "featured", "address", "city", "state", "country", "latitude", "longitude", "eventTypes", "website", "phone", "email",
            "registration", "general", "description", "organization", "parking", "photo", "photo2", "photo3", "results1", "results2",
            "results3", "donateUrl", "facebookUrl1", "photoUploadUrl", "hashtag",
            "courseRules", "running", "gunFired", "sync", "syncId", "regEnabled", "regStart", "regEnd", "gunTime", "gunTimeStart", "created", "updated", "photos", "alerts",
            "maps", "results", "eventUserGroups", "waiver");

    public static EntityManager entityManager() {
        EntityManager em = new Event().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countEvents() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Event o", Long.class).getSingleResult();
    }

    public static List<Event> findAllEvents() {
        return entityManager().createQuery("SELECT o FROM Event o", Event.class).getResultList();
    }

    public static List<Event> findAllEvents(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Event o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Event.class).getResultList();
    }

    public static Event findEvent(Long id) {
        if (id == null)
            return null;
        return entityManager().find(Event.class, id);
    }

    public static List<Event> findEventEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Event o", Event.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Event> findEventsForUser(UserProfile user) {
        return findEventsForUser(user, -1, -1, null, null);
    }

    public static List<Event> findEventsForUser(UserProfile user, int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        // return all events for the sysadmin or unauthenticated users (can not edit anyways)
        if (user == null || PermissionsUtil.isSysAdmin(user))
            return findEventEntries(firstResult, maxResults, sortFieldName, sortOrder);
        // get only accessible events for everyone else
        String jpaQuery = "select e from Event e join e.eventUserGroups eug join eug.userGroup ug join ug.userGroupUserAuthorities ugua join ugua.userAuthorities uasid join uasid.userProfile up where up.id = :user_id";
        // ordering if requested
        if (sortFieldName != null && sortOrder != null && fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }

        TypedQuery<Event> q = entityManager().createQuery(jpaQuery, Event.class);
        q.setParameter("user_id", user.getId());
        // paging if requested
        if (firstResult >= 0 && maxResults >= 0) {
            q.setFirstResult(firstResult).setMaxResults(maxResults);
        }

        return q.getResultList();
    }

    public static List<Event> findEventEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Event o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Event> q = entityManager().createQuery(jpaQuery, Event.class);
        if (firstResult >= 0 && maxResults >= 0) {
            q.setFirstResult(firstResult).setMaxResults(maxResults);
        }
        return q.getResultList();
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
            Event attached = Event.findEvent(this.id);
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
    public Event merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Event merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public Set<RaceImage> getRaceImages() {
        return this.raceImages;
    }

    public void setRaceImages(Set<RaceImage> raceImages) {
        this.raceImages = raceImages;
    }

    public Set<RaceResult> getRaceResults() {
        return this.raceResults;
    }

    public void setRaceResults(Set<RaceResult> raceResults) {
        this.raceResults = raceResults;
    }

    public Set<ResultsFile> getResultsFiles() {
        return this.resultsFiles;
    }

    public void setResultsFiles(Set<ResultsFile> resultsFiles) {
        this.resultsFiles = resultsFiles;
    }

    public List<AwardCategory> getAwardCategorys() {
        return this.awardCategorys;
    }

    public void setAwardCategorys(List<AwardCategory> awardCategorys) {
        this.awardCategorys = awardCategorys;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return this.timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getFeatured() {
        return this.featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public String getCity() {
        return WordUtils.capitalizeFully(this.city);
    }

    public void setCity(String city) {
        this.city = city;
    }

	public String getState() {
        return WordUtils.capitalizeFully(this.state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<EventType> getEventTypes() {
        return this.eventTypes;
    }

    public void setEventTypes(Set<EventType> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistration() {
        return this.registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getGeneral() {
        return this.general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto2() {
        return this.photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public String getPhoto3() {
        return this.photo3;
    }

    public void setPhoto3(String photo3) {
        this.photo3 = photo3;
    }

    public String getDonateUrl() {
        return this.donateUrl;
    }

    public void setDonateUrl(String donateUrl) {
        this.donateUrl = donateUrl;
    }

    public String getFacebookUrl1() {
        return this.facebookUrl1;
    }

    public void setFacebookUrl1(String facebookUrl1) {
        this.facebookUrl1 = facebookUrl1;
    }

    public String getPhotoUploadUrl() {
        return this.photoUploadUrl;
    }

    public void setPhotoUploadUrl(String photoUploadUrl) {
        this.photoUploadUrl = photoUploadUrl;
    }

    /**
     * Get the hashtag used by this event for social sharing.
     * This is autogenerated by the create event html endpoint as
     * a twitter-safe version of the event name.
     * @return alphanumeric twittersafe string, no hashtag included
     */
    public String getHashtag() {
        return this.hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getCourseRules() {
        return this.courseRules;
    }

    public void setCourseRules(String courseRules) {
        this.courseRules = courseRules;
    }

    public int getRunning() {
        return this.running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public boolean isGunFired() {
        return this.gunFired;
    }

    public void setGunFired(boolean gunFired) {
        this.gunFired = gunFired;
    }

    public boolean isSync() {
        return this.sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getSyncId() {
        return this.syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public boolean isRegEnabled() {
        return this.regEnabled;
    }

    public void setRegEnabled(boolean regEnabled) {
        this.regEnabled = regEnabled;
    }

    public Date getRegStart() {
        return this.regStart;
    }

    public void setRegStart(Date regStart) {
        this.regStart = regStart;
    }

    public Date getRegEnd() {
        return this.regEnd;
    }

    public void setRegEnd(Date regEnd) {
        this.regEnd = regEnd;
    }

    public Date getGunTime() {
        return this.gunTime;
    }

    public void setGunTime(Date gunTime) {
        this.gunTime = gunTime;
    }

    public long getGunTimeStart() {
        return this.gunTimeStart;
    }

    public void setGunTimeStart(long gunTimeStart) {
        this.gunTimeStart = gunTimeStart;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return this.updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /**
     * Get a list of Event Photos. 
     * These are used to display logos/images in app, not for runner/spectator uploaded images.
     * @return A list of EventPhoto objects.
     */
    public List<EventPhoto> getPhotos() {
        return this.photos;
    }

    /**
     * Set a list of Event Photos.
     * These are used to display logos/images in app, not for runner/spectator uploaded images.
     * @param photos A list of EventPhoto objects to set.
     */
    public void setPhotos(List<EventPhoto> photos) {
        this.photos = photos;
    }

    /**
     * Get a list of Event Alerts used to give notifications in app.
     * Common examples are: 
     *  - "Actual start time now 7:30".
     *  - "Remember to pickup your packet at the front table"
     * @return A list of Event Alerts set by an event director.
     */
    public List<EventAlert> getAlerts() {
        return this.alerts;
    }

    public void setAlerts(List<EventAlert> alerts) {
        this.alerts = alerts;
    }

    /**
     * Get a list of EventMap Images set by an event director.
     * These are image files uploaded and paired with an event.
     * @return
     */
    public List<EventMap> getMaps() {
        return this.maps;
    }

    public void setMaps(List<EventMap> maps) {
        this.maps = maps;
    }

    public List<EventResult> getResults() {
        return this.results;
    }

    public void setResults(List<EventResult> results) {
        this.results = results;
    }

    public List<EventUserGroup> getEventUserGroups() {
        return this.eventUserGroups;
    }

    public void setEventUserGroups(List<EventUserGroup> eventUserGroups) {
        this.eventUserGroups = eventUserGroups;
    }

    /**
     * Get waiver URL. When a waiver is posted as plaintext, it is uploaded to s3.
     * The bucket url is returned here. 
     * @return String containing bucket URL of waiver on S3.
     */
    public String getWaiver() {
        return this.waiver;
    }

    /**
     * URL of a waiver to set. This is used internally by the waiver endpoint.
     * The resulting waiver URL is saved in this field.
     * @param waiver
     */
    public void setWaiver(String waiver) {
        this.waiver = waiver;
    }

    /**
     * Returns whether ticket transfer is enabled for this event.
     * An event director still needs to specify a cutoff time for this feature to work.
     * @return
     */
    public boolean isTicketTransferEnabled() {
        return this.ticketTransferEnabled;
    }

    /**
     * Set this boolean to enable ticket transfer for an event.
     * A cutoff time still needs to be specified for this feature to work.
     * @param ticketTransferEnabled
     */
    public void setTicketTransferEnabled(boolean ticketTransferEnabled) {
        this.ticketTransferEnabled = ticketTransferEnabled;
    }

    /**
     * This is the time that user enabled ticket transfer ends for an event.
     * Ticket transfer must be set to true for this to work.
	 * @return Date of ticket transfer cutoff
	 */
	public Date getTicketTransferCutoff() {
		return ticketTransferCutoff;
	}
	/**
	 * @param ticketTransferCutoff Date of ticket transfer cutoff
	 */
	public void setTicketTransferCutoff(Date ticketTransferCutoff) {
		this.ticketTransferCutoff = ticketTransferCutoff;
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
    
    /**
     * Name of the charity that this event is for. This field is indexed for searches.
     * @return
     */
	public String getCharity() {
		return charity;
	}
	/**
	 * Name of the charity that this event is for. This field is indexed for searches.
	 * @param charity
	 */
	public void setCharity(String charity) {
		this.charity = charity;
	}
	/**
	 * Returns a switch to indicate whether or not the event is currently live.
	 * This impacts visibility in searches to unauthenticated users.
	 * @return - The current value of the boolean.
	 */
	public boolean isLive() {
		return live;
	}
	/**
	 * Set a boolean for whether or not the event is live.
	 * This impacts visibility in searches to unauthenticated users.
	 * @param live - The new value of the boolean.
	 */
	public void setLive(boolean live) {
		this.live = live;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getParking() {
		return parking;
	}
	public void setParking(String parking) {
		this.parking = parking;
	}

	/**
	 * This is an embedded entity inside of Event and can only be selected from its context.
	 * @return the awardsConfig
	 */
	public EventAwardsConfig getAwardsConfig() {
		return awardsConfig;
	}

	/**
	 * This is an embedded entity inside of event and can only be set from its context.
	 * @param awardsConfig the awardsConfig to set
	 */
	public void setAwardsConfig(EventAwardsConfig awardsConfig) {
		this.awardsConfig = awardsConfig;
	}
}
