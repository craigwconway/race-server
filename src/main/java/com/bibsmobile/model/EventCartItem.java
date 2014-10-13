package com.bibsmobile.model;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import javax.persistence.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import javax.validation.constraints.NotNull;

import java.util.HashSet;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(finders = { "findEventCartItemsByEvent", "findEventCartItemsByNameEquals", "findEventCartItemsByType" })
public class EventCartItem {

    @ManyToOne
    private Event event;

    private String name;

    private String description;

    private double price;

    private int available;

    private int purchased;

    private String coupon;
    
    private String eventType;

    private double couponPrice;

    private int couponsAvailable;

    private int couponsUsed;

    private boolean timeLimit;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeStart;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM/dd/yyyy h:mm:ss a")
    private Date timeEnd;

    /**
     */
    @NotNull
    @Enumerated
    private EventCartItemTypeEnum type;

    /**
     */
    private double donationAmount;

    /**
     */
    private String charityName;

    /**
     */
    private String tshirtSizes;

    /**
     */
    private String tshirtColors;

    /**
     */
    private String tshirtImageUrls;

    /**
     */
    private int minAge;

    @OneToMany(fetch = FetchType.EAGER, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "eventCartItem")
    private Set<EventCartItemPriceChange> priceChanges;

    /**
     */
    private int maxAge;

    /**
     */
    @Enumerated
    private EventCartItemGenderEnum gender;

    @Transient
    private Date birthDate;
    @Transient
    private String email;
    @Transient
    private String phone;
    @Transient
    private String addressLine1;
    @Transient
    private String addressLine2;
    @Transient
    private String zipCode;
    @Transient
    private String emergencyContactName;
    @Transient
    private String emergencyContactPhone;
    @Transient
    private String hearFrom;


    public double getActualPrice() {
        if (CollectionUtils.isEmpty(priceChanges)) {
            return price;
        }
        Date now = new Date();
        for (EventCartItemPriceChange priceChange : priceChanges) {
            if (priceChange.getStartDate() != null && priceChange.getEndDate() != null) {
                if (now.after(priceChange.getStartDate()) && now.before(priceChange.getEndDate())) {
                    return priceChange.getPrice();
                }
            }
        }
        return price;
    }
    public static TypedQuery<EventCartItem> findEventCartItemsByEvents(List<Event> events) {
        if (events == null) throw new IllegalArgumentException("The events argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<EventCartItem> q = em.createQuery("SELECT o FROM EventCartItem AS o WHERE o.event IN (:events)", EventCartItem.class);
        q.setParameter("events", events);
        return q;
    }

    
    public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static EventCartItem fromJsonToEventCartItem(String json) {
        return new JSONDeserializer<EventCartItem>()
        .use(null, EventCartItem.class).deserialize(json);
    }
    
    public static Collection<EventCartItem> fromJsonArrayToEventCartItems(String json) {
        return new JSONDeserializer<List<EventCartItem>>()
        .use("values", EventCartItem.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventCartItem> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String toJsonArray(Collection<EventCartItem> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
}
