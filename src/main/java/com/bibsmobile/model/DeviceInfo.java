package com.bibsmobile.model;

import java.net.InetAddress;
import java.net.NetworkInterface;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Entity
public class DeviceInfo {
	@Id
	private long id;
	
	private Long runnersUsed;
	
	@Transient
	private byte[] macAddress;

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
	
}
