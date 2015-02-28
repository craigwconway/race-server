package com.bibsmobile.model;

import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

/**
* DeviceInfo class stores information about the system
* pertaining to the license, used for restricting use
* of the bibs software.
*
* @author  galen
* @version 1.0
* @since   2015-01-02 
*/
@Configurable
@Entity
public class DeviceInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long runnersUsed;
	
	@Transient
	private byte[] macAddress;

	/**
	 * This method gets runners currently used by the system.
	 * This quantity is restricted by a license object.
	 * @return long This returns a number of runners added to races.
	 */
	public Long getRunnersUsed() {
		return runnersUsed;
	}

	public void setRunnersUsed(Long runnersUsed) {
		this.runnersUsed = runnersUsed;
	}

	/**
	 * This method gets the Mac Address of the system,
	 * used as a unique identifier.
	 * @return byte[] A 6 byte array containing the Mac Address.
	 */
	public byte[] getMacAddress() {
		// Get macaddress from system info:
		System.out.println("enter getMacAddress");
		InetAddress ip;
		try {
			System.out.println("getting mac address");
			ip = InetAddress.getLocalHost();
			System.out.println("ip: " + ip.getHostAddress());
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			System.out.println("mac: " + network.getHardwareAddress().toString());
			return mac;
		} catch (Exception e) {
			System.out.println("Couldn't find macaddress");
			System.out.println(e);
			return null;
		}
	}

	// -------------------------------------------
	// -------------------------------------------
	// Quick Add/Remove
	// -------------------------------------------
	public static void quickBlindAddRunner() {
		DeviceInfo info = DeviceInfo.findDeviceInfo(new Long(1));
		info.setRunnersUsed(info.getRunnersUsed() + 1);
		info.merge();
	}
	public static void quickBlindRemoveRunner() {
		DeviceInfo info = DeviceInfo.findDeviceInfo(new Long(1));
		info.setRunnersUsed(info.getRunnersUsed() - 1);
		info.merge();
	}
	
	public void quickAddRunner() {
		setRunnersUsed(getRunnersUsed() + 1);
	}
	
	public void quickRemoveRunner() {
		setRunnersUsed(getRunnersUsed() - 1);
	}
	
	// -------------------------------------------
	// -------------------------------------------
	// Database transaction functions
	// -------------------------------------------
	// Right now we only want to find a device info
	// with the starting ID. If they have tampered
	// with the device id, the user is trying to exploit
	// the system
	
    public static DeviceInfo findDeviceInfo(Long id) {
        if (id == null)
            return null;
        return entityManager().find(DeviceInfo.class, id);
    }
	
    
    public static long countDeviceInfos() {
        return entityManager().createQuery("SELECT COUNT(o) FROM DeviceInfo o", Long.class).getSingleResult();
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
            DeviceInfo attached = DeviceInfo.findDeviceInfo(this.id);
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
	// -------------------------------------------
	// End the gramps framework overhead:
	// -------------------------------------------
	// -------------------------------------------
}
