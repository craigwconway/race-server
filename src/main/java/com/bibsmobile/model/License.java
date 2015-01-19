package com.bibsmobile.model;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

import org.springframework.transaction.annotation.Transactional;


/**
* License class is model for license objects in the database.
* Licenses are stored as blobs, with all relevant checks pulled out
* and run as transactions.
*
* @author  galen
* @version 1.0
* @since   2015-01-19 
*/
public class License {
	@Id
	private long id;

	@Transient
	private byte[] macAddress;
	
	@Transient
	private long issuetime;
	
	@Transient
	private long expiretime;
	
	@Transient
	private long issueunits;
	
	@Transient
	private long endunits;
	
	

	
	/**
	 * Pulls mac address as unique id from token.
	 * @return A 6 byte array containing mac address.
	 */
	public byte[] getMacAddress() {
		return macAddress;
	}
	
    /**
	 * @return the issuetime
	 */
	public long getIssuetime() {
		return issuetime;
	}

	/**
	 * @return the expiretime
	 */
	public long getExpiretime() {
		return expiretime;
	}

	/**
	 * @return the issueunits
	 */
	public long getIssueunits() {
		return issueunits;
	}


	/**
	 * @return the endunits
	 */
	public long getEndunits() {
		return endunits;
	}
	
	
	// -------------------------------------------
	// -------------------------------------------
	// Database transaction functions
	// -------------------------------------------
	// Right now we only want to find a device info
	// with the starting ID. If they have tampered
	// with the device id, the user is trying to exploit
	// the system

	public static License findLicense(Long id) {
        if (id == null)
            return null;
        return entityManager().find(License.class, id);
    }
	
    public static long countLicenses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM License o", Long.class).getSingleResult();
    }
	
	// -------------------------------------------
	// -------------------------------------------	
	// -------------------------------------------
	// Begin the gramps framework overhead:
	// -------------------------------------------
    @PersistenceContext
    transient EntityManager entityManager;
    public static EntityManager entityManager() {
        EntityManager em = new UserAuthority().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    @Transactional
    public void persist() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    @Transactional
    public void remove() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            License attached = License.findLicense(this.id);
            this.entityManager.remove(attached);
        }
    }
    @Transactional
    public void flush() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.flush();
    }
    @Transactional
    public void clear() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        this.entityManager.clear();
    }
    @Transactional
    public License merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        License merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
	// -------------------------------------------
	// End the gramps framework overhead:
	// -------------------------------------------
	// -------------------------------------------
}
