package com.bibsmobile.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
@RooJpaActiveRecord(finders = { "findRaceResultsByEvent", "findRaceResultsByEventAndBibEquals", 
		"findRaceResultsByEventAndFirstnameLike", "findRaceResultsByEventAndLastnameLike",
		"findRaceResultsByEventAndFirstnameLikeAndLastnameLike"})
public class RaceResult implements Comparable<RaceResult>{

    @ManyToOne
    private Event event;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "raceResult")
	private Set<RaceImage> raceImage;

    @NotNull 
    private String bib;

    private String firstname;

    private String lastname;

    private String middlename;

    private String middlename2;

    private String age;

    private String gender;

    private String type;

    private String time5k;
    
    private String time10k;
    
    private String time15k;
    
    private String timeoverall;

    private String timegun;

    private String timechip;

    private String timesplit;
    
    private String timestart;

    private String timerun;

    private String timeswim;

    private String timetrans1;

    private String timetrans2;

    private String timebike;

    private String timepace;

    private String timeofficial;

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

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style="SS")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style="SS")
    private Date updated;
    
    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
    
    @Override
    public String toString(){
    	return event.toString() + " " + bib + " " + firstname + " " + lastname;
    }
    
    /**
     * Over-write current fields with non-null values from new object
     * @param raceResult
     */
    public void merge(final RaceResult raceResult){
    	BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo( RaceResult.class );
	        PropertyDescriptor pDescArr[] = info.getPropertyDescriptors();
	        for(PropertyDescriptor pDesc : pDescArr){
	        	System.out.println(pDesc.getName());
	        	Object getterReturn = pDesc.getReadMethod().invoke(raceResult, null);
	        	System.out.println(getterReturn);
	            if(null!=getterReturn && null != pDesc.getWriteMethod()){
	            	pDesc.getWriteMethod().invoke(this, getterReturn);
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static TypedQuery<RaceResult> findRaceResultsByEventAndFirstnameLike(Event event, String firstname) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.length() == 0) throw new IllegalArgumentException("The firstname argument is required");
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
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        if (firstname == null || firstname.length() == 0) throw new IllegalArgumentException("The firstname argument is required");
        firstname = firstname.replace('*', '%');
        if (firstname.charAt(0) != '%') {
            firstname = "%" + firstname;
        }
        if (firstname.charAt(firstname.length() - 1) != '%') {
            firstname = firstname + "%";
        }
        if (lastname == null || lastname.length() == 0) throw new IllegalArgumentException("The lastname argument is required");
        lastname = lastname.replace('*', '%');
        if (lastname.charAt(0) != '%') {
            lastname = "%" + lastname;
        }
        if (lastname.charAt(lastname.length() - 1) != '%') {
            lastname = lastname + "%";
        }
        EntityManager em = RaceResult.entityManager();
        TypedQuery<RaceResult> q = em.createQuery("SELECT o FROM RaceResult AS o WHERE o.event = :event AND LOWER(o.firstname) LIKE LOWER(:firstname)  AND LOWER(o.lastname) LIKE LOWER(:lastname)", RaceResult.class);
        q.setParameter("event", event);
        q.setParameter("firstname", firstname);
        q.setParameter("lastname", lastname);
        return q;
    }

	public static TypedQuery<RaceResult> findRaceResultsByEventAndLastnameLike(Event event, String lastname) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        if (lastname == null || lastname.length() == 0) throw new IllegalArgumentException("The lastname argument is required");
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

	public String toJson() {
        return new JSONSerializer().exclude("*.class","event").serialize(this);
    }

	public static RaceResult fromJsonToRaceResult(String json) {
        return new JSONDeserializer<RaceResult>().use(null, RaceResult.class).deserialize(json);
    }

	public static String toJsonArray(Collection<RaceResult> collection) {
        return new JSONSerializer().exclude("*.class","event").serialize(collection);
    }

	public static Collection<RaceResult> fromJsonArrayToRaceResults(String json) {
        return new JSONDeserializer<List<RaceResult>>().use(null, ArrayList.class).use("values", RaceResult.class).deserialize(json);
    }
	
	public int compareTo(RaceResult other) {
		long val = timer - other.timer;
		return (int) val;
	}

}
