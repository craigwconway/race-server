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
public class PaymentAccount {
	/**
	 * This is the id of the awards template.
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	private boolean primaryAccount=true;
	
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
	
	@Temporal(TemporalType.TIMESTAMP)
	Date created;
	
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
	 * @return the primaryAccount
	 */
	public boolean isPrimaryAccount() {
		return primaryAccount;
	}

	/**
	 * @param primaryAccount the primaryAccount to set
	 */
	public void setPrimaryAccount(boolean primaryAccount) {
		this.primaryAccount = primaryAccount;
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
	 * @return the userGroup
	 */
	public UserGroup getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the stripeToken
	 */
	public String getStripeToken() {
		return stripeToken;
	}

	/**
	 * @param stripeToken the stripeToken to set
	 */
	public void setStripeToken(String stripeToken) {
		this.stripeToken = stripeToken;
	}

	@PrePersist
	void prePersist() {
		if(this.created == null) {
			this.created = new Date();
		}
			
	}
	
	public PaymentAccount(UserProfile user, UserGroup group, String stripeToken) {
		this.user = user;
		this.userGroup = group;
		this.stripeToken = stripeToken;
	}

	@PersistenceContext
    transient EntityManager entityManager;

	public static final EntityManager entityManager() {
        EntityManager em = new PaymentAccount().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
	
	/**
	 * Find a single award category by id
	 * @param id of Award Template to search for
	 * @return the AwardsTemplate object
	 */
	public static PaymentAccount findAwardsTemplate(Long id) {
        if (id == null) return null;
        return entityManager().find(PaymentAccount.class, id);
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
            PaymentAccount attached = PaymentAccount.findAwardsTemplate(this.id);
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
    public PaymentAccount merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PaymentAccount merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
	
	/**
	 * Default Constructor
	 */
 	private PaymentAccount() {
		
	}
	
    public static List<PaymentAccount> findActivePaymentAccountsForUser(UserProfile user) {
        return entityManager().createQuery("SELECT o FROM PaymentAccount o where o.primaryAccount = 1 AND o.user = :user", PaymentAccount.class).setParameter("user",  user).getResultList();
    }
    
    public static List<PaymentAccount> findActivePaymentAccountsForOrg(UserGroup userGroup) {
        return entityManager().createQuery("SELECT o FROM PaymentAccount o where o.primaryAccount = 1 AND o.userGroup = :userGroup", PaymentAccount.class).setParameter("userGroup",  userGroup).getResultList();
    }
    
}
