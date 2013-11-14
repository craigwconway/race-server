package com.bibsmobile.model;

import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class EventAwardCategory {
	
    @ManyToOne
    private Event event;
    @ManyToOne
	private AwardCategory awardCategory;
	private int sortOrder;
	  
}
