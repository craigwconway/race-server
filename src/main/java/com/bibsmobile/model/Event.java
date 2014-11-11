package com.bibsmobile.model;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import javax.persistence.CascadeType;
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

@Configurable
@Entity
public class Event {

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private Set<RaceImage> raceImages;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private Set<RaceResult> raceResults;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private Set<ResultsFile> resultsFiles;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private List<AwardCategory> awardCategorys;

    @NotNull
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeStart;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeEnd;

    private int featured;

    private String address1;
    
    private String address2;
    
    private String city;

    private String state;
    
    private String zip;

    private String country;

    private String lattitude;

    private String longitude;

    @Deprecated
    private String type;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<EventType> eventTypes = new HashSet<>();

    private String website;

    private String phone;

    private String email;
    
    private String contactPerson;

    private String registration;

    private String parking;

    private String general;

    private String description;

    private String organization;

    private String photo;

    private String photo2;

    private String photo3;

    private String map;

    private String map2;

    private String map3;

    @Column(name = "results")
    private String results1;

    private String results2;

    private String results3;
    
    // hack for awards - JI-42
    @Transient
    private EventAwardsConfig awardsConfig;
    // use results 3 for json EventAwardsConfig
    public EventAwardsConfig getAwardsConfig(){
    	return (null == getResults3() || getResults3().isEmpty()) ? new EventAwardsConfig() : EventAwardsConfig.fromJson(getResults3());
    }
    public void setAwardsConfig(EventAwardsConfig awardsConfig){
    	this.awardsConfig = awardsConfig;
    	setResults3(awardsConfig.toJson());
    }

    private String alert1;

    private String alert2;

    private String alert3;

    private String donateUrl;

    private String facebookUrl1;

    private String facebookUrl2;

    private String photoUploadUrl;

    private String coursemaps;

    private String merchandise;

    private String beachEvents;

    private String shuttles;

    private String courseRules;

    private int running;

    private boolean gunFired;

    private boolean sync;

    private String syncId;

    private boolean regEnabled;
    
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

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private List<EventPhoto> photos = new ArrayList<EventPhoto>();

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private List<EventAlert> alerts = new ArrayList<EventAlert>();

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private List<EventMap> maps = new ArrayList<EventMap>();

    /**
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private List<EventResult> results = new ArrayList<EventResult>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "event")
    private List<EventUserGroup> eventUserGroups = new ArrayList<EventUserGroup>();

    /**
     */
    private String waiver;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

    @Override
    public String toString() {
        return name;
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
        if (name == null || name.length() == 0) throw new IllegalArgumentException("The name argument is required");
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
        if (timeStart == null) throw new IllegalArgumentException("The timeStart argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.timeStart >= :timeStart AND o.featured = :featured " + "ORDER BY o.timeStart ASC", Event.class);
        q.setParameter("timeStart", timeStart);
        q.setParameter("featured", featured);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q;
    }

    public static TypedQuery<Event> findEventsByTimeStartLessThan(Date timeStart, int page, int size) {
        if (timeStart == null) throw new IllegalArgumentException("The timeStart argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.timeStart < :timeStart " + "ORDER BY o.timeStart DESC", Event.class);
        q.setParameter("timeStart", timeStart);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);
        return q;
    }

    public List<RaceResult> getAwards(String gender, int min, int max, int size) {
    	return getAwards( gender,  min,  max,  size, new ArrayList<String>());
    }

    public List<RaceResult> getAwards(String gender, int min, int max, int size, List<String> excludeBibs) { 
    	List<RaceResult> results = Event.findRaceResultsByAwardCategory(id,gender,min,max,1,30);
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
    
    public List<AwardCategoryResults> calculateMedals(Event event){
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	List<String> mastersBibs = new ArrayList<String>();
    	
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
		List<String> awarded = new ArrayList<String>();
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
        if (min > max) min = max;
        String HQL = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.timeofficial > 0 ";
        if (!gender.isEmpty()) HQL += " AND o.gender = :gender ";
        if (min >= 0 && max > 0) HQL += "AND (cast(o.age, int) >= :min AND cast(o.age, int) <= :max ) ";
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

    public static TypedQuery<Event> findEventsByTypeEquals(String type, int firstResult, int maxResults) {
        if (type == null || type.length() == 0) throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.type = :type", Event.class);
        q.setParameter("type", type);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q;
    }

    public static TypedQuery<String> findAllEventsCountries() {
        EntityManager em = Event.entityManager();
        TypedQuery<String> q = em.createQuery("SELECT distinct event.country FROM Event AS event", String.class);
        return q;
    }

    public static TypedQuery<String> findAllEventsCities() {
        EntityManager em = Event.entityManager();
        TypedQuery<String> q = em.createQuery("SELECT distinct event.city FROM Event AS event", String.class);
        return q;
    }

    public static TypedQuery<String> findAllEventsCitiesByCountry(String country) {
        EntityManager em = Event.entityManager();
        TypedQuery<String> q = em.createQuery("SELECT distinct event.city FROM Event AS event WHERE event.country = :country", String.class);
        q.setParameter("country", country);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEqualsAndCityEquals(String state, String city, int firstResult, int maxResults) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.length() == 0) throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state  AND o.city = :city", Event.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q;
    }

    public static TypedQuery<Event> findEventsByStateEquals(String state, int firstResult, int maxResults) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state", Event.class);
        q.setParameter("state", state);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q;
    }

    public ResultsFile getLatestImportFile() {
        ResultsImport latest = null;
        if (this.resultsFiles == null) return null;
        for (ResultsFile rf : this.resultsFiles) {
            ResultsImport tmp = rf.getLatestImport();
            if (tmp == null) continue;
            if (latest == null || (latest.getRunDate() != null && tmp.getRunDate() != null && latest.getRunDate().compareTo(tmp.getRunDate()) > 0)) {
                latest = tmp;
            }
        }
        if (latest == null) return null;
        return latest.getResultsFile();
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static Event fromJsonToEvent(String json) {
        return new JSONDeserializer<Event>()
        .use(null, Event.class).deserialize(json);
    }

	public static String toJsonArray(Collection<Event> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<Event> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<Event> fromJsonArrayToEvents(String json) {
        return new JSONDeserializer<List<Event>>()
        .use("values", Event.class).deserialize(json);
    }

	public boolean equals(Object obj) {
        if (!(obj instanceof Event)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Event rhs = (Event) obj;
        return new EqualsBuilder().append(address1, rhs.address1).append(address2, rhs.address2).append(alert1, rhs.alert1).append(alert2, rhs.alert2).append(alert3, rhs.alert3).append(beachEvents, rhs.beachEvents).append(city, rhs.city).append(contactPerson, rhs.contactPerson).append(country, rhs.country).append(courseRules, rhs.courseRules).append(coursemaps, rhs.coursemaps).append(created, rhs.created).append(description, rhs.description).append(donateUrl, rhs.donateUrl).append(email, rhs.email).append(facebookUrl1, rhs.facebookUrl1).append(facebookUrl2, rhs.facebookUrl2).append(featured, rhs.featured).append(general, rhs.general).append(gunFired, rhs.gunFired).append(gunTime, rhs.gunTime).append(gunTimeStart, rhs.gunTimeStart).append(id, rhs.id).append(lattitude, rhs.lattitude).append(longitude, rhs.longitude).append(map, rhs.map).append(map2, rhs.map2).append(map3, rhs.map3).append(merchandise, rhs.merchandise).append(name, rhs.name).append(organization, rhs.organization).append(parking, rhs.parking).append(phone, rhs.phone).append(photo, rhs.photo).append(photo2, rhs.photo2).append(photo3, rhs.photo3).append(photoUploadUrl, rhs.photoUploadUrl).append(regEnabled, rhs.regEnabled).append(regEnd, rhs.regEnd).append(regStart, rhs.regStart).append(registration, rhs.registration).append(results1, rhs.results1).append(results2, rhs.results2).append(results3, rhs.results3).append(running, rhs.running).append(shuttles, rhs.shuttles).append(state, rhs.state).append(sync, rhs.sync).append(syncId, rhs.syncId).append(timeEnd, rhs.timeEnd).append(timeStart, rhs.timeStart).append(type, rhs.type).append(updated, rhs.updated).append(waiver, rhs.waiver).append(website, rhs.website).append(zip, rhs.zip).isEquals();
    }

	public int hashCode() {
        return new HashCodeBuilder().append(address1).append(address2).append(alert1).append(alert2).append(alert3).append(beachEvents).append(city).append(contactPerson).append(country).append(courseRules).append(coursemaps).append(created).append(description).append(donateUrl).append(email).append(facebookUrl1).append(facebookUrl2).append(featured).append(general).append(gunFired).append(gunTime).append(gunTimeStart).append(id).append(lattitude).append(longitude).append(map).append(map2).append(map3).append(merchandise).append(name).append(organization).append(parking).append(phone).append(photo).append(photo2).append(photo3).append(photoUploadUrl).append(regEnabled).append(regEnd).append(regStart).append(registration).append(results1).append(results2).append(results3).append(running).append(shuttles).append(state).append(sync).append(syncId).append(timeEnd).append(timeStart).append(type).append(updated).append(waiver).append(website).append(zip).toHashCode();
    }

	public static Long countFindEventsByStateEquals(String state) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM Event AS o WHERE o.state = :state", Long.class);
        q.setParameter("state", state);
        return ((Long) q.getSingleResult());
    }

	public static Long countFindEventsByStateEqualsAndCityEquals(String state, String city) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.length() == 0) throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM Event AS o WHERE o.state = :state  AND o.city = :city", Long.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        return ((Long) q.getSingleResult());
    }

	public static Long countFindEventsByTypeEquals(String type) {
        if (type == null || type.length() == 0) throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM Event AS o WHERE o.type = :type", Long.class);
        q.setParameter("type", type);
        return ((Long) q.getSingleResult());
    }

	public static TypedQuery<Event> findEventsByStateEquals(String state) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state", Event.class);
        q.setParameter("state", state);
        return q;
    }

	public static TypedQuery<Event> findEventsByStateEquals(String state, String sortFieldName, String sortOrder) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
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
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.length() == 0) throw new IllegalArgumentException("The city argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.state = :state  AND o.city = :city", Event.class);
        q.setParameter("state", state);
        q.setParameter("city", city);
        return q;
    }

	public static TypedQuery<Event> findEventsByStateEqualsAndCityEquals(String state, String city, String sortFieldName, String sortOrder) {
        if (state == null || state.length() == 0) throw new IllegalArgumentException("The state argument is required");
        if (city == null || city.length() == 0) throw new IllegalArgumentException("The city argument is required");
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

	public static TypedQuery<Event> findEventsByTypeEquals(String type) {
        if (type == null || type.length() == 0) throw new IllegalArgumentException("The type argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery("SELECT o FROM Event AS o WHERE o.type = :type", Event.class);
        q.setParameter("type", type);
        return q;
    }

	public static TypedQuery<Event> findEventsByTypeEquals(String type, String sortFieldName, String sortOrder) {
        if (type == null || type.length() == 0) throw new IllegalArgumentException("The type argument is required");
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

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("raceImages", "raceResults", "resultsFiles", "awardCategorys", "name", "timeStart", "timeEnd", "featured", "address1", "address2", "city", "state", "zip", "country", "lattitude", "longitude", "type", "eventTypes", "website", "phone", "email", "contactPerson", "registration", "parking", "general", "description", "organization", "photo", "photo2", "photo3", "map", "map2", "map3", "results1", "results2", "results3", "alert1", "alert2", "alert3", "donateUrl", "facebookUrl1", "facebookUrl2", "photoUploadUrl", "coursemaps", "merchandise", "beachEvents", "shuttles", "courseRules", "running", "gunFired", "sync", "syncId", "regEnabled", "regStart", "regEnd", "gunTime", "gunTimeStart", "created", "updated", "photos", "alerts", "maps", "results", "eventUserGroups", "waiver");

	public static final EntityManager entityManager() {
        EntityManager em = new Event().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
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
        if (id == null) return null;
        return entityManager().find(Event.class, id);
    }

	public static List<Event> findEventEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Event o", Event.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<Event> findEventEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Event o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Event.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Event attached = Event.findEvent(this.id);
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
    public Event merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
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

	public String getAddress1() {
        return this.address1;
    }

	public void setAddress1(String address1) {
        this.address1 = address1;
    }

	public String getAddress2() {
        return this.address2;
    }

	public void setAddress2(String address2) {
        this.address2 = address2;
    }

	public String getCity() {
        return WordUtils.capatolizeFully(this.city);
    }

	public void setCity(String city) {
        this.city = city;
    }

	public String getState() {
        return WordUtils.capatolizeFully(this.state);
    }

	public void setState(String state) {
        this.state = state;
    }

	public String getZip() {
        return this.zip;
    }

	public void setZip(String zip) {
        this.zip = zip;
    }

	public String getCountry() {
        return this.country;
    }

	public void setCountry(String country) {
        this.country = country;
    }

	public String getLattitude() {
        return this.lattitude;
    }

	public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

	public String getLongitude() {
        return this.longitude;
    }

	public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

	public String getType() {
        return this.type;
    }

	public void setType(String type) {
        this.type = type;
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

	public String getContactPerson() {
        return this.contactPerson;
    }

	public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

	public String getRegistration() {
        return this.registration;
    }

	public void setRegistration(String registration) {
        this.registration = registration;
    }

	public String getParking() {
        return this.parking;
    }

	public void setParking(String parking) {
        this.parking = parking;
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

	public String getMap() {
        return this.map;
    }

	public void setMap(String map) {
        this.map = map;
    }

	public String getMap2() {
        return this.map2;
    }

	public void setMap2(String map2) {
        this.map2 = map2;
    }

	public String getMap3() {
        return this.map3;
    }

	public void setMap3(String map3) {
        this.map3 = map3;
    }

	public String getResults1() {
        return this.results1;
    }

	public void setResults1(String results1) {
        this.results1 = results1;
    }

	public String getResults2() {
        return this.results2;
    }

	public void setResults2(String results2) {
        this.results2 = results2;
    }

	public String getResults3() {
        return this.results3;
    }

	public void setResults3(String results3) {
        this.results3 = results3;
    }

	public String getAlert1() {
        return this.alert1;
    }

	public void setAlert1(String alert1) {
        this.alert1 = alert1;
    }

	public String getAlert2() {
        return this.alert2;
    }

	public void setAlert2(String alert2) {
        this.alert2 = alert2;
    }

	public String getAlert3() {
        return this.alert3;
    }

	public void setAlert3(String alert3) {
        this.alert3 = alert3;
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

	public String getFacebookUrl2() {
        return this.facebookUrl2;
    }

	public void setFacebookUrl2(String facebookUrl2) {
        this.facebookUrl2 = facebookUrl2;
    }

	public String getPhotoUploadUrl() {
        return this.photoUploadUrl;
    }

	public void setPhotoUploadUrl(String photoUploadUrl) {
        this.photoUploadUrl = photoUploadUrl;
    }

	public String getCoursemaps() {
        return this.coursemaps;
    }

	public void setCoursemaps(String coursemaps) {
        this.coursemaps = coursemaps;
    }

	public String getMerchandise() {
        return this.merchandise;
    }

	public void setMerchandise(String merchandise) {
        this.merchandise = merchandise;
    }

	public String getBeachEvents() {
        return this.beachEvents;
    }

	public void setBeachEvents(String beachEvents) {
        this.beachEvents = beachEvents;
    }

	public String getShuttles() {
        return this.shuttles;
    }

	public void setShuttles(String shuttles) {
        this.shuttles = shuttles;
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

	public List<EventPhoto> getPhotos() {
        return this.photos;
    }

	public void setPhotos(List<EventPhoto> photos) {
        this.photos = photos;
    }

	public List<EventAlert> getAlerts() {
        return this.alerts;
    }

	public void setAlerts(List<EventAlert> alerts) {
        this.alerts = alerts;
    }

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

	public String getWaiver() {
        return this.waiver;
    }

	public void setWaiver(String waiver) {
        this.waiver = waiver;
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
}
