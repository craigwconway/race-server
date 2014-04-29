// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.EventCartItem;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect EventCartItem_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager EventCartItem.entityManager;
    
    public static final EntityManager EventCartItem.entityManager() {
        EntityManager em = new EventCartItem().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long EventCartItem.countEventCartItems() {
        return entityManager().createQuery("SELECT COUNT(o) FROM EventCartItem o", Long.class).getSingleResult();
    }
    
    public static List<EventCartItem> EventCartItem.findAllEventCartItems() {
        return entityManager().createQuery("SELECT o FROM EventCartItem o", EventCartItem.class).getResultList();
    }
    
    public static EventCartItem EventCartItem.findEventCartItem(Long id) {
        if (id == null) return null;
        return entityManager().find(EventCartItem.class, id);
    }
    
    public static List<EventCartItem> EventCartItem.findEventCartItemEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM EventCartItem o", EventCartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void EventCartItem.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void EventCartItem.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            EventCartItem attached = EventCartItem.findEventCartItem(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void EventCartItem.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void EventCartItem.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public EventCartItem EventCartItem.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        EventCartItem merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}