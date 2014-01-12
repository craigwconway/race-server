package com.bibsmobile.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthority;
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
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
			// readers
			if(TimerConfig.countTimerConfigs() < 1){
				new TimerConfig().persist(); // reader 1
				new TimerConfig().persist(); // reader 2
			}
			
			// users
			if(UserProfile.countUserProfiles() < 1){
			// system role and user
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_SYS_ADMIN");
			userAuthority.persist();
			Set<UserAuthority> uas = new HashSet<UserAuthority>();
			uas.add(userAuthority);
			userProfile = new UserProfile();
			userProfile.setUsername("admin");
			userProfile.setPassword("admin");
			userProfile.setUserAuthorities(uas);
			userProfile.persist();
			// other roles
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_EVENT_ADMIN");
			userAuthority.persist();
			// other roles
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_USER_ADMIN");
			userAuthority.persist();
			userAuthority = new UserAuthority();
			userAuthority.setAuthority("ROLE_USER");
			userAuthority.persist();
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
