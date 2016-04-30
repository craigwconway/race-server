package com.bibsmobile.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.util.BuildTypeUtil;
import com.bibsmobile.util.PermissionsUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * RaceResult object for storing runners information and times. 
 * 
 * Fields are either auto-generated, importable into or populated from forms/rfid timers.
 * This is a Hibernate entity with two embedded collections of entities: one for custom fields
 * and the other for split times.
 * 
 * @author galen [gedanziger]
 *
 */
@Entity
@Configurable
@Indexed
public class RaceResult implements Comparable<RaceResult> {

	/**
	 * Event holding the race result. Deleting the event cascacdes to the race result.
	 */
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    /**
     * EventType holding the race result.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private EventType eventType;

    /**
     * User profile associated with the race result.
     */
    @ManyToOne
    private UserProfile userProfile;

    /**
     * Race images tagged with this race result. This is done by associating a bib number.
     * Mapped on a join table called race_result_race_image.
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "race_result_race_image",
        joinColumns = {@JoinColumn(name = "result_id", referencedColumnName="id")},
        inverseJoinColumns = @JoinColumn(name = "image_id", referencedColumnName="id"))
    private Set<RaceImage> raceImages;

    /**
     * Bib identifying the race result. Note that this is not an id, though the set
     * (Event, Bib) should represent a unique identifier for searches. This is an import field.
     */
    @NotNull
    @Field
    private long bib;

    /**
     * First name of runner (Can contain multiple names, all but the last name should go
     * here when imported from the fullname field). This is an import field.
     */
    @Field // search field
    private String firstname;

    /**
     * Last name of runner. Last name of runner should go here when imported from the fullname field.
     * This is an import field.
     */
    @Field // search field
    private String lastname;

    /**
     * Runner's age. This must be a positive integer or null. This is an import field.
     */
    private Integer age;

    /**
     * Runner's gender. This is stored as either 'M' or 'F' in the database. This is an import field.
     */
    private String gender;
    
    /**
     * Name of team that the runner belongs to. This is an import field.
     */
    private String team;
    
    /**
     * Runner's Email. This is an import field.
     */
    private String email;

    /**
     * Number of laps completed by the runner.
     */
    private Integer laps;
    
    private String timesplit;

    /**
     * Runner's start time as a UNIX ms timestamp. Cannot be imported into manually.
     */
    private long timestart;

    /**
     * Runner's finish time as a UNIX ms timestamp. Importing a readable time string into this
     * field parses the value and generates the actual timeofficial.
     */
    private long timeofficial;

    /**
     * Autogenerated string showing the runner's finish time in human-readable form.
     */
    private String timeofficialdisplay;
    
    /**
     * String used to store valid import times.
     */
    private String timeofficialmanual;
    
    /**
     * String to store valid gun import times.
     */
    private String timegunmanual;
    
    /**
     * String used to store time difference for official time computations.
     */
    private long timediff;

    /**
     * Runner's overall rank. This is populated on import.
     */
    private String rankoverall;

    /**
     * Runner's age rank. This is populated on import.
     */
    private String rankage;

    /**
     * Runner's gender rank. This is populated on import.
     */
    private String rankgender;

    /**
     * Runner's class rank. This is populated on import.
     */
    private String rankclass;

    /**
     * Runner's medal (if recieved). This is populated on import.
     */
    private String medal;
 
    /**
     * Embedded set of split objects containing times and positions for splits.
     */
    @ElementCollection
    @MapKeyColumn(name = "position")
    private Map<Integer, Split> splits = new HashMap<Integer, Split>();
    
    /**
     * Embedded set of custom field objects containing a key-value pair of fields and responses.
     */
    @ElementCollection
    @CollectionTable
    @Column(name = "value")
    @MapKeyColumn(name = "field")
    private Map<String,String> customFields = new HashMap<String, String> ();
    
    /**
     * Runner's City of origin
     */
    private String city;

    /**
     * Runner's state of origin. This is an import field.
     */
    private String state;

    /**
     * Runner's country of origin. This is an import field.
     */
    private String country;
        
   /**
     * This indicates whether or not a license was applied to this raceresult. Not an import field.
     */
    private boolean licensed;
    
    /**
     * This is a switch to indicate whether this raceresult has come from a timer. Not an import field.
     */
    private boolean timed;

    /**
     * Boolean to indicate whether or not a runner has been checked in to the event.
     */
    private boolean checkedin;
    
    /**
     * Flag to indicate whether the runner has been disqualified. Keep False
     */
    private Boolean disqualified=false;
    /**
     * The time the raceresult was created, generated through a pre-update hook. This is not an import field.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date created;

    /**
     * Time that the raceresult was last updated, generted through a pre-update hook. This is not an import field.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date updated;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        if (this.created == null)
            this.created = new Date();
        this.updated = new Date();
        if(!StringUtils.isEmpty(this.timeofficialmanual)) {
        	try {
        		this.timediff = RaceResult.fromHumanTime(this.timeofficialmanual);
        		} catch(Exception e) {
        			System.out.println("Result ID: " + this.getId() + " Bib: " + this.bib + " Malformed manual time: " + this.timeofficialmanual);
        			this.timeofficialmanual = null;
        	}
        } else if(this.timeofficial > 0 && this.timestart > 0 ) {
        	this.timediff = timeofficial - timestart;
        } else {
        	this.timediff = 0;
        }
    }

    @Override
    public String toString() {
        return this.event + " " + this.bib + " " + this.firstname + " " + this.lastname;
    }

    /**
     * Get timeofficialdisplay from race result. In licensed builds, this will return emptystring if the RaceResult
     * does not have a license applied to it. It will also return emptystring if timestart or timeofficial is zero,
     * or if timeofficial is less than timestart.
     * @return String containing a runner's human-readable time.
     */
    public String getTimeofficialdisplay() {
    	if (!StringUtils.isEmpty(this.timeofficialmanual)) {
    		return this.timeofficialmanual;
    	}else if (this.timestart == 0 || this.timeofficial == 0)
            return "";
        return RaceResult.toHumanTime(this.timestart, this.timeofficial);
    }

    public String getTimeofficialdisplayGun() {
    	if(!StringUtils.isEmpty(this.getTimegunmanual())) return this.timegunmanual;
    	if(this.getEventType() == null) return "";
    	if(this.getEventType().getGunTime() == null) return "";
        return RaceResult.toHumanTime(this.getEventType().getGunTime().getTime(), this.timeofficial);
    }
    /**
     * This returns the last-set value of the runner's timeofficialdisplay, regardless of what the current timeofficial,
     * timestart or state of the runner's license are.
     * @return
     */
    public String valueOfTimeofficialdisplay() {
    	return this.timeofficialdisplay;
    }
    /**
     * Over-write current fields with non-null values from new object
     * 
     * @param raceResult
     */
    public void merge(final RaceResult raceResult) {
        this.age = raceResult.getAge() != null ? raceResult.getAge() : this.age;
        this.city = raceResult.getCity() != null ? raceResult.getCity() : this.city;
        this.state = raceResult.getState() != null ? raceResult.getState() : this.state;
        this.country = raceResult.getCountry() != null ? raceResult.getCountry() : this.country;
        this.email = raceResult.getEmail() != null ? raceResult.getEmail() : this.email;
        this.team = raceResult.getTeam() != null ? raceResult.getTeam(): this.team;
        this.firstname = raceResult.getFirstname() != null ? raceResult.getFirstname() : this.firstname;
        this.lastname = raceResult.getLastname() != null ? raceResult.getLastname() : this.lastname;
        this.timeofficialmanual = raceResult.getTimeofficialmanual() != null ? raceResult.getTimeofficialmanual() :this.timeofficialmanual;
        this.gender = raceResult.getGender() != null ? raceResult.getGender() : this.gender;
        this.laps = raceResult.getLaps() != null ? raceResult.getLaps() : this.laps;
        this.eventType = raceResult.getEventType() != null ? raceResult.getEventType() : this.eventType;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndFirstnameLike(Event event, String firstname) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.isEmpty())
            throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)", RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        return q;
    }
    
    public static List<String> getTeamsForEventType(EventType type) {
    	EntityManager em = RaceResult.entityManager();
        TypedQuery<String> q = em.createQuery("SELECT distinct o.team FROM RaceResult AS o WHERE o.eventType = :type and o.team != null order by o.team ASC", String.class);
        q.setParameter("type", type);
        return q.getResultList();
    }
    
    public static List<RaceResult> getRaceResultsByTeam(EventType type, String team) {
    	EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.eventType = :type and o.team = :team", RaceResult.class);
        q.setParameter("type", type);
        q.setParameter("team", team);
        return q.getResultList();
    }
    
    public static List<RaceResult> getRankingForTeam(EventType type, String team, String gender, int number) {
    	EntityManager em = RaceResult.entityManager();
    	String query = "SELECT o FROM RaceResult AS o WHERE o.eventType = :type and o.team = :team and o.timediff > 0";
    	if(StringUtils.equalsIgnoreCase(gender, "M")) {
    		query += "and o.gender = 'M'";
    	} else if(StringUtils.equalsIgnoreCase(gender, "F")) {
    		query += "and o.gender = 'F'";
    	} 
    	query += "ORDER BY o.timediff ASC";
        TypedQuery<RaceResult> q = em.createQuery(query, RaceResult.class);
        q.setParameter("type", type);
        q.setParameter("team", team);
        //q.setP
        //if(StringUtils.equalsIgnoreCase(gender, "M") || StringUtils.equalsIgnoreCase(gender, "F")){
        //	 q.setParameter("team", team);
        //}
        q.setMaxResults(number);
       return q.getResultList();
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event event, String firstname, String lastname) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.isEmpty())
            throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em
                .createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)  AND LOWER(o.lastname) LIKE LOWER(:lastname)",
                        RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        q.setParameter("lastname", lastname);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndLastnameLike(Event event, String lastname) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.lastname) LIKE LOWER(:lastname)", RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("lastname", lastname);
        return q;
    }

    public static List<RaceResult> findRaceResultsByEventAndMultipleBibs(Event event, List<Long> bibs) {
        if (bibs == null) {
            throw new IllegalArgumentException("The bibs argument is required");
        }
        EntityManager em = RaceResult.entityManager();
        String HQL = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.bib IN (:bibs)";
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("bibs", bibs);
        return q.getResultList();
    }

    public static List<RaceResult> searchSeriesPaginated(Series series, String name, Integer page, Integer pageSize) {
        EntityManager em = RaceResult.entityManager();

        String firstname = "";
        String lastname = "";
        if (name.contains(" ")) {
            firstname = name.split(" ")[0];
            lastname = name.split(" ")[1];
        }

        String HQL = "SELECT o FROM RaceResult o join o.event e  WHERE e.series = :series AND";

        if (!firstname.isEmpty() && !lastname.isEmpty()) {
            firstname += "%";
            lastname += "%";
            HQL += " LOWER(o.firstname) LIKE LOWER(:firstname) AND LOWER(o.lastname) LIKE LOWER(:lastname) ";
        } else {
            name += "%";
            HQL += " (LOWER(o.firstname) LIKE LOWER(:name) OR LOWER(o.lastname) LIKE LOWER(:name)) ";
        }
        if(HQL.endsWith("AND"))
        	HQL = HQL.substring(0, HQL.length() - 3);
        
        System.out.println("HQL: " + HQL);
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);

        q.setParameter("series", series);
        if (!firstname.isEmpty() && !lastname.isEmpty()) {
            q.setParameter("firstname", firstname);
            q.setParameter("lastname", lastname);
        } else {
            q.setParameter("name", name);
        }
        q.setFirstResult((page-1) * 10);
        q.setMaxResults(pageSize);

        return q.getResultList();
    }    
    
    public static List<RaceResult> searchPaginated(Long eventId, Long eventTypeId, String name, Long bib, Integer page, Integer pageSize, String gender, String team) {
        EntityManager em = RaceResult.entityManager();
        
        Event event = new Event();
        if (null != eventId && eventId > 0)
            event = Event.findEvent(eventId);
        
        EventType eventType = null;
        if (null != eventTypeId && eventTypeId > 0)
        	eventType = EventType.findEventType(eventTypeId);
        
        String firstname = "";
        String lastname = "";
        if (name.contains(" ")) {
            firstname = name.split(" ")[0];
            lastname = name.split(" ")[1];
        }

        String HQL = "SELECT o FROM RaceResult AS o WHERE ";

        if (StringUtils.isEmpty(name) && bib == null)
        	HQL += "o.timediff > 0 AND";
        
        if (null != eventId && eventId > 0)
            HQL += " o.event = :event AND ";

        if (null != eventTypeId && eventTypeId > 0)
        	HQL += " o.eventType = :eventType AND ";
        
        if (bib != null)
            HQL += " o.bib = :bib AND ";

        if(gender != null && gender.equalsIgnoreCase("M"))
        	HQL += " o.gender = 'M' AND";
        
        if(gender != null && gender.equalsIgnoreCase("F"))
        	HQL += " o.gender = 'F' AND";
        
        if(team != null) {
        	HQL += " o.team LIKE LOWER(:team) AND";
        }
        if (!StringUtils.isEmpty(firstname) && !StringUtils.isEmpty(lastname)) {
            firstname += "%";
            lastname += "%";
            HQL += " LOWER(o.firstname) LIKE LOWER(:firstname) AND LOWER(o.lastname) LIKE LOWER(:lastname) ";
        } else if (StringUtils.isEmpty(firstname) && StringUtils.isEmpty(lastname)){
        	System.out.println("No name selected");
        } else{
            name += "%";
            HQL += " (LOWER(o.firstname) LIKE LOWER(:name) OR LOWER(o.lastname) LIKE LOWER(:name)) ";
        }
        if(HQL.endsWith("AND"))
        	HQL = HQL.substring(0, HQL.length() - 3);
        HQL += "ORDER BY (o.timediff) ASC";
        
        System.out.println("HQL: " + HQL);
        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);

        if (null != eventId && eventId > 0)
            q.setParameter("event", event);
        if (null != eventTypeId && eventTypeId > 0)
        	q.setParameter("eventType", eventType);
        if (bib != null)
            q.setParameter("bib", bib);
        if(team != null)
        	q.setParameter("team", team);
        if (StringUtils.isNotEmpty(firstname) && !StringUtils.isNotEmpty(lastname)) {
            q.setParameter("firstname", firstname);
            q.setParameter("lastname", lastname);
        } else if(StringUtils.isNotEmpty(firstname) && StringUtils.isNotEmpty(lastname)){
            q.setParameter("name", name);
        }
        q.setFirstResult((page-1) * 10);
        q.setMaxResults(pageSize);
        System.out.println("Prepared Query: " + HQL);
        return q.getResultList();
    }    
    
    public static List<RaceResult> search(Long eventId, String name, Long bib) {
        EntityManager em = RaceResult.entityManager();

        Event event = new Event();
        if (null != eventId && eventId > 0)
            event = Event.findEvent(eventId);

        String firstname = "";
        String lastname = "";
        if (name.contains(" ")) {
            firstname = name.split(" ")[0];
            lastname = name.split(" ")[1];
        }

        String HQL = "SELECT o FROM RaceResult AS o WHERE ";

        HQL += "o.timediff > 0 AND";
        
        if (null != eventId && eventId > 0)
            HQL += " o.event = :event AND ";

        if (bib != null)
            HQL += " o.bib = :bib AND ";

        if (!firstname.isEmpty() && !lastname.isEmpty()) {
            firstname += "%";
            lastname += "%";
            HQL += " LOWER(o.firstname) LIKE LOWER(:firstname) AND LOWER(o.lastname) LIKE LOWER(:lastname) ";
        } else {
            name += "%";
            HQL += " (LOWER(o.firstname) LIKE LOWER(:name) OR LOWER(o.lastname) LIKE LOWER(:name)) ";
        }

        TypedQuery<RaceResult> q = em.createQuery(HQL, RaceResult.class);

        if (null != eventId && eventId > 0)
            q.setParameter("event", event);
        if (bib != null)
            q.setParameter("bib", bib);
        if (!firstname.isEmpty() && !lastname.isEmpty()) {
            q.setParameter("firstname", firstname);
            q.setParameter("lastname", lastname);
        } else {
            q.setParameter("name", name);
        }
        q.setMaxResults(100);

        return q.getResultList();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class", "event").include("customFields", "splits").serialize(this);
    }

    public String toJson(boolean full) {
        return new JSONSerializer().serialize(this);
    }
    
    public static RaceResult fromJsonToRaceResult(String json) {
        return new JSONDeserializer<RaceResult>().use(null, RaceResult.class).deserialize(json);
    }

    public static String toJsonArray(Collection<RaceResult> collection) {
        return new JSONSerializer().exclude("*.class", "event").serialize(collection);
    }

    public static Collection<RaceResult> fromJsonArrayToRaceResults(String json) {
        return new JSONDeserializer<List<RaceResult>>().use(null, ArrayList.class).use("values", RaceResult.class).deserialize(json);
    }

    @Override
    public int compareTo(RaceResult other) {
        //return (int) ((other.timestart - other.timeofficial) - (this.timestart - this.timeofficial));
    	return (int) ((this.timediff - other.timediff));
    }

    public static long fromHumanTime(String humanReadable) {
    	String[] arr = humanReadable.split(":");
    	if(arr.length == 3){
    		return new Period(Integer.valueOf(arr[0]),Integer.valueOf(arr[1]),Integer.valueOf(arr[2]),0).toStandardDuration().getMillis();
    	}else if(arr.length == 2){
    		return new Period(0,Integer.valueOf(arr[0]),Integer.valueOf(arr[1]),0).toStandardDuration().getMillis();
    	}
		System.out.println("invalid human readable "+humanReadable);
        return 0;
    }

    public static long fromHumanTime(long startTime, String humanReadable) {
        return fromHumanTime(humanReadable) > 0 ? startTime + fromHumanTime(humanReadable) : 0;
    }

    public static String paceToHumanTime(long start, long finish, long meters) {
    	if(finish == 0){
    		return "N/A";
    	}
        long l = finish - start;
        
        String rtn = "";
        if(l < 0 || meters == 0) {
        	return "N/A";
        }
        double units = ((double) meters)/1760;
        long lAdjusted = (long) (l/units);
        
        int hours = (int) ((lAdjusted / 3600000));
        int minutes = (int) ((lAdjusted / 60000) % 60);
        int seconds = (int) ((lAdjusted / 1000) % 60);
        // int millis = (int) (l%100);
        if (hours > 0 && hours <= 9)
            rtn = "0" + hours + ":";
        else if (hours > 9)
            rtn = hours + ":";
        else if (hours == 0)
            rtn = "00:";
        if (minutes > 0 && minutes <= 9)
            rtn = rtn + "0" + minutes;
        else if (minutes > 9)
            rtn = rtn + "" + minutes;
        else if (minutes == 0)
            rtn = rtn + "00";
        if (seconds >= 0 && seconds <= 9)
            rtn = rtn + ":0" + seconds;
        else if (seconds > 9)
            rtn = rtn + ":" + seconds;
        return rtn;
    }    
    
    public static String toHumanTime(long start, long finish) {
    	if(finish == 0){
    		return "00:00:00";
    	}
        long l = finish - start;
        String rtn = "";
        l = Math.abs(l);
        int hours = (int) ((l / 3600000));
        int minutes = (int) ((l / 60000) % 60);
        int seconds = (int) ((l / 1000) % 60);
        // int millis = (int) (l%100);
        if (hours > 0 && hours <= 9)
            rtn = "0" + hours + ":";
        else if (hours > 9)
            rtn = hours + ":";
        else if (hours == 0)
            rtn = "00:";
        if (minutes > 0 && minutes <= 9)
            rtn = rtn + "0" + minutes;
        else if (minutes > 9)
            rtn = rtn + "" + minutes;
        else if (minutes == 0)
            rtn = rtn + "00";
        if (seconds >= 0 && seconds <= 9)
            rtn = rtn + ":0" + seconds;
        else if (seconds > 9)
            rtn = rtn + ":" + seconds;
        return rtn;
    }

    public static Long countFindRaceResultsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }
    
    public static Long countResultsForUser(UserProfile user) {
        // return all events for the sysadmin or unauthenticated users (can not edit anyways)
        if (user == null || PermissionsUtil.isSysAdmin(user))
            return RaceResult.countRaceResults();
        // get only accessible events for everyone else
        String jpaQuery = "select COUNT(r) from RaceResult r join r.event e join e.eventUserGroups eug join eug.userGroup ug join ug.userGroupUserAuthorities ugua join ugua.userAuthorities uasid join uasid.userProfile up where up.id = :user_id";

        TypedQuery<Long> q = entityManager().createQuery(jpaQuery, Long.class);
        q.setParameter("user_id", user.getId());


        return q.getSingleResult();
    }

    public static Long countFindRaceResultsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The eventType argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.eventType = :eventType", Long.class);
        q.setParameter("eventType", eventType);
        return q.getSingleResult();
    }    

    public static Long countFindValidRaceResultsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The eventType argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.eventType = :eventType AND o.timediff > 0 AND o.disqualified = 0", Long.class);
        q.setParameter("eventType", eventType);
        return q.getSingleResult();
    }     
    
    public static Long countFindValidRaceResultsInGenderForType(EventType eventType, String gender) {
        if (eventType == null)
            throw new IllegalArgumentException("The eventType argument is required");
        if (! (StringUtils.equalsIgnoreCase(gender, "M") || StringUtils.equalsIgnoreCase(gender, "F") ) ) {
        	throw new IllegalArgumentException("The gender argument is required");
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.eventType = :eventType AND o.timediff > 0 AND o.gender = :gender AND o.disqualified = 0", Long.class);
        q.setParameter("eventType", eventType);
        q.setParameter("gender", gender);
        return q.getSingleResult();
    }

    public static Long findGenderRankingForResult(RaceResult r) {
        if (r.getEventType() == null)
            throw new IllegalArgumentException("The eventType argument is required");
        if (! (StringUtils.equalsIgnoreCase(r.getGender(), "M") || StringUtils.equalsIgnoreCase(r.getGender(), "F") ) ) {
        	throw new IllegalArgumentException("Result does not have a gender");
        }
        if(r.getTimediff() == 0) {
        	throw new IllegalArgumentException("Result is missing a time");
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o "
        		+ "WHERE o.eventType = :eventType AND o.gender = :gender AND o.disqualified = 0"
        		+ "AND o.timediff <= :timediff AND o.timediff > 0", Long.class);
        q.setParameter("eventType", r.getEventType());
        q.setParameter("gender", r.getGender());
        q.setParameter("timediff", r.getTimediff());
        return q.getSingleResult();
    }    
 
    public static Long findOverallRankingForResult(RaceResult r) {
        if (r.getEventType() == null)
            throw new IllegalArgumentException("The eventType argument is required");
        if(r.getTimediff() == 0) {
        	throw new IllegalArgumentException("Result is missing a time");
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o "
        		+ "WHERE o.eventType = :eventType AND o.disqualified = 0"
        		+ "AND o.timediff <= :timediff AND o.timediff > 0", Long.class);
        q.setParameter("eventType", r.getEventType());
        q.setParameter("timediff", r.getTimediff());
        return q.getSingleResult();
    }
    
    public String computeGenderRanking() {
    	try{
    		return "" + RaceResult.findGenderRankingForResult(this) + " of " + RaceResult.countFindValidRaceResultsInGenderForType(this.getEventType(), this.gender);
    	} catch (Exception e ) {
    		e.printStackTrace();
    		return "N/A";
    	}
    }

    public String computeOverallRanking() {
    	try{
    		return "" + RaceResult.findOverallRankingForResult(this) + " of " + RaceResult.countFindValidRaceResultsByEventType(this.getEventType());
    	} catch (Exception e ) {
    		return "N/A";
    	}
    }    
    
    public static Long countFindRaceResultsByEventAndBibEquals(Event event, long bib) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event AND o.bib = :bib", Long.class);
        q.setParameter("event", event);
        q.setParameter("bib", bib);
        return q.getSingleResult();
    }
    
    public static List <Long> findBibsUsedInEvent(Event event) {
    	if (event == null)
    		throw new IllegalArgumentException("The event argument is required.");
    	EntityManager em = RaceResult.entityManager();
    	return em.createQuery("select o.bib from RaceResult o where o.event = :event", Long.class).setParameter("event", event).getResultList();
    }
    
    public static Long countFindRaceResultsByBibEquals(long bib) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.bib = :bib", Long.class);
        q.setParameter("bib", bib);
        return q.getSingleResult();
    }    

    public static Long countFindRaceResultsByEventAndFirstnameLike(Event event, String firstname) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.isEmpty())
            throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)", Long.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        return (q.getSingleResult());
    }

    public static Long countFindRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event event, String firstname, String lastname) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.isEmpty())
            throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)  AND LOWER(o.lastname) LIKE LOWER(:lastname)",
                Long.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        q.setParameter("lastname", lastname);
        return (q.getSingleResult());
    }

    public static Long countFindRaceResultsByEventAndLastnameLike(Event event, String lastname) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.lastname) LIKE LOWER(:lastname)", Long.class);
        q.setParameter("event", event);
        q.setParameter("lastname", lastname);
        return (q.getSingleResult());
    }
    
    public static long countRaceResultsCompleteByEventType(EventType eventType) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT Count(rr) FROM RaceResult rr WHERE rr.eventType = :eventType and rr.timeofficial > 0", Long.class);
        q.setParameter("eventType", eventType);
        return q.getSingleResult();
    }

    public static long countUnassignedCompleteRaceResults(Event event) {
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT Count(rr) FROM RaceResult rr WHERE rr.event = :event and rr.eventType is null and rr.timeofficial > 0", Long.class);
        q.setParameter("event", event);
        return q.getSingleResult();
    }    
    
    public static Long countFindUnassignedRaceResultsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event AND o.eventType is null", Long.class);
        q.setParameter("event", event);
        return (q.getSingleResult());
    }    
 
    public static List <RaceResult> findUnassignedRaceResultsByEventPaginated(Event event, int page, int size) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if(page < 1) {
        	page = 1;
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.eventType is null ORDER by o.bib ASC", RaceResult.class);
        q.setParameter("event", event);
        q.setFirstResult(size*(page-1));
        q.setMaxResults(size);
        return q.getResultList();
    }
    
    public static TypedQuery<RaceResult> findRaceResultsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event", RaceResult.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventType(EventType eventType) {
        if (eventType == null)
            throw new IllegalArgumentException("The event type argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.eventType = :eventType", RaceResult.class);
        q.setParameter("eventType", eventType);
        return q;
    }    
    
    public static TypedQuery<RaceResult> findRaceResultsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        String jpaQuery = "SELECT o FROM RaceResult AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RaceResult> q = em.createQuery(jpaQuery, RaceResult.class);
        q.setParameter("event", event);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndBibEquals(Event event, long bib) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.bib = :bib", RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("bib", bib);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndBibEquals(Event event, long bib, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        String jpaQuery = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND o.bib = :bib";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RaceResult> q = em.createQuery(jpaQuery, RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("bib", bib);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndFirstnameLike(Event event, String firstname, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.isEmpty())
            throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        String jpaQuery = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RaceResult> q = em.createQuery(jpaQuery, RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event event, String firstname, String lastname, String sortFieldName,
            String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.isEmpty())
            throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        String jpaQuery = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)  AND LOWER(o.lastname) LIKE LOWER(:lastname)";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RaceResult> q = em.createQuery(jpaQuery, RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        q.setParameter("lastname", lastname);
        return q;
    }

    public static TypedQuery<RaceResult> findRaceResultsByEventAndLastnameLike(Event event, String lastname, String sortFieldName, String sortOrder) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        if (lastname == null || lastname.isEmpty())
            throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        String jpaQuery = "SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.lastname) LIKE LOWER(:lastname)";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RaceResult> q = em.createQuery(jpaQuery, RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("lastname", lastname);
        return q;
    }
    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("event", "userProfile", "raceImages", "bib", "firstname", "lastname", 
            "age", "gender", "eventType", "timeoverall", "timesplit", "timestart", "timerun", "timeswim",
            "timeofficial", "timeofficialdisplay", "rankoverall", "rankage", "rankgender", "rankclass", "medal",
            "city", "state", "country", "created", "updated");

    public static EntityManager entityManager() {
        EntityManager em = new RaceResult().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countRaceResults() {
        return entityManager().createQuery("SELECT COUNT(o) FROM RaceResult o", Long.class).getSingleResult();
    }

    public static List<RaceResult> findAllRaceResults() {
        return entityManager().createQuery("SELECT o FROM RaceResult o", RaceResult.class).getResultList();
    }

    public static List<RaceResult> findUnlicensedResults() {
    	try {
            EntityManager em = RaceResult.entityManager();
            TypedQuery <RaceResult> q = em.createQuery("SELECT o FROM RaceResult o where o.event != :event", RaceResult.class);
            System.out.println("get");
            q.setParameter("event", Event.findEventByNameEquals("Unassigned Results"));
            List <RaceResult> rrs= q.getResultList();
            List <RaceResult> returnList = new LinkedList <RaceResult> ();
            for(RaceResult rr : rrs) {
            	if(!rr.isLicensed()) {
            		returnList.add(rr);
            	}
            }
            return returnList;
    	} catch(Exception e) {
    		System.out.println("exception");
            List <RaceResult> returnList = new LinkedList <RaceResult> ();
            for(RaceResult rr : RaceResult.findAllRaceResults()) {
            	if(!rr.isLicensed()) {
            		returnList.add(rr);
            	}
            }
            return returnList;
    	}
    }
    
    public static List<String> findTeamsInEvent(Event e) {
        EntityManager em = RaceResult.entityManager();
        return em.createQuery("SELECT distinct r.team FROM RaceResult AS r where r.event = :event", String.class)
        		.setParameter("event", e).getResultList();
    }
    
    public static List<RaceResult> findAllRaceResults(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RaceResult o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RaceResult.class).getResultList();
    }
    
    public static RaceResult findFullRaceResult(Long id) {
    	if (id == null)
    		return null;
    	RaceResult result = entityManager().createQuery("Select o FROM RaceResult o where o.id = :id", RaceResult.class)
    			.setParameter("id", id).getSingleResult();
    	
    	Hibernate.initialize(result.getCustomFields());
    	Hibernate.initialize(result.getSplits());
    	return result;
    }

    public static RaceResult findRaceResult(Long id) {
        if (id == null)
            return null;
        return entityManager().find(RaceResult.class, id);
    }

    public static List<RaceResult> findRaceResultsByEventAndUser(Event event, UserProfile userProfile) {
    	if(event == null || userProfile == null) {
    		return null;
    	}
    	return entityManager().createQuery("SELECT o FROM RaceResult o WHERE o.event = :event AND o.userProfile = :userProfile", RaceResult.class)
    			.setParameter("event", event).setParameter("userProfile", userProfile).getResultList();
    }
    
    public static List<RaceResult> findRaceResultEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM RaceResult o", RaceResult.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<RaceResult> findRaceResultEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RaceResult o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RaceResult.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            RaceResult attached = RaceResult.findRaceResult(this.id);
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
    public RaceResult merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        RaceResult merged = this.entityManager.merge(this);
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

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Set<RaceImage> getRaceImages() {
        return this.raceImages;
    }

    public void setRaceImages(Set<RaceImage> raceImages) {
        this.raceImages = raceImages;
    }

    /**
     * Get bib number of RaceResult. This value must be non-null. Import option.
     * @return bib as a long.
     */
    public long getBib() {
        return this.bib;
    }

    /**
     * Bib number to set. This value must be non-null. Import option.
     * @param bib bib number to set
     */
    public void setBib(long bib) {
        this.bib = bib;
    }

    /**
     * Get Firstname of runner. Import option.
     * @return firstname Returns a capitalized string containing the firstname of the runner.
     */
	public String getFirstname() {
        return WordUtils.capitalizeFully(firstname);
    }

	/**
	 * Set firstname of the runner. Import option.
	 * @param firstname of runner to set.
	 */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /** Get lastname of runner with first letters of each word capitalized. Import option.
     * @return lastname of runner, with first letters capitalized of each word.
     */
	public String getLastname() {
        return WordUtils.capitalizeFully(this.lastname);
    }

	/**
	 * Set lastname of runner. Import option.
	 * @param lastname String to set as lastname
	 */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Age of runner to get, must be non-null and greater than zero. Import option.
     * @return Integer object of runner's age
     */
    public Integer getAge() {
        return this.age;
    }

    /**
     * Age of runner to set, must be non-null and greater than zero. Import option.
     * @param age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Get gender of runner. This function returns values from the set:
     * {"M" || "F" || null}
     * Import option.
     * @return String containing gender character or null.
     */
	public String getGender() {
		if(gender == null) return "";
		if(gender.equalsIgnoreCase("M")){
			return "M";
		}else if(gender.equalsIgnoreCase("F")){
			return "F";
		} else {
			return "";
		}
    }

	/**
	 * Gender to set. User values from the set {"M"||"F"||null}.
	 * Import option.
	 * @param gender gender to set
	 */
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTimesplit() {
        return this.timesplit;
    }

    public void setTimesplit(String timesplit) {
        this.timesplit = timesplit;
    }

    /**
     * Timestart is a long containing the unix timestamp of milliseconds at the time the runner started.
     * It is written to the gun time or a recorded chip time.
     * @return long containing runner's starting timestamp.
     */
    public long getTimestart() {
        return this.timestart;
    }

    /**
     * Timestart to set. Replaces the gun time when set to a new value. Import option, timeofficialdisplay
     * is generated from this.
     * @param timestart, a long containing a unix timestamp of the runner's start time.
     */
    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    /**
     * Runner's official time is stored in a unix timestamp. Import option, timeofficialdisplay is generated from this.
     * @return long containing runner's official time.
     */
    public long getTimeofficial() {
        return this.timeofficial;
    }

    public void setTimeofficial(long timeofficial) {
        this.timeofficial = timeofficial;
    }

    public void setTimeofficialdisplay(String timeofficialdisplay) {
        this.timeofficialdisplay = timeofficialdisplay;
    }

    /**
	 * @return the timeofficialimport
	 */
	public String getTimeofficialmanual() {
		return timeofficialmanual;
	}

	/**
	 * @param timeofficialmanual the manual time string to set
	 */
	public void setTimeofficialmanual(String timeofficialmanual) {
		this.timeofficialmanual = timeofficialmanual;
	}

	/**
	 * @return the timegunmanual
	 */
	public String getTimegunmanual() {
		return timegunmanual;
	}

	/**
	 * @param timegunmanual the timegunmanual to set
	 */
	public void setTimegunmanual(String timegunmanual) {
		this.timegunmanual = timegunmanual;
	}

	/**
	 * @return the timediff
	 */
	public long getTimediff() {
		return timediff;
	}

	/**
	 * @param timediff the timediff to set
	 */
	public void setTimediff(long timediff) {
		this.timediff = timediff;
	}

	public String getRankoverall() {
        return this.rankoverall;
    }

    public void setRankoverall(String rankoverall) {
        this.rankoverall = rankoverall;
    }

    public String getRankage() {
        return this.rankage;
    }

    public void setRankage(String rankage) {
        this.rankage = rankage;
    }

    public String getRankgender() {
        return this.rankgender;
    }

    public void setRankgender(String rankgender) {
        this.rankgender = rankgender;
    }

    public String getRankclass() {
        return this.rankclass;
    }

    public void setRankclass(String rankclass) {
        this.rankclass = rankclass;
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
        return WordUtils.capitalizeFully(this.country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the laps
	 */
	public Integer getLaps() {
		return laps;
	}

	/**
	 * @param laps the laps to set
	 */
	public void setLaps(Integer laps) {
		this.laps = laps;
	}

	/**
	 * @return the medal
	 */
	public String getMedal() {
		return medal;
	}

	/**
	 * @param medal the medal to set
	 */
	public void setMedal(String medal) {
		this.medal = medal;
	}

	/**
	 * Time of raceresult creation
	 * @return created a dat object generated at persist time.
	 */
	public Date getCreated() {
        return this.created;
    }

	/**
	 * Time of raceresult creation.
	 * @param created a date object generated at persist time.
	 */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Last update time of the raceresult.
     * @return Updated a date object modified at persist/update time.
     */
    public Date getUpdated() {
        return this.updated;
    }

    /**
     * Last update time of the raceresult.
     * @param updated a date object modified at persist/update time.
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

	/**
	 * @return boolean - True if the result is a licensed race result
	 */
	public boolean isLicensed() {
		return licensed;
	}

	/**
	 * @param boolean - Set true when licensing a result, false if unlicensed
	 */
	public void setLicensed(boolean licensed) {
		this.licensed = licensed;
	}

	/**
	 * @return timed whether the raceresult has recieved a time in the past
	 */
	public boolean isTimed() {
		return timed;
	}

	/**
	 * @param timed Switch to set if the raceresult has or hasn't recieved a time from an RFID unit.
	 */
	public void setTimed(boolean timed) {
		this.timed = timed;
	}

	/**
	 * @return the checkedin
	 */
	public boolean isCheckedin() {
		return checkedin;
	}

	/**
	 * @param checkedin the checkedin to set
	 */
	public void setCheckedin(boolean checkedin) {
		this.checkedin = checkedin;
	}

	/**
	 * @return the whether a runner is disqualified. Default false.
	 */
	public boolean isDisqualified() {
		return disqualified;
	}

	/**
	 * @param disqualified set a runner as disqualified.
	 */
	public void setDisqualified(boolean disqualified) {
		this.disqualified = disqualified;
	}

	/**
	 * @return the splits
	 */
	public Map<Integer, Split> getSplits() {
		return splits;
	}

	/**
	 * @param splits the splits to set
	 */
	public void setSplits(Map <Integer, Split> splits) {
		this.splits = splits;
	}

	/**
	 * @return the customFields
	 */
	public Map<String,String> getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(Map <String, String> customFields) {
		this.customFields = customFields;
	}
}
