/**
 * 
 */
package com.bibsmobile.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
public class Payout {
	/**
	 * This is the id payout.
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	/**
	 * Token of payout.
	 */
	private String stripeToken;
	
	/**
	 * User in charge of payments.
	 */
	@ManyToOne
	UserProfile user;
	
	/**
	 * Organization recieving payment.
	 */
	@ManyToOne
	UserGroup userGroup;
	
	/**
	 * Account credited payment.
	 */
	@ManyToOne
	PaymentAccount account;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date processed;

	@PrePersist
	void prePersist() {
		if(this.created == null) {
			this.created = new Date();
		}
			
	}

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new Payout().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
	
	/**
	 * Find a single award category by id
	 * @param id of Award Template to search for
	 * @return the AwardsTemplate object
	 */
	public static Payout findAwardsTemplate(Long id) {
        if (id == null) return null;
        return entityManager().find(Payout.class, id);
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
            Payout attached = Payout.findAwardsTemplate(this.id);
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
    public Payout merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Payout merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
	
	/**
	 * Default Constructor
	 */
 	private Payout() {
		
	}
	
    public static List<Payout> findPayoutsForUser(UserProfile user) {
        return entityManager().createQuery("SELECT o FROM Payout o where o.user = :user", Payout.class).setParameter("user",  user).getResultList();
    }
    
    public static List<Payout> findPayoutsForUserAndUserGroup(UserProfile user, UserGroup userGroup) {
        return entityManager().createQuery("SELECT o FROM Payout o where o.user = :user AND o.userGroup = :userGroup", Payout.class)
        		.setParameter("user",  user).setParameter("userGroup", userGroup).getResultList();
    }
    
}
