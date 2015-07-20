/**
 * 
 */
package com.bibsmobile.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author galen
 *
 */
@Configurable
@Entity
public class CustomRegField {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Event event;

	@NotNull
	private String question;

	private String responseSet;

	/**
	 * @return CustomRegField Id, Autoincremented from 1
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param set CustomRegField Id, this field is autoincremented from 1
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This association is currently only one way
	 * @return The event mapped by this custom reg field.
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * This association is currently only one way
	 * @param event Set the event mapped by this custom reg field
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return The question in this field. Appears as a string above the field.
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question The question in this field. Appears as a string above the field.
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * A comma separated list of responses. Leave blank if the object is a field.
	 * @return the responseSet.
	 */
	public String getResponseSet() {
		return responseSet;
	}

	/**
	 * A comma separated list of responses. Leave blank if the object is a field.
	 * @param responseSet the responseSet to set.
	 */
	public void setResponseSet(String responseSet) {
		this.responseSet = responseSet;
	}
	
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
            CustomRegField attached = CustomRegField.findCustomRegField(this.id);
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
    public CustomRegField merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        CustomRegField merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    
    /**
     * Search for CustomRegFields by event. This is used for both display / update eventcartitems.
     * @param event - Event searched -- this should exist, though it does not have to have CustomRegFields yet.
     * @return A query of eventcartitems, generally followed by getResultList().
     */
    public static TypedQuery<CustomRegField> findCustomRegFieldsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = CustomRegField.entityManager();
        TypedQuery<CustomRegField> q = em.createQuery("SELECT o FROM CustomRegField AS o WHERE o.event = :event", CustomRegField.class);
        q.setParameter("event", event);
        return q;
    }
    
    /**
     * Find a single CustomRegField by id. This will throw an exception on fail.
     * @param id - Long id of the CustomRegField to search for. Null safe, but should not be null
     * @return 
     */
    public static CustomRegField findCustomRegField(Long id) {
        if (id == null)
            return null;
        return entityManager().find(CustomRegField.class, id);
    }
    
    /**
     * Auto Json Serialization using the flexjson package
     * @return Json String of single CustomRegField object
     */
    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
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
    public static String toJsonArray(Collection<CustomRegField> collection) {
        return new JSONSerializer().include("*.children").exclude("*.class").serialize(collection);
    }

    /**
     * @param collection of CustomRegFields to convert to a Json Array
     * @param fields to include
     * @return json array of custom reg fields
     */
    public static String toJsonArray(Collection<CustomRegField> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }    
    
    /**
     * Deserialize a single custom reg field from json to java object.
     * @param json - proper json string of custom reg field to deserialize
     * @return Deserialized CustomRegField object
     */
    public static CustomRegField fromJsonToCustomRegField(String json) {
        return new JSONDeserializer<CustomRegField>().use(null, CustomRegField.class).deserialize(json);
    }

    /**
     * Deserialize an array of CustomRegField objects. These must have the format:
     * {values:[{"id":11, "Question":"Team Name", "responseSet":""}, {"id":12, "Question"Photos?", "responseSet":"yes,no"}]}
     * @param json array of CustomRegFields to deserialize
     * @return List of CustomRegFields in response
     */
    public static Collection<CustomRegField> fromJsonArrayToCustomRegFields(String json) {
        return new JSONDeserializer<List<CustomRegField>>().use("values", CustomRegField.class).deserialize(json);
    }
	
}
