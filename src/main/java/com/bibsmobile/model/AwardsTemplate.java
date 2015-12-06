/**
 * 
 */
package com.bibsmobile.model;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is a template class used to save and generate sets of award categories.
 * A user can select a configuration for awards from one of these templates.
 * @author galen
 *
 */
@Configurable
@Entity
public class AwardsTemplate {
	/**
	 * This is the id of the awards template.
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	/**
	 * This is the chosen name for the awards template.
	 */
	private String name;
	
	/**
	 * User creating awards template.
	 */
	@ManyToOne
	UserProfile user;

	EventAwardsConfig awardsConfig = new EventAwardsConfig();
	
	/**
	 * Whether or not this template is a default awards template available for all users.
	 */
	boolean defaultTemplate=false;
	
	/**
	 * Set of award categories to use in template.
	 */
	@OneToMany
	List<AwardCategory> categories;
	
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
	 * @return the user
	 */
	public UserProfile getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(UserProfile user) {
		this.user = user;
	}

	/**
	 * @return the awardsConfig
	 */
	public EventAwardsConfig getAwardsConfig() {
		return awardsConfig;
	}

	/**
	 * @param awardsConfig the awardsConfig to set
	 */
	public void setAwardsConfig(EventAwardsConfig awardsConfig) {
		this.awardsConfig = awardsConfig;
	}

	/**
	 * @return the defaultTemplate
	 */
	public boolean isDefaultTemplate() {
		return defaultTemplate;
	}

	/**
	 * @param defaultTemplate the defaultTemplate to set
	 */
	public void setDefaultTemplate(boolean defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	/**
	 * @return the categories
	 */
	public List<AwardCategory> getCategories() {
		return categories;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<AwardCategory> categories) {
		this.categories = categories;
	}

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new AwardsTemplate().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
	
	/**
	 * Find a single award category by id
	 * @param id of Award Template to search for
	 * @return the AwardsTemplate object
	 */
	public static AwardsTemplate findAwardsTemplate(Long id) {
        if (id == null) return null;
        return entityManager().find(AwardsTemplate.class, id);
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
            AwardsTemplate attached = AwardsTemplate.findAwardsTemplate(this.id);
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
    public AwardsTemplate merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        AwardsTemplate merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
	
	/**
	 * Default Constructor
	 */
 	private AwardsTemplate() {
		
	}
	/**
	 * This constructor creates a copy of an awards configuration from a given event type and passed in properties.
	 * defaultStatus should be false unless this appears for all users.
	 * @param type {@link EventType} object to copy
	 * @param name String of the display name for this configuration
	 * @param defaultStatus True if publicly visible
	 * @param user User owning this awards template
	 */
	public AwardsTemplate(EventType type, String name, boolean defaultStatus, UserProfile user) {
		List<AwardCategory> categories = new ArrayList<AwardCategory>();
		this.setAwardsConfig(type.getAwardsConfig());
		this.setName(name);
		this.setDefaultTemplate(defaultStatus);
		this.setUser(user);
		for(AwardCategory category : type.getAwardCategorys()) {
			AwardCategory newCategory = new AwardCategory();
			newCategory.setListSize(category.getListSize());
			newCategory.setAgeMin(category.getAgeMin());
			newCategory.setAgeMax(category.getAgeMax());
			newCategory.setMaster(category.isMaster());
			newCategory.setMedal(category.isMedal());
			newCategory.setName(category.getName());
			newCategory.setSortOrder(category.getSortOrder());
			newCategory.setVersion(1);
			newCategory.setGender(category.getGender());
			categories.add(newCategory);
		}
		this.setCategories(categories);
	}
	
    public static List<AwardsTemplate> findAwardsTemplatesForUser(UserProfile user) {
    	if(user == null) {
    		return entityManager().createQuery("SELECT o FROM AwardsTemplate o where o.defaultTemplate = 1", AwardsTemplate.class).getResultList();
    	}
        return entityManager().createQuery("SELECT o FROM AwardsTemplate o where o.defaultTemplate = 1 OR o.user = :user", AwardsTemplate.class).setParameter("user",  user).getResultList();
    }
}
