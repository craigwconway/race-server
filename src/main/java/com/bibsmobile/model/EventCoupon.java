package com.bibsmobile.model;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Configurable
@Entity
public class EventCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @ManyToMany
    private Set<EventCartItem> eventCartItems;

    @ManyToOne
    private Event event;
    
    @Enumerated
    private EventCouponTypeEnum type;
    
    private String code;
    
    private int discount;
    
    private int numberAvailable;
    
    private int numberUsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<EventCartItem> getEventCartItems() {
        return eventCartItems;
    }

    public void setEventCartItems(Set<EventCartItem> eventCartItems) {
        this.eventCartItems = eventCartItems;
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

	/**
	 * @return the numberUsed
	 */
	public int getNumberUsed() {
		return numberUsed;
	}

	/**
	 * @param numberUsed the numberUsed to set
	 */
	public void setNumberUsed(int numberUsed) {
		this.numberUsed = numberUsed;
	}
	
    @PersistenceContext
    transient EntityManager entityManager;	

    public static EntityManager entityManager() {
        EntityManager em = new EventPhoto().entityManager;
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
            EventPhoto attached = EventPhoto.findEventPhoto(this.id);
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
    public EventCoupon merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        EventCoupon merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static EventCoupon findEventCoupon(Long id) {
        if (id == null)
            return null;
        return entityManager().find(EventCoupon.class, id);
    }
	
    public static EventCoupon findEventCouponByCodeAndEventEquals(String code, Event event) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventCoupon> q = em.createQuery("SELECT o FROM EventCoupon AS o WHERE o.code EQ :code AND o.event EQ :event", EventCoupon.class);
        q.setParameter("code", code);
        q.setParameter("event", event);
        return q.getSingleResult();
    }
    
    public static List<EventCoupon> findEventCouponsByEventEquals(Event event) {
        EntityManager em = Event.entityManager();
        TypedQuery<EventCoupon> q = em.createQuery("SELECT o FROM EventCoupon AS o WHERE o.event EQ :event", EventCoupon.class);
        q.setParameter("event", event);
        return q.getResultList();
    }
    
}
