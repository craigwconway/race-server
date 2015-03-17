package com.bibsmobile.model;

import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Configurable
@Entity
public class EventCartItemCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private EventCartItem eventCartItem;

    private String code;
    private int discount;
    private int numberAvailable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventCartItem getEventCartItem() {
        return eventCartItem;
    }

    public void setEventCartItem(EventCartItem eventCartItem) {
        this.eventCartItem = eventCartItem;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getNumberAvailable() {
        return numberAvailable;
    }

    public void setNumberAvailable(int numberAvailable) {
        this.numberAvailable = numberAvailable;
    }
}
