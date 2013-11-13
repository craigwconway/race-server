package com.bibsmobile.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private static UserAuthority userAuthority;
	
	@Autowired
	private static UserProfile userProfile;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(userProfile.countUserProfiles()<1){
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
	}
	
}
