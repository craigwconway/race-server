/**
 * 
 */
package com.bibsmobile.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
public class CustomRegFieldResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private CustomRegField customRegField;

	@ManyToOne
	private Cart cart;
	
	private String response;
	
	private Long price;
	
	
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
	 * @return the customRegField
	 */
	public CustomRegField getCustomRegField() {
		return customRegField;
	}

	/**
	 * @param customRegField the customRegField to set
	 */
	public void setCustomRegField(CustomRegField customRegField) {
		this.customRegField = customRegField;
	}

	/**
	 * @return the cart
	 */
	public Cart getCart() {
		return cart;
	}

	/**
	 * @param cart the cart to set
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * @return the price
	 */
	public Long getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Long price) {
		this.price = price;
	}

	/**
	 * Hibernate Overhead -- Do not remove
	 */
    @PersistenceContext
    transient EntityManager entityManager;	
    
    public static String generateExportString(Event event, List<CustomRegFieldResponse> responses, List<CustomRegField> fields) {
    	String returnString = "";
    	Map<CustomRegField, CustomRegFieldResponse> questionMap = new HashMap<CustomRegField, CustomRegFieldResponse>();
    	for(CustomRegFieldResponse response : responses) {
    		questionMap.put(response.getCustomRegField(), response);
    	}
    	for(CustomRegField field : fields) {
    		if(!returnString.isEmpty()) {
    			returnString +=",";
    		}
    		if(questionMap.get(field) == null) {
    			returnString += "-";
    		} else {
    			returnString += questionMap.get(field).getResponse();
    		}
    	}
    	return returnString;
    }
    
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
            CustomRegFieldResponse attached = CustomRegFieldResponse.findCustomRegFieldResponse(this.id);
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
    public CustomRegFieldResponse merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        CustomRegFieldResponse merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    
    /**
     * Search for CustomRegFields by event. This is used for both display / update eventcartitems.
     * @param event - Event searched -- this should exist, though it does not have to have CustomRegFields yet.
     * @return A query of eventcartitems, generally followed by getResultList().
     */
    public static TypedQuery<CustomRegFieldResponse> findCustomRegFieldResponsesByField(CustomRegField customRegField) {
        if (customRegField == null)
            throw new IllegalArgumentException("The customRegField argument is required");
        EntityManager em = CustomRegField.entityManager();
        TypedQuery<CustomRegFieldResponse> q = em.createQuery("SELECT o FROM CustomRegFieldResponse AS o WHERE o.customRegField = :customRegField", CustomRegFieldResponse.class);
        q.setParameter("customRegField", customRegField);
        return q;
    }

    /**
     * Search for CustomRegFields by event. This is used for both display / update eventcartitems.
     * @param event - Event searched -- this should exist, though it does not have to have CustomRegFields yet.
     * @return A query of eventcartitems, generally followed by getResultList().
     */
    public static TypedQuery<CustomRegFieldResponse> findCustomRegFieldResponsesByFieldAndCart(CustomRegField customRegField, Cart cart) {
        if (customRegField == null || cart == null)
            throw new IllegalArgumentException("The CustomRegField and Cart arguments are required");
        EntityManager em = CustomRegField.entityManager();
        TypedQuery<CustomRegFieldResponse> q = em.createQuery("SELECT o FROM CustomRegFieldResponse AS o WHERE o.customRegField = :customRegField AND o.cart = :cart", CustomRegFieldResponse.class);
        q.setParameter("customRegField", customRegField);
        q.setParameter("cart", cart);
        return q;
    }
    
    /**
     * Find a single CustomRegField by id. This will throw an exception on fail.
     * @param id - Long id of the CustomRegField to search for. Null safe, but should not be null
     * @return 
     */
    public static CustomRegFieldResponse findCustomRegFieldResponse(Long id) {
        if (id == null)
            return null;
        return entityManager().find(CustomRegFieldResponse.class, id);
    }
    
    /**
     * Auto Json Serialization using the flexjson package
     * @return Json String of single CustomRegFieldResponse object
     */
    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    /**
     * Auto Json Serialization using the flexjson package.
     * @param fields - Extra child fields to include.
     * @return Json String of single CustomRegFieldResponse object with included fields
     */
    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    /**
     * @param collection of CustomRegFieldResponses to convert to a Json array
     * @return json array of custom reg fields, all children included
     */
    public static String toJsonArray(Collection<CustomRegFieldResponse> collection) {
        return new JSONSerializer().include("*.children").exclude("*.class").serialize(collection);
    }

    /**
     * @param collection of CustomRegFieldResponses to convert to a Json Array
     * @param fields to include
     * @return json array of custom reg field responses
     */
    public static String toJsonArray(Collection<CustomRegFieldResponse> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }    
    
    /**
     * Deserialize a single custom reg field from json to java object.
     * @param json - proper json string of custom reg field to deserialize
     * @return Deserialized CustomRegFieldResponse object
     */
    public static CustomRegFieldResponse fromJsonToCustomRegFieldResponse(String json) {
        return new JSONDeserializer<CustomRegFieldResponse>().use(null, CustomRegFieldResponse.class).deserialize(json);
    }

    /**
     * Deserialize an array of CustomRegFieldResponse objects. These must have the format:
     * {values:[{"id":11, customRegField:{"id":1}, cart:{"id":55}, "response":"shrug"}, {"id":12, customRegField:{"id":2}, cart:{"id":55}, "response":"facepunch"}]}
     * @param json array of CustomRegFieldResponses to deserialize
     * @return List of CustomRegFieldResponses in response
     */
    public static Collection<CustomRegFieldResponse> fromJsonArrayToCustomRegFieldResponses(String json) {
        return new JSONDeserializer<List<CustomRegFieldResponse>>().use("values", CustomRegFieldResponse.class).deserialize(json);
    }
	

}
