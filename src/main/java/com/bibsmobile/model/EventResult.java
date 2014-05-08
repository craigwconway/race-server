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
@RooJpaActiveRecord(finders = { "findEventResultsByEvent" })
public class EventResult {

    /**
     */
    private String text;

    @ManyToOne
    private Event event;

    @JSON(include = false)
    public Event getEvent(){
        return event;
    }

    public static TypedQuery<EventResult> findEventResultsByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventResult> q = em.createQuery("SELECT er FROM EventResult AS er WHERE er.event.id = :eventId", EventResult.class);
        q.setParameter("eventId", eventId);
        return q;
    }
}
