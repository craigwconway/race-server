package com.bibsmobile.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class UserGroup {
	
	private String name;

	@ManyToMany
	@JoinTable(name = "group_users")
	private List<UserProfile> users;

	@ManyToMany
	@JoinTable(name = "group_events")
	private List<Event> events;
	
}
