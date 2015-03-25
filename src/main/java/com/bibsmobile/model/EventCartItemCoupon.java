package com.bibsmobile.model;

import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import java.util.List;

@Configurable
@Entity
public class EventCartItemCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Event event;

    private String code;
    private Long discountAbsolute; // is an object to allow for null
    private Double discountRelative; // is an object to allow for null
    private int numberAvailable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getDiscountAbsolute() {
        return discountAbsolute;
    }

    public void setDiscountAbsolute(long discount) {
        this.discountAbsolute = discount;
    }

    public double getDiscountRelative() {
        return discountRelative;
    }

    public void setDiscountRelative(double discount) {
        this.discountRelative = discount;
    }

    public int getNumberAvailable() {
        return numberAvailable;
    }

    public void setNumberAvailable(int numberAvailable) {
        this.numberAvailable = numberAvailable;
    }

    public long getDiscount(long originalPrice) {
        // check that this is valid
        if (this.discountAbsolute == null && this.discountRelative == null)
            throw new IllegalStateException("No discount set");
        else if(this.discountAbsolute != null && this.discountRelative != null)
            throw new IllegalStateException("Both discounts set");
        // calculate new price
        if (this.discountAbsolute != null) {
            return this.discountAbsolute;
        } else if (this.discountRelative != null) {
            return (int)Math.floor(originalPrice * this.discountRelative);
        } else {
            throw new IllegalStateException("How did I get here?");
        }
    }

    public static EventCartItemCoupon findCouponByCode(Event event, String code) {
        if (event == null || code == null)
            throw new IllegalArgumentException("The event and code arguments are required");
        EntityManager em = Cart.entityManager();
        TypedQuery<EventCartItemCoupon> q = em.createQuery(
                "SELECT c FROM EventCartItemCoupon AS c WHERE c.event = :event AND c.code = :code", EventCartItemCoupon.class);
        q.setParameter("event", event);
        q.setParameter("code", code);
        List<EventCartItemCoupon> resultList = q.getResultList();
        if (resultList.size() != 1)
            return null;
        else
            return resultList.get(0);
    }
}
