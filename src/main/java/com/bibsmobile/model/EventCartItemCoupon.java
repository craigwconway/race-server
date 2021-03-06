package com.bibsmobile.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONSerializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Collection;
import java.util.Date;
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
    @OneToOne
    private EventCartItem item;

    private String timeStartLocal;
    private String timeEndLocal;
    private Date timeStart;
    private Date timeEnd;
    
    /**
     * Whether the coupon is currently active. Default true.
     */
    private boolean active=true;
    
    /**
     * Code linked with coupon
     */
    private String code;
    private Long discountAbsolute; // is an object to allow for null
    private Double discountRelative; // is an object to allow for null
    private int available;
    private int used;

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

    /**
     * String containing local starting time for coupon
	 * @return the timeStartLocal
	 */
	public String getTimeStartLocal() {
		return timeStartLocal;
	}

	/**
	 * String containing local starting time for coupon
	 * @param timeStartLocal the timeStartLocal to set
	 */
	public void setTimeStartLocal(String timeStartLocal) {
		this.timeStartLocal = timeStartLocal;
	}

	/**
	 * String containing local ending time for coupon
	 * @return the timeEndLocal
	 */
	public String getTimeEndLocal() {
		return timeEndLocal;
	}

	/**
	 * String containing local ending time for coupon
	 * @param timeEndLocal the timeEndLocal to set
	 */
	public void setTimeEndLocal(String timeEndLocal) {
		this.timeEndLocal = timeEndLocal;
	}

	/**
	 * Generated from timeStartLocal in event's timezone
	 * @return the timeStart
	 */
	public Date getTimeStart() {
		return timeStart;
	}

	/**
	 * generated from timeStartLocal in event's timezone
	 * @param timeStart the timeStart to set
	 */
	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	/**
	 * generated from timeEndLocal in event's timezone
	 * @return the timeEnd
	 */
	public Date getTimeEnd() {
		return timeEnd;
	}

	/**
	 * generated from timeEndLocal in event's timezone
	 * @param timeEnd the timeEnd to set
	 */
	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	/**
	 * Switch for whether the coupon is active
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Switch for whether the coupon is active
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the item
	 */
	public EventCartItem getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(EventCartItem item) {
		this.item = item;
	}

	public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = StringUtils.upperCase(code);
    }

    public Long getDiscountAbsolute() {
        return discountAbsolute;
    }

    public void setDiscountAbsolute(Long discount) {
        this.discountAbsolute = discount;
    }

    public Double getDiscountRelative() {
        return discountRelative;
    }

    public void setDiscountRelative(Double discount) {
        this.discountRelative = discount;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    /**
	 * @return the used
	 */
	public int getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed(int used) {
		this.used = used;
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
            return (int)Math.floor(originalPrice * this.discountRelative/100);
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

    public static EventCartItemCoupon findEventCartItemCoupon(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventCartItemCoupon.class, id);
    }    

    public static TypedQuery<EventCartItemCoupon> findEventCartItemCouponsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = EventCartItem.entityManager();
        TypedQuery<EventCartItemCoupon> q = em.createQuery("SELECT o FROM EventCartItemCoupon AS o WHERE o.event = :event", EventCartItemCoupon.class);
        q.setParameter("event", event);
        return q;
    }    
    
    @PersistenceContext
    transient EntityManager entityManager;    

    public static EntityManager entityManager() {
        EntityManager em = new EventCartItem().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }    
    
    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            EventCartItem attached = EventCartItem.findEventCartItem(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public EventCartItemCoupon merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventCartItemCoupon merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }    
    
    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static String toJsonArray(Collection<EventCartItem> collection) {
        return new JSONSerializer().include("*.children").exclude("*.class").serialize(collection);
    }
}
