package com.bibsmobile.model;

import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class DeviceInfo {
	@Id
	private long id;
	
	private Long runnersUsed;
	
	@Transient
	private byte[] macAddress;

    @PersistenceContext
    transient EntityManager entityManager;    	

	public Long getRunnersUsed() {
		return runnersUsed;
	}

	public void setRunnersUsed(Long runnersUsed) {
		this.runnersUsed = runnersUsed;
	}

	public byte[] getMacAddress() {
		// Get macaddress from system info:
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			return mac;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

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
            UserAuthority attached = UserAuthority.findUserAuthority(this.id);
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
    public DeviceInfo merge() {
        if (this.entityManager == null)
            this.entityManager = entityManager();
        DeviceInfo merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
}
