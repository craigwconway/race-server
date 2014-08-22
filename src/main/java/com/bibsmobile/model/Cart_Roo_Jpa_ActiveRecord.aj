// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Cart;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Cart_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Cart.entityManager;
    
    public static final List<String> Cart.fieldNames4OrderClauseFilter = java.util.Arrays.asList("NEW", "SAVED", "PROCESSING", "COMPLETE", "REFUND_REQUEST", "REFUNDED", "user", "cartItems", "shipping", "total", "created", "updated", "status", "coupons", "timeout");
    
    public static final EntityManager Cart.entityManager() {
        EntityManager em = new Cart().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Cart.countCarts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Cart o", Long.class).getSingleResult();
    }
    
    public static List<Cart> Cart.findAllCarts() {
        return entityManager().createQuery("SELECT o FROM Cart o", Cart.class).getResultList();
    }
    
    public static List<Cart> Cart.findAllCarts(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Cart o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Cart.class).getResultList();
    }
    
    public static Cart Cart.findCart(Long id) {
        if (id == null) return null;
        return entityManager().find(Cart.class, id);
    }
    
    public static List<Cart> Cart.findCartEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Cart o", Cart.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Cart> Cart.findCartEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Cart o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Cart.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Cart.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Cart.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Cart attached = Cart.findCart(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Cart.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Cart.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Cart Cart.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Cart merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
