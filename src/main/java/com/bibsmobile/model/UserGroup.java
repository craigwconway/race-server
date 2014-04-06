package com.bibsmobile.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooEquals
public class UserGroup {
	
	private String name;
	private int bibWrites;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "userGroup")
	private Set<UserProfile> userProfiles;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "userGroup")
	private Set<Event> events;
	
}
