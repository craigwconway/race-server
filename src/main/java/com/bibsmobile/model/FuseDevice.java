package com.bibsmobile.model;

import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
* This holds a representation of a device as an ordroid. This is useful for
* the syncing up to the bibs platform.
*
* @author  galen
* @version 1.0
* @since   2015-12-21 
*/
@Configurable
@Entity
public class FuseDevice {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String deviceName;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @PreUpdate
    protected void onUpdate() {
        if (this.created == null)
            this.created = new Date();
        this.updated = new Date();
    }
	
    private String secret;
		
    //Default Constructor
    FuseDevice() {}
    
    public FuseDevice(String name) {
    	this.deviceName = name;
    	KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
	    	keyGen.init(256);
	    	SecretKey secretKey = keyGen.generateKey();
	    	secret = Base64.encodeBase64String(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public static FuseDevice findFuseDevice(Long id) {
        if (id == null)
            return null;
        return entityManager().find(FuseDevice.class, id);
    }
	
    //
	// -------------------------------------------
	// -------------------------------------------
	// Database transaction functions
	// -------------------------------------------
	// Right now we only want to find a device info
	// with the starting ID. If they have tampered
	// with the device id, the user is trying to exploit
	// the system
	
    public static long countFuseDevices() {
        return entityManager().createQuery("SELECT COUNT(o) FROM FuseDevice o", Long.class).getSingleResult();
    }
	// -------------------------------------------
	// -------------------------------------------	
	// -------------------------------------------
	// Begin the gramps framework overhead:
	// -------------------------------------------
    @PersistenceContext
    transient EntityManager entityManager;    		
    public static EntityManager entityManager() {
        EntityManager em = new FuseDevice().entityManager;
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
            FuseDevice attached = FuseDevice.findFuseDevice(this.id);
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
    public FuseDevice merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        FuseDevice merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
	// -------------------------------------------
	// End the gramps framework overhead:
	// -------------------------------------------
	// -------------------------------------------
}