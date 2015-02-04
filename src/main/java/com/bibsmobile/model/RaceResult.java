package com.bibsmobile.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.joda.time.DateTime;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class RaceResult implements Comparable<RaceResult> {

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @ManyToOne
    private UserProfile userProfile;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "raceResult")
    private Set<RaceImage> raceImage;

    @NotNull
    @Index(name="bib_index") // search field
    private long bib;

    @Index(name="name_index") // search field
    private String firstname;

    @Index(name="name_index") // search field
    private String lastname;

    private String middlename;

    private String middlename2;

    private Integer age;

    private String gender;

    private String type;

    private String time5k;

    private String time10k;

    private String time15k;

    private String timeoverall;

    private String timegun;

    private String timechip;

    private String timesplit;

    private long timestart;

    private String timerun;

    private String timeswim;

    private String timetrans1;

    private String timetrans2;

    private String timebike;

    private String timepace;

    private long timeofficial;

    private String timeofficialdisplay;

    private String rankoverall;

    private String rankage;

    private String rankgender;

    private String rankclass;

    private String medal1;

    private String medal2;

    private String medal3;

    private String city;

    private String state;

    private String country;

    private String fullname;

    private String lisencenumber;

    private String laps;

    private String award;

    private long timer = 0l;

    private String timesplit1;

    private String timesplit2;

    private String timesplit3;

    private String timesplit4;

    private String timemile1;

    private String timemile2;

    private String timemile3;

    private String timemile4;
    
    private boolean licensed;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date updated;

    @PreUpdate
    protected void onUpdate() {
        if (this.created == null)
            this.created = new Date();
        this.updated = new Date();
    }

    @Override
    public String toString() {
        return this.event + " " + this.bib + " " + this.firstname + " " + this.lastname;
    }

    public String getTimeofficialdisplay() {
        if (this.timestart == 0 || this.timeofficial == 0 || this.licensed == false)
            return "";
        return RaceResult.toHumanTime(this.timestart, this.timeofficial);
    }

    public String valueOfTimeofficialdisplay() {
    	return this.timeofficialdisplay;
    }
    /**
     * Over-write current fields with non-null values from new object
     * 
     * @param raceResult
     */
    public void merge(final RaceResult raceResult) {
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(RaceResult.class);
            PropertyDescriptor[] pDescArr = info.getPropertyDescriptors();
            for (PropertyDescriptor pDesc : pDescArr) {
                System.out.println(pDesc.getName());
                Object getterReturn = pDesc.getReadMethod().invoke(raceResult, (Object) null);
                System.out.println(getterReturn);
                if (null != getterReturn && null != pDesc.getWriteMethod()) {
                    pDesc.getWriteMethod().invoke(this, getterReturn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return new JSONSerializer().exclude("*.class", "event").serialize(this);
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
        return (int) ((other.timestart - other.timeofficial) - (this.timestart - this.timeofficial));
    }

    public static String toHumanTime(long start, long finish) {
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

    public static Long countFindRaceResultsByEventAndBibEquals(Event event, long bib) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM RaceResult AS o WHERE o.event = :event AND o.bib = :bib", Long.class);
        q.setParameter("event", event);
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

    public static TypedQuery<RaceResult> findRaceResultsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event", RaceResult.class);
        q.setParameter("event", event);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        RaceResult rhs = (RaceResult) obj;
        return new EqualsBuilder().append(this.age, rhs.age).append(this.award, rhs.award).append(this.bib, rhs.bib).append(this.city, rhs.city).append(this.country, rhs.country)
                .append(this.created, rhs.created).append(this.event, rhs.event).append(this.firstname, rhs.firstname).append(this.fullname, rhs.fullname)
                .append(this.gender, rhs.gender).append(this.id, rhs.id).append(this.laps, rhs.laps).append(this.lastname, rhs.lastname)
                .append(this.lisencenumber, rhs.lisencenumber).append(this.medal1, rhs.medal1).append(this.medal2, rhs.medal2).append(this.medal3, rhs.medal3)
                .append(this.middlename, rhs.middlename).append(this.middlename2, rhs.middlename2).append(this.rankage, rhs.rankage).append(this.rankclass, rhs.rankclass)
                .append(this.rankgender, rhs.rankgender).append(this.rankoverall, rhs.rankoverall).append(this.state, rhs.state).append(this.time10k, rhs.time10k)
                .append(this.time15k, rhs.time15k).append(this.time5k, rhs.time5k).append(this.timebike, rhs.timebike).append(this.timechip, rhs.timechip)
                .append(this.timegun, rhs.timegun).append(this.timemile1, rhs.timemile1).append(this.timemile2, rhs.timemile2).append(this.timemile3, rhs.timemile3)
                .append(this.timemile4, rhs.timemile4).append(this.timeofficial, rhs.timeofficial).append(this.timeofficialdisplay, rhs.timeofficialdisplay)
                .append(this.timeoverall, rhs.timeoverall).append(this.timepace, rhs.timepace).append(this.timer, rhs.timer).append(this.timerun, rhs.timerun)
                .append(this.timesplit, rhs.timesplit).append(this.timesplit1, rhs.timesplit1).append(this.timesplit2, rhs.timesplit2).append(this.timesplit3, rhs.timesplit3)
                .append(this.timesplit4, rhs.timesplit4).append(this.timestart, rhs.timestart).append(this.timeswim, rhs.timeswim).append(this.timetrans1, rhs.timetrans1)
                .append(this.timetrans2, rhs.timetrans2).append(this.type, rhs.type).append(this.updated, rhs.updated).append(this.userProfile, rhs.userProfile).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.age).append(this.award).append(this.bib).append(this.city).append(this.country).append(this.created).append(this.event)
                .append(this.firstname).append(this.fullname).append(this.gender).append(this.id).append(this.laps).append(this.lastname).append(this.lisencenumber)
                .append(this.medal1).append(this.medal2).append(this.medal3).append(this.middlename).append(this.middlename2).append(this.rankage).append(this.rankclass)
                .append(this.rankgender).append(this.rankoverall).append(this.state).append(this.time10k).append(this.time15k).append(this.time5k).append(this.timebike)
                .append(this.timechip).append(this.timegun).append(this.timemile1).append(this.timemile2).append(this.timemile3).append(this.timemile4).append(this.timeofficial)
                .append(this.timeofficialdisplay).append(this.timeoverall).append(this.timepace).append(this.timer).append(this.timerun).append(this.timesplit)
                .append(this.timesplit1).append(this.timesplit2).append(this.timesplit3).append(this.timesplit4).append(this.timestart).append(this.timeswim)
                .append(this.timetrans1).append(this.timetrans2).append(this.type).append(this.updated).append(this.userProfile).toHashCode();
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = Arrays.asList("event", "userProfile", "raceImage", "bib", "firstname", "lastname", "middlename",
            "middlename2", "age", "gender", "type", "time5k", "time10k", "time15k", "timeoverall", "timegun", "timechip", "timesplit", "timestart", "timerun", "timeswim",
            "timetrans1", "timetrans2", "timebike", "timepace", "timeofficial", "timeofficialdisplay", "rankoverall", "rankage", "rankgender", "rankclass", "medal1", "medal2",
            "medal3", "city", "state", "country", "fullname", "lisencenumber", "laps", "award", "timer", "timesplit1", "timesplit2", "timesplit3", "timesplit4", "timemile1",
            "timemile2", "timemile3", "timemile4", "created", "updated");

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

    public static RaceResult findRaceResult(Long id) {
        if (id == null)
            return null;
        return entityManager().find(RaceResult.class, id);
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

    public Set<RaceImage> getRaceImage() {
        return this.raceImage;
    }

    public void setRaceImage(Set<RaceImage> raceImage) {
        this.raceImage = raceImage;
    }

    public long getBib() {
        return this.bib;
    }

    public void setBib(long bib) {
        this.bib = bib;
    }

	public String getFirstname() {
        return WordUtils.capitalizeFully(firstname);
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

	public String getLastname() {
        return WordUtils.capitalizeFully(this.lastname);
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

	public String getMiddlename() {
        return WordUtils.capitalizeFully(this.middlename);
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getMiddlename2() {
        return this.middlename2;
    }

    public void setMiddlename2(String middlename2) {
        this.middlename2 = middlename2;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

	public String getGender() {
		if(null==gender || gender.equalsIgnoreCase("M")){
			return "M";
		}else{
			return "F";
		}
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime5k() {
        return this.time5k;
    }

    public void setTime5k(String time5k) {
        this.time5k = time5k;
    }

    public String getTime10k() {
        return this.time10k;
    }

    public void setTime10k(String time10k) {
        this.time10k = time10k;
    }

    public String getTime15k() {
        return this.time15k;
    }

    public void setTime15k(String time15k) {
        this.time15k = time15k;
    }

    public String getTimeoverall() {
        return this.timeoverall;
    }

    public void setTimeoverall(String timeoverall) {
        this.timeoverall = timeoverall;
    }

    public String getTimegun() {
        return this.timegun;
    }

    public void setTimegun(String timegun) {
        this.timegun = timegun;
    }

    public String getTimechip() {
        return this.timechip;
    }

    public void setTimechip(String timechip) {
        this.timechip = timechip;
    }

    public String getTimesplit() {
        return this.timesplit;
    }

    public void setTimesplit(String timesplit) {
        this.timesplit = timesplit;
    }

    public long getTimestart() {
        return this.timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public String getTimerun() {
        return this.timerun;
    }

    public void setTimerun(String timerun) {
        this.timerun = timerun;
    }

    public String getTimeswim() {
        return this.timeswim;
    }

    public void setTimeswim(String timeswim) {
        this.timeswim = timeswim;
    }

    public String getTimetrans1() {
        return this.timetrans1;
    }

    public void setTimetrans1(String timetrans1) {
        this.timetrans1 = timetrans1;
    }

    public String getTimetrans2() {
        return this.timetrans2;
    }

    public void setTimetrans2(String timetrans2) {
        this.timetrans2 = timetrans2;
    }

    public String getTimebike() {
        return this.timebike;
    }

    public void setTimebike(String timebike) {
        this.timebike = timebike;
    }

    public String getTimepace() {
        return this.timepace;
    }

    public void setTimepace(String timepace) {
        this.timepace = timepace;
    }

    public long getTimeofficial() {
        return this.timeofficial;
    }

    public void setTimeofficial(long timeofficial) {
        this.timeofficial = timeofficial;
    }

    public void setTimeofficialdisplay(String timeofficialdisplay) {
        this.timeofficialdisplay = timeofficialdisplay;
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

    public String getMedal1() {
        return this.medal1;
    }

    public void setMedal1(String medal1) {
        this.medal1 = medal1;
    }

    public String getMedal2() {
        return this.medal2;
    }

    public void setMedal2(String medal2) {
        this.medal2 = medal2;
    }

    public String getMedal3() {
        return this.medal3;
    }

    public void setMedal3(String medal3) {
        this.medal3 = medal3;
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

	public String getFullname() {
        return WordUtils.capitalizeFully(this.fullname);
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLisencenumber() {
        return this.lisencenumber;
    }

    public void setLisencenumber(String lisencenumber) {
        this.lisencenumber = lisencenumber;
    }

    public String getLaps() {
        return this.laps;
    }

    public void setLaps(String laps) {
        this.laps = laps;
    }

    public String getAward() {
        return this.award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public long getTimer() {
        return this.timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public String getTimesplit1() {
        return this.timesplit1;
    }

    public void setTimesplit1(String timesplit1) {
        this.timesplit1 = timesplit1;
    }

    public String getTimesplit2() {
        return this.timesplit2;
    }

    public void setTimesplit2(String timesplit2) {
        this.timesplit2 = timesplit2;
    }

    public String getTimesplit3() {
        return this.timesplit3;
    }

    public void setTimesplit3(String timesplit3) {
        this.timesplit3 = timesplit3;
    }

    public String getTimesplit4() {
        return this.timesplit4;
    }

    public void setTimesplit4(String timesplit4) {
        this.timesplit4 = timesplit4;
    }

    public String getTimemile1() {
        return this.timemile1;
    }

    public void setTimemile1(String timemile1) {
        this.timemile1 = timemile1;
    }

    public String getTimemile2() {
        return this.timemile2;
    }

    public void setTimemile2(String timemile2) {
        this.timemile2 = timemile2;
    }

    public String getTimemile3() {
        return this.timemile3;
    }

    public void setTimemile3(String timemile3) {
        this.timemile3 = timemile3;
    }

    public String getTimemile4() {
        return this.timemile4;
    }

    public void setTimemile4(String timemile4) {
        this.timemile4 = timemile4;
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
}
