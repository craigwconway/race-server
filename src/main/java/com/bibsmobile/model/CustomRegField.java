/**
 * 
 */
package com.bibsmobile.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * Used by event directors to hide unwanted responses
	 */
	boolean hidden;

	/**
	 * Used by to make answering questions optional
	 */
	boolean optional;
	
	/**
	 * Used to display whether a question should appear in all items. This is automatically set.
	 * If no items are checked, it will not display.
	 */
	boolean allItems;
	
	/**
	 * The set of EventCartItems that have this question displayed
	 */
	@ManyToMany
	private Set <EventCartItem> eventItems = new HashSet<EventCartItem>();

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
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	/**
	 * Extra method for getting only integers for mapped eventItems
	 * for frontend implementation.
	 */
	public List<Long> getEventItemIds(){
		List<Long> ids = new ArrayList<Long>();
		for(EventCartItem item : this.eventItems) {
			ids.add(item.getId());
		}
		return ids;
	}

	/**
	 * @return the eventItems
	 */
	public Set<EventCartItem> getEventItems() {
		return eventItems;
	}

	/**
	 * @param eventItems the eventItems to set
	 */
	public void setEventItems(HashSet<EventCartItem> eventItems) {
		this.eventItems = eventItems;
	}

	/**
	 * Whether or not this should be displayed to all items. This is set automatically
	 * if no specific EventCartItems are checked.
	 * @return the allItems
	 */
	public boolean isAllItems() {
		return allItems;
	}

	/**
	 * Whether or not this should be displayed to all items. This is set automatically
	 * if no specific EventCartItems are checked.
	 * @param allItems the allItems to set
	 */
	public void setAllItems(boolean allItems) {
		this.allItems = allItems;
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
    
    public static TypedQuery<CustomRegField> findVisibleCustomRegFieldsByEvent(Event event) {
        if (event == null)
            throw new IllegalArgumentException("The event argument is required");
        EntityManager em = CustomRegField.entityManager();
        TypedQuery<CustomRegField> q = em.createQuery("SELECT o FROM CustomRegField AS o WHERE o.event = :event AND o.hidden IS NOT 1", CustomRegField.class);
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
        return new JSONDeserializer<CustomRegField>().use(null, CustomRegField.class).use("eventItems", EventCartItem.class).deserialize(json);
    }
    
    /**
     * The Jackson version of the json deserializser
     * @param json
     * @return
     */
    public static CustomRegField fromJson(String json) {
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			return mapper.readValue(json, CustomRegField.class);
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
    public static Collection<CustomRegField> fromJsonArrayToCustomRegFields(String json) {
        return new JSONDeserializer<List<CustomRegField>>().use("values", CustomRegField.class).deserialize(json);
    }
	
}
