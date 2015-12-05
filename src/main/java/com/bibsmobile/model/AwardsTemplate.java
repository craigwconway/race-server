/**
 * 
 */
package com.bibsmobile.model;

import java.util.List;

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
	
	/**
	 * Whether or not this template is a default awards template available for all users.
	 */
	boolean defaultTemplate=false;
	
	/**
	 * Set of award categories to use in template.
	 */
	@OneToMany
	List<AwardCategory> categories;
	
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
	
    public static List<AwardsTemplate> findAwardsTemplatesForUser(UserProfile user) {
    	if(user == null) {
    		return entityManager().createQuery("SELECT o FROM AwardsTemplate o where o.defaultTemplate = 1", AwardsTemplate.class).getResultList();
    	}
        return entityManager().createQuery("SELECT o FROM AwardsTemplates o where o.defaultTemplate = 1 OR o.user = :user", AwardsTemplate.class).setParameter("user",  user).getResultList();
    }
}
