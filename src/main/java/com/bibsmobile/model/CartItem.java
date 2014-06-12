package com.bibsmobile.model;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(finders = { "findCartItemsByEventCartItem", "findCartItemsByCreatedGreaterThan" })
public class CartItem {

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private EventCartItem eventCartItem;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="user_profile_id")
    private UserProfile userProfile;

    private int quantity;

    private Date created;

    private Date updated;

    private String comment;

    private String coupon;

    public static TypedQuery<CartItem> findCartItemsByEventCartItems(List<EventCartItem> eventCartItems, Date greaterThan, Date lessThan) {
        if (eventCartItems == null) throw new IllegalArgumentException("The eventCartItems argument is required");
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o WHERE o.eventCartItem IN (:eventCartItems)";
        if (greaterThan != null) {
            jpaQuery += " AND o.created > :fromDate";
        }
        if (lessThan != null) {
            jpaQuery += " AND o.created < :toDate";
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        q.setParameter("eventCartItems", eventCartItems);
        if (greaterThan != null) {
            q.setParameter("fromDate", greaterThan);
        }
        if (lessThan != null) {
            q.setParameter("toDate", lessThan);
        }
        return q;
    }

    public static List<CartItem> findAllCartItems(Date from, Date to) {
        EntityManager em = CartItem.entityManager();
        String jpaQuery = "SELECT o FROM CartItem AS o";
        if (from != null && to != null) {
            jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created > :fromDate AND o.created < :toDate";
        } else if (from != null) {
            jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created > :fromDate";
        } else if (to != null) {
            jpaQuery = "SELECT o FROM CartItem AS o WHERE o.created < :toDate";
        }
        TypedQuery<CartItem> q = em.createQuery(jpaQuery, CartItem.class);
        if (from != null) {
            q.setParameter("fromDate", from);
        }
        if (to != null) {
            q.setParameter("toDate", to);
        }
        return q.getResultList();
    }

}
