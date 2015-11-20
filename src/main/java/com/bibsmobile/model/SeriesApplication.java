/**
 * 
 */
package com.bibsmobile.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.ManyToMany;
import javax.persistence.PreUpdate;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.JSON;

/**
 * @author galen
 *
 */
@Configurable
@Entity
public class SeriesApplication {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;
	
	@Enumerated
	private SeriesApplicationStatus status = SeriesApplicationStatus.DRAFT;

	@ManyToOne
	private Event event;
	
	/**
	 * Expected number of athletes in string range.
	 */
	String expectedAthletes;
	
	/**
	 * Why you are interested in this series.
	 */
	String interest;
	
	/**
	 * Why you are a good fit for this series
	 */
	@Column(length = 2000)
	String fit;
	
	/**
	 * Interested in chip timing for this event
	 */
	boolean chipTiming = false;
	
	/**
	 * Already have a chip timer for this event
	 */
	boolean haveTimer = false;
	
	/**
	 * Have registration for this event
	 */
	boolean haveRegistration = false;
	
	/**
	 * Name of registration provider for this event
	 */
	String registrationProvider;
	
	/**
	 * URL of race's FB page
	 */
	String facebook;
	
	/**
	 * URL of race's twitter page
	 */
	String twitter;
	
	/**
	 * URL of race's instagram page
	 */
	String instagram;
	
	@ManyToOne
	private Series series;
	
	@ManyToOne
	private UserProfile user;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
	private Set <SeriesApplicationResponse> responses;
	
	/**
	 * Hibernate Overhead -- Do not remove
	 */
    @PersistenceContext
    transient EntityManager entityManager;
    
	/**
	 * Hibernate Overhead -- Do not remove
	 */
    public static EntityManager entityManager() {
        EntityManager em = new EventCartItem().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }    

	/**
	 * Hibernate Overhead -- Do not remove
	 */    
    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	/**
	 * Hibernate Overhead -- Do not remove
	 */
    @Transactional
    public void remove() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SeriesApplication attached = SeriesApplication.findCustomRegField(this.id);
            this.entityManager.remove(attached);
        }
    }

	/**
	 * Hibernate Overhead -- Do not remove
	 */
    @Transactional
    public void flush() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.flush();
    }

	/**
	 * Hibernate Overhead -- Do not remove
	 */
    @Transactional
    public void clear() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.clear();
    }

	/**
	 * Hibernate Overhead -- Do not remove
	 */
    @Transactional
    public SeriesApplication merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        SeriesApplication merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.created == null)
            this.created = new Date();
        this.updated = new Date();
    }    
    
    /**
     * Search for CustomRegFields by event. This is used for both display / update eventcartitems.
     * @param event - Event searched -- this should exist, though it does not have to have CustomRegFields yet.
     * @return A query of eventcartitems, generally followed by getResultList().
     */
    public static TypedQuery<SeriesApplication> findCustomRegFieldsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = SeriesApplication.entityManager();
        TypedQuery<SeriesApplication> q = em.createQuery("SELECT o FROM CustomRegField AS o WHERE o.event = :event", SeriesApplication.class);
        q.setParameter("event", event);
        return q;
    }
    
    public static TypedQuery<SeriesApplication> findVisibleCustomRegFieldsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = SeriesApplication.entityManager();
        TypedQuery<SeriesApplication> q = em.createQuery("SELECT o FROM CustomRegField AS o WHERE o.event = :event AND o.hidden IS NOT 1", SeriesApplication.class);
        q.setParameter("event", event);
        return q;
    }    
    
    /**
     * Find a single CustomRegField by id. This will throw an exception on fail.
     * @param id - Long id of the CustomRegField to search for. Null safe, but should not be null
     * @return 
     */
    public static SeriesApplication findCustomRegField(Long id) {
        if (id == null)
            return null;
        return entityManager().find(SeriesApplication.class, id);
    }
    
    /**
     * Auto Json Serialization using the flexjson package
     * @return Json String of single CustomRegField object
     */
    public String toJson() {
        return new JSONSerializer().include("responseSet").exclude("*.class").serialize(this);
    }

    /**
     * Auto Json Serialization using the flexjson package.
     * @param fields - Extra child fields to include.
     * @return Json String of single CustomRegField object with included fields
     */
    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    /**
     * @param collection of CustomRegFields to convert to a Json array
     * @return json array of custom reg fields, all children included
     */
    public static String toJsonArray(Collection<SeriesApplication> collection) {
        return new JSONSerializer().include("*.children").include("responseSet").exclude("*.class").serialize(collection);
    }

    /**
     * @param collection of CustomRegFields to convert to a Json Array
     * @param fields to include
     * @return json array of custom reg fields
     */
    public static String toJsonArray(Collection<SeriesApplication> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }    
    
    /**
     * Deserialize a single custom reg field from json to java object.
     * @param json - proper json string of custom reg field to deserialize
     * @return Deserialized CustomRegField object
     */
    public static SeriesApplication fromJsonToCustomRegField(String json) {
        return new JSONDeserializer<SeriesApplication>().use(null, SeriesApplication.class).use("eventItems", EventCartItem.class).deserialize(json);
    }
    
    /**
     * The Jackson version of the json deserializser
     * @param json
     * @return
     */
    public static SeriesApplication fromJson(String json) {
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			return mapper.readValue(json, SeriesApplication.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    /**
     * Deserialize an array of CustomRegField objects. These must have the format:
     * {values:[{"id":11, "Question":"Team Name", "responseSet":""}, {"id":12, "Question"Photos?", "responseSet":"yes,no"}]}
     * @param json array of CustomRegFields to deserialize
     * @return List of CustomRegFields in response
     */
    public static Collection<SeriesApplication> fromJsonArrayToCustomRegFields(String json) {
        return new JSONDeserializer<List<SeriesApplication>>().use("values", SeriesApplication.class).deserialize(json);
    }
	
}
