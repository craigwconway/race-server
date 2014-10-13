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
@RooJpaActiveRecord(finders = { "findEventAlertsByEvent" })
public class EventAlert {

    /**
     */
    private String text;

    @ManyToOne
    private Event event;

    public static TypedQuery<EventAlert> findEventAlertsByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventAlert> q = em.createQuery("SELECT ea FROM EventAlert AS ea WHERE ea.event.id = :eventId", EventAlert.class);
        q.setParameter("eventId", eventId);
        return q;
    }

    @JSON(include = false)
    public Event getEvent(){
        return event;
    }
}


