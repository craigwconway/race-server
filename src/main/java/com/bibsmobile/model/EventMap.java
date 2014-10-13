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
@RooJpaActiveRecord(finders = { "findEventMapsByEvent" })
public class EventMap {

    /**
     */
    private String url;

    @ManyToOne
    private Event event;

    @JSON(include = false)
    public Event getEvent(){
        return event;
    }

    public static TypedQuery<EventMap> findEventMapsByEventId(Long eventId) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventMap> q = em.createQuery("SELECT em FROM EventMap AS em WHERE em.event.id = :eventId", EventMap.class);
        q.setParameter("eventId", eventId);
        return q;
    }
}
