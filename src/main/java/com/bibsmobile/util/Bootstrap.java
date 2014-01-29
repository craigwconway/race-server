package com.bibsmobile.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private static UserAuthority userAuthority;

	@Autowired
	private static UserProfile userProfile;
	
	@Autowired
	private static AwardCategory awardCategory;
	
	@Autowired
	private static TimerConfig timerConfig;
	
	@Autowired
	private static UserGroup userGroup;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
			// readers
			if(TimerConfig.countTimerConfigs() < 1){
				TimerConfig timerConfig = new TimerConfig();
				timerConfig.setUrl("tmr://bibs001.bibsmobile.com");
				timerConfig.persist(); // reader 1
				timerConfig = new TimerConfig();
				timerConfig.setUrl("tmr://bibs002.bibsmobile.com");
				timerConfig.setPosition(1);
				timerConfig.persist(); // reader 2
			}
			
			// users
			if(UserProfile.countUserProfiles() < 1){
				
			userGroup = new UserGroup();
			userGroup.setName("My Organization (Unique:"+ new Date().getTime() +")");
			userGroup.setBibWrites(3000);
			userGroup.persist();
			List<UserProfile> userProfiles = new ArrayList<UserProfile>();
			
			// system role and user
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_SYS_ADMIN");
			userAuthority.persist();
			Set<UserAuthority> uas = new HashSet<UserAuthority>();
			uas.add(userAuthority);
			userProfile = new UserProfile();
			userProfile.setUsername("admin");
			userProfile.setPassword("admin");
			userProfile.setFirstname("System");
			userProfile.setLastname("Administrator");
			userProfile.setUserAuthorities(uas);
			userProfile.setUserGroup(userGroup);
			userProfile.persist();
			userProfiles.add(userProfile);
			// other roles
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_EVENT_ADMIN");
			userAuthority.persist();
			uas = new HashSet<UserAuthority>();
			uas.add(userAuthority);
			userProfile = new UserProfile();
			userProfile.setUsername("eventadmin");
			userProfile.setPassword("eventadmin");
			userProfile.setFirstname("Event");
			userProfile.setLastname("Administrator");
			userProfile.setUserAuthorities(uas);
			userProfile.setUserGroup(userGroup);
			userProfile.persist();
			userProfiles.add(userProfile);
			// other roles
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_USER_ADMIN");
			userAuthority.persist();
			uas = new HashSet<UserAuthority>();
			uas.add(userAuthority);
			userProfile = new UserProfile();
			userProfile.setUsername("useradmin");
			userProfile.setPassword("useradmin");
			userProfile.setFirstname("User");
			userProfile.setLastname("Administrator");
			userProfile.setUserAuthorities(uas);
			userProfile.setUserGroup(userGroup);
			userProfile.persist();
			userProfiles.add(userProfile);
			// user
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_USER");
			userAuthority.persist();
			uas = new HashSet<UserAuthority>();
			uas.add(userAuthority);
			userProfile = new UserProfile();
			userProfile.setUsername("user");
			userProfile.setPassword("user");
			userProfile.setFirstname("Bibs");
			userProfile.setLastname("User");
			userProfile.setUserAuthorities(uas);
			userProfile.setUserGroup(userGroup);
			userProfile.persist();
			userProfiles.add(userProfile);
			
			// groups
			userGroup.setUserProfiles(userProfiles);
			userGroup.merge();
		}
		
		// standard award categories
		if(AwardCategory.countAwardCategorys() < 1){
			awardCategory = new AwardCategory();
			awardCategory.setName("Overall Winners");
			awardCategory.persist();
			awardCategory = new AwardCategory();
			awardCategory.setName("Men Overall");
			awardCategory.setGender("M");
			awardCategory.persist();
			awardCategory = new AwardCategory();
			awardCategory.setName("Women Overall");
			awardCategory.setGender("F");
			awardCategory.persist();
			awardCategory = new AwardCategory();
			awardCategory.setName("Ages 10 and Under");
			awardCategory.setAgeMin(0);
			awardCategory.setAgeMax(10);
			awardCategory.persist();
			awardCategory = new AwardCategory();
			awardCategory.setName("Men's Masters");
			awardCategory.setGender("M");
			awardCategory.setAgeMin(40);
			awardCategory.setAgeMax(49);
			awardCategory.persist();
		}
		
	}
	
}
