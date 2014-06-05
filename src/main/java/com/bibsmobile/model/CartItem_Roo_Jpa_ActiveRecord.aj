// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.CartItem;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect CartItem_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager CartItem.entityManager;
    
    public static final List<String> CartItem.fieldNames4OrderClauseFilter = java.util.Arrays.asList("cart", "eventCartItem", "userProfile", "quantity", "created", "updated", "comment", "coupon");
    
    public static final EntityManager CartItem.entityManager() {
        EntityManager em = new CartItem().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long CartItem.countCartItems() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CartItem o", Long.class).getSingleResult();
    }
    
    public static List<CartItem> CartItem.findAllCartItems() {
        return entityManager().createQuery("SELECT o FROM CartItem o", CartItem.class).getResultList();
    }
    
    public static List<CartItem> CartItem.findAllCartItems(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM CartItem o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, CartItem.class).getResultList();
    }
    
    public static CartItem CartItem.findCartItem(Long id) {
        if (id == null) return null;
        return entityManager().find(CartItem.class, id);
    }
    
    public static List<CartItem> CartItem.findCartItemEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM CartItem o", CartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<CartItem> CartItem.findCartItemEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM CartItem o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, CartItem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void CartItem.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void CartItem.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            CartItem attached = CartItem.findCartItem(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void CartItem.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void CartItem.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public CartItem CartItem.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        CartItem merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
