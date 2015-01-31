package com.bibsmobile.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;
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
@Configurable
@Entity
public class License {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	/**
	 * Unencrypted Token Format (bigE, 64b):
	 * 8b Issuetime
	 * 8b Starttime
	 * 8b Endtime
	 * 8b Startunits
	 * 8b Licensewidth
	 * 8b UID +2b pad
	 * 8b UserID
	 * 8b UID +2b pad issue machine
	 */
	private byte[] token;

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
	 * @return Get the license token from the database
	 */
	public byte[] getToken() {
		return token;
	}

	/**
	 * @param token the license token we are setting
	 */
	public void setToken(byte[] token) {
		this.token = token;
	}
	
	/**
	 * Pulls mac address as unique id from token.
	 * @return A 6 byte array containing mac address.
	 */
	public byte[] getMacAddress() {
		return Arrays.copyOfRange(token, 40, 46);
	}
	
    /**
	 * @return the issuetime
	 */
	public long getIssuetime() {
		byte[] tt = dc();
		long tmpissuetime = tt[0] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[1] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[2] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[3] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[4] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[5] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[6] & 0xff;
		tmpissuetime <<= 8;
		tmpissuetime = tt[7] & 0xff;
		return tmpissuetime;
	}



	private byte[] dc() {
		// TODO Auto-generated method stub
		return token;
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
		byte[] tt = dc();
		long tmpissueunits = tt[24] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[25] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[26] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[27] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[28] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[29] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[30] & 0xff;
		tmpissueunits <<= 8;
		tmpissueunits = tt[31] & 0xff;
		return tmpissueunits;
	}


	/**
	 * @return the endunits
	 */
	public long getEndunits() {
		byte[] tt = dc();
		long tmpendunits = tt[32] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[33] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[34] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[35] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[36] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[37] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[38] & 0xff;
		tmpendunits <<= 8;
		tmpendunits = tt[39] & 0xff;
		return tmpendunits;
	}
	
	// License validation functions
	public boolean validateLength() {
		return(this.token.length == 64);
	}
	
	// -------------------------------------------
	// -------------------------------------------
	// Database transaction functions
	// -------------------------------------------
	// Right now we only want to find a device info
	// with the starting ID. If they have tampered
	// with the device id, the user is trying to exploit
	// the system

    public static License findCurrentLicense() {
    	// I think this is the fastest way to do it?
        return entityManager().createQuery("SELECT o FROM License o ORDER BY id DESC", License.class).setMaxResults(1).getSingleResult();
    }
    
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
