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
	private int readTimeout;
	private int readPower;
	private int writePower;
	private int type; // Dummy, ThingMagic, etc 
	
}
