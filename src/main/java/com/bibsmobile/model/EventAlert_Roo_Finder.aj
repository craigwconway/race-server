// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAlert;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect EventAlert_Roo_Finder {
    
    public static Long EventAlert.countFindEventAlertsByEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventAlert.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM EventAlert AS o WHERE o.event = :event", Long.class);
        q.setParameter("event", event);
        return ((Long) q.getSingleResult());
    }
    
    public static TypedQuery<EventAlert> EventAlert.findEventAlertsByEvent(Event event) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventAlert.entityManager();
        TypedQuery<EventAlert> q = em.createQuery("SELECT o FROM EventAlert AS o WHERE o.event = :event", EventAlert.class);
        q.setParameter("event", event);
        return q;
    }
    
    public static TypedQuery<EventAlert> EventAlert.findEventAlertsByEvent(Event event, String sortFieldName, String sortOrder) {
        if (event == null) throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventAlert.entityManager();
        String jpaQuery = "SELECT o FROM EventAlert AS o WHERE o.event = :event";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<EventAlert> q = em.createQuery(jpaQuery, EventAlert.class);
        q.setParameter("event", event);
        return q;
    }
    
}