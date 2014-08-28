package com.bibsmobile.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;

@RooJavaBean
@RooEquals
@RooJson
@RooJpaActiveRecord(finders = { "findEventsByTypeEquals", "findEventsByStateEquals", "findEventsByStateEqualsAndCityEquals", "findEventsByEventUserGroup" })
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
        if (min > max) min = max;
        List<RaceResult> allResults = new ArrayList<RaceResult>(raceResults);
        Collections.sort(allResults);
        List<RaceResult> results = new ArrayList<RaceResult>();
        for (RaceResult result : allResults) {
            int age = 0;
            try {
                age = Integer.valueOf(result.getAge());
            } catch (Exception ex) {
            }
            if (results.size() < size && result.getTimeofficial() > 0 && (gender.isEmpty() || gender == null || gender.equals(result.getGender())) && (min == 0 || (min <= age && age != 0)) && (max == 0 || (max >= age && age != 0))) {
                results.add(result);
            }
            if (results.size() == size && size != 0) break;
        }
        return results;
    }

    public static List<RaceResult> findRaceResultsByAwardCategory(long event, String gender, int min, int max, int page, int size) {
        if (min > max) min = max;
        String HQL = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.timeofficial > 0 ";
        if (!gender.isEmpty()) HQL += " AND o.gender = :gender ";
        if (min > 0 && max > 0) HQL += "AND (o.age >= :min AND o.age <= :max ) ";
        HQL += " order by (o.timeofficial-o.timestart) asc";
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);
        q.setParameter("event", Event.findEvent(event));
        if (!gender.isEmpty()) q.setParameter("gender", gender);
        if (min > 0 && max > 0) {
            q.setParameter("min", String.valueOf(min));
            q.setParameter("max", String.valueOf(max));
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
            if (latest == null || latest.getRunDate().compareTo(tmp.getRunDate()) > 0) {
                latest = tmp;
            }
        }
        if (latest == null) return null;
        return latest.getResultsFile();
    }
}
