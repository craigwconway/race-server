/**
 * 
 */
package com.bibsmobile.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Series;

/**
 * Represents a series of athletic events. An example is the series:
 * "Zappos 200", which is sponsored by the sportswear company Zappos.
 * Subqueries on events in this series may be performed, and users can
 * earn rewards specific to this group of events.
 * @author galen {gedanziger}
 *
 */
@Entity
@Configurable
public class SeriesRegion {
	/**
	 * Autogenerated id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	/**
	 * Name of the series
	 */
	private String name;

	/**
	 * Events in the series
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "region")
	private Set <Event> events;
	
	/**
	 * Name of encompassing series
	 */
	@ManyToOne
	private Series series;

	/**
	 * Used by hibernate
	 */
	@PersistenceContext
    transient EntityManager entityManager;

	/**
	 * Used by hibernate
	 */
	public static final EntityManager entityManager() {
        EntityManager em = new SeriesRegion().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SeriesRegion attached = SeriesRegion.findSeries(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public SeriesRegion merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SeriesRegion merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }	
	
	public static SeriesRegion findSeries(Long id) {
        if (id == null) return null;
        return entityManager().find(SeriesRegion.class, id);
    }
	
    public static List<SeriesRegion> findAllSeries() {
        return entityManager().createQuery("SELECT o FROM Series o", SeriesRegion.class).getResultList();
    }
	
	/**
	 * ID of series, autogenerated on persist.
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * ID of series, autogenerated on persist.
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Name of series (e.g. Zappos 200)
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Name of series (e.g. Zappos 200)
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
