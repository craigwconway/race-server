package com.bibsmobile.model;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
public class TimerConfig { 

	private int position;
	private String url; 
	private int readTimeout = 1;
	private int readPower;
	private int writePower;
	private int type; // Dummy, ThingMagic, etc 
	private String ports; // comma seperated list of ints 
	 
	public void setReadTimeout(int t){ 
		if(t >= 1)
			readTimeout = t; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ports == null) ? 0 : ports.hashCode());
		result = prime * result + readPower;
		result = prime * result + type;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + writePower;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimerConfig other = (TimerConfig) obj;
		if (ports == null) {
			if (other.ports != null)
				return false;
		} else if (!ports.equals(other.ports))
			return false;
		if (readPower != other.readPower)
			return false;
		if (type != other.type)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (writePower != other.writePower)
			return false;
		return true;
	}
	
}
