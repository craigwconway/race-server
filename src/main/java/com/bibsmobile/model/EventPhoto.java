package com.bibsmobile.model;
import flexjson.JSON;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.json.RooJson;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findEventPhotoesByEvent" })
public class EventPhoto {

    /**
     */
    private String url;

    @ManyToOne
    private Event event;

    @JSON(include = false)
    public Event getEvent(){
        return event;
    }

    public static TypedQuery<EventPhoto> findEventPhotosByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventPhoto> q = em.createQuery("SELECT ep FROM EventPhoto AS ep WHERE ep.event.id = :eventId", EventPhoto.class);
        q.setParameter("eventId", eventId);
        return q;
    }
}
