package com.bibsmobile.model;

import java.util.List;

import javax.persistence.ManyToMany;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class UserGroup {
	
	private String name;
	private int bibWrites;

	@ManyToMany
	private List<UserProfile> userProfiles;

	@ManyToMany
	private List<Event> events;
	
}
