package com.bibsmobile.model;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
@RooJpaActiveRecord()
public class Event {
    @NotNull
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "SS")
    private Date timeStart;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "SS")
    private Date timeEnd;
    private int featured;
    private String city;
    private String state;
    private String lattitude;
    private String longitude;
    private String type;
    private String website;
    private String phone;
    private String email;
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
    private String results;
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
    private String  merchandise;
    private String  beachEvents;
    private String  shuttles;
    private String  courseRules;
    private Long timerStart; 

    @Override
    public String toString() {
        return name;
    }

	public static TypedQuery<Event> findEventsByFeaturedGreaterThan(int featured, int page, int size) {
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery(
        		"SELECT o FROM Event AS o WHERE o.featured > :featured order by o.featured ASC", Event.class);
        q.setParameter("featured", featured);
        q.setFirstResult((page-1)*size);
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
        q.setFirstResult((page-1)*size);
        q.setMaxResults(size);
        return q;
    }

	public static TypedQuery<Event> findEventsByTimeStartGreaterThanAndFeaturedEquals(Date timeStart, int featured, int page, int size) {
        if (timeStart == null) throw new IllegalArgumentException("The timeStart argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery(
        		"SELECT o FROM Event AS o WHERE o.timeStart >= :timeStart AND o.featured = :featured " +
        		"ORDER BY o.timeStart ASC", Event.class);
        q.setParameter("timeStart", timeStart);
        q.setParameter("featured", featured);
        q.setFirstResult((page-1)*size);
        q.setMaxResults(size);
        return q;
    }

	public static TypedQuery<Event> findEventsByTimeStartLessThan(Date timeStart, int page, int size) {
        if (timeStart == null) throw new IllegalArgumentException("The timeStart argument is required");
        EntityManager em = Event.entityManager();
        TypedQuery<Event> q = em.createQuery(
        		"SELECT o FROM Event AS o WHERE o.timeStart < :timeStart " +
        		"ORDER BY o.timeStart DESC", Event.class);
        q.setParameter("timeStart", timeStart);
        q.setFirstResult((page-1)*size);
        q.setMaxResults(size);
        return q;
    }

}
