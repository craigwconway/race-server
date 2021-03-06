/**
 * 
 */
package com.bibsmobile.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.Temporal;
import javax.persistence.Enumerated;

import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.BadgeTriggerEnum;

/**
 * Copy of a badge. All badges are queried on their trigger type and whether they
 * are enabled when a trigger comes in from a queue. If the badge matches, the a
 * UserBadge is created. A badge may be triggered by either an event or a series.
 * @author galen
 *
 */
@Entity
public class Badge {
	/**
	 * Autogenerated id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Enumerated
	private BadgeTriggerEnum badgeTrigger = BadgeTriggerEnum.MANUAL;

	private boolean active = true;
	
	private String name;
	
	private String description;
	
	private String url;
	
	@ManyToOne
	private Event event = null;
	
	@ManyToOne
	private Series series = null;
	
	private int triggerQuantity;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeEnd;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "badge")
	Set <UserBadge> granted;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the badgeTrigger
	 */
	public BadgeTriggerEnum getBadgeTrigger() {
		return badgeTrigger;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setBadgeTrigger(BadgeTriggerEnum badgeTrigger) {
		this.badgeTrigger = badgeTrigger;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Short description for badge.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Short description for badge.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return the series
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * @param series the series to set
	 */
	public void setSeries(Series series) {
		this.series = series;
	}

	/**
	 * @return the triggerQuantity
	 */
	public int getTriggerQuantity() {
		return triggerQuantity;
	}

	/**
	 * @param triggerQuantity the triggerQuantity to set
	 */
	public void setTriggerQuantity(int triggerQuantity) {
		this.triggerQuantity = triggerQuantity;
	}

	/**
	 * @return the timeStart
	 */
	public Date getTimeStart() {
		return timeStart;
	}

	/**
	 * @param timeStart the timeStart to set
	 */
	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	/**
	 * @return the timeEnd
	 */
	public Date getTimeEnd() {
		return timeEnd;
	}

	/**
	 * @param timeEnd the timeEnd to set
	 */
	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}
	
    @PersistenceContext
    transient EntityManager entityManager;
    
    public static EntityManager entityManager() {
        EntityManager em = new EventType().entityManager;
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
            Badge attached = Badge.findBadge(this.id);
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
    public Badge merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        Badge merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static Badge findBadge(Long id) {
        if (id == null)
            return null;
        return entityManager().find(Badge.class, id);
    }
    
    public static List<Badge> findBadgesByEvent(Event event) {
        return entityManager().createQuery("SELECT o FROM Badge AS o WHERE o.event = :event", Badge.class).setParameter("event", event).getResultList();
    }    
}
