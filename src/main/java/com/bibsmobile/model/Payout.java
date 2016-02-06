/**
 * 
 */
package com.bibsmobile.model;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
	private String payoutToken;
	
	@Enumerated
	private PayoutStatus status = PayoutStatus.PENDING;
	
	/**
	 * Amount computed in payout.
	 */
	private long amount;
	
	/**
	 * Manually added total modification.
	 */
	private long manualAddition;
	
	/**
	 * Amount manually settled in payout (ignored/not paid out).
	 */
	private long manualSettleAmount;
	
	/**
	 * Amount actually paid out. This is the same as {{@link #amount} generally.
	 */
	private long paidOut;
	
	@Enumerated
	private CurrencyEnum currency = CurrencyEnum.USD;
	
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
	 * @return the payoutToken
	 */
	public String getPayoutToken() {
		return payoutToken;
	}

	/**
	 * @param payoutToken the payoutToken to set
	 */
	public void setPayoutToken(String payoutToken) {
		this.payoutToken = payoutToken;
	}

	/**
	 * @return the status
	 */
	public PayoutStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PayoutStatus status) {
		this.status = status;
	}

	/**
	 * @return the amount
	 */
	public long getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getManualAddition() {
		return manualAddition;
	}

	public void setManualAddition(long manualAddition) {
		this.manualAddition = manualAddition;
	}

	/**
	 * @return the manualSettleAmount
	 */
	public long getManualSettleAmount() {
		return manualSettleAmount;
	}

	/**
	 * @param manualSettleAmount the manualSettleAmount to set
	 */
	public void setManualSettleAmount(long manualSettleAmount) {
		this.manualSettleAmount = manualSettleAmount;
	}

	public long getPaidOut() {
		return paidOut;
	}

	public void setPaidOut(long paidOut) {
		this.paidOut = paidOut;
	}

	/**
	 * @return the currency
	 */
	public CurrencyEnum getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(CurrencyEnum currency) {
		this.currency = currency;
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
	 * @return the account
	 */
	public PaymentAccount getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(PaymentAccount account) {
		this.account = account;
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
	 * @return the processed
	 */
	public Date getProcessed() {
		return processed;
	}

	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(Date processed) {
		this.processed = processed;
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
	public static Payout findPayout(Long id) {
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
            Payout attached = Payout.findPayout(this.id);
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
 	public Payout() {
		
	}
 	
 	public Payout(UserGroup userGroup) {
 		
 	}
	
    public static List<Payout> findPayoutsForUser(UserProfile user) {
        return entityManager().createQuery("SELECT o FROM Payout o where o.user = :user", Payout.class).setParameter("user",  user).getResultList();
    }
    
    public static List<Payout> findPayoutsForUserAndUserGroup(UserProfile user, UserGroup userGroup) {
        return entityManager().createQuery("SELECT o FROM Payout o where o.user = :user AND o.userGroup = :userGroup", Payout.class)
        		.setParameter("user",  user).setParameter("userGroup", userGroup).getResultList();
    }
    
}
