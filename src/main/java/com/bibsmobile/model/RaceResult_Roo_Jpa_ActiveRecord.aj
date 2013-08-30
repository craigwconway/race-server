// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.RaceResult;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect RaceResult_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager RaceResult.entityManager;
    
    public static final EntityManager RaceResult.entityManager() {
        EntityManager em = new RaceResult().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long RaceResult.countRaceResults() {
        return entityManager().createQuery("SELECT COUNT(o) FROM RaceResult o", Long.class).getSingleResult();
    }
    
    public static List<RaceResult> RaceResult.findAllRaceResults() {
        return entityManager().createQuery("SELECT o FROM RaceResult o", RaceResult.class).getResultList();
    }
    
    public static RaceResult RaceResult.findRaceResult(Long id) {
        if (id == null) return null;
        return entityManager().find(RaceResult.class, id);
    }
    
    public static List<RaceResult> RaceResult.findRaceResultEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM RaceResult o", RaceResult.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void RaceResult.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void RaceResult.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            RaceResult attached = RaceResult.findRaceResult(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void RaceResult.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void RaceResult.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public RaceResult RaceResult.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        RaceResult merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
