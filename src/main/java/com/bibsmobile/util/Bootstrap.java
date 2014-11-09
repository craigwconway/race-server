package com.bibsmobile.util;

import java.util.Random;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;

import org.joda.time.DateTime;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private static UserAuthority userAuthority;
    @Autowired
    private static UserAuthorities userAuthorities;

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

        // expire leftover carts at startup
        try {
            BaseJob.scheduleNow(CartExpiration.class);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        //store default readers
        if (TimerConfig.countTimerConfigs() < 1) {
            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs001.bibsmobile.com");
            timerConfig.persist(); // reader 1
            timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs002.bibsmobile.com");
            timerConfig.setPosition(1);
            timerConfig.persist(); // reader 2
        }

        //  store default users
        if (UserProfile.countUserProfiles() < 1) {

            userProfile = new UserProfile();
            userProfile.setUsername("admin");
            userProfile.setPassword("admin");
            userProfile.setFirstname("System");
            userProfile.setLastname("Administrator");
            userProfile.persist();

            userProfile = new UserProfile();
            userProfile.setUsername("eventadmin");
            userProfile.setPassword("eventadmin");
            userProfile.setFirstname("Event");
            userProfile.setLastname("Administrator");
            userProfile.persist();

            userProfile = new UserProfile();
            userProfile.setUsername("useradmin");
            userProfile.setPassword("useradmin");
            userProfile.setFirstname("User");
            userProfile.setLastname("Administrator");
            userProfile.persist();

            userProfile = new UserProfile();
            userProfile.setUsername("user");
            userProfile.setPassword("user");
            userProfile.setFirstname("Bibs");
            userProfile.setLastname("User");
            userProfile.persist();
        }

        //store default roles
        if (UserAuthority.countUserAuthoritys() < 1) {
            for (String authorityName : new String[]{"ROLE_SYS_ADMIN", "ROLE_EVENT_ADMIN", "ROLE_USER_ADMIN", "ROLE_USER"}) {
                userAuthority = new UserAuthority();
                userAuthority.setAuthority(authorityName);
                userAuthority.persist();
            }
        }

        if (UserAuthorities.countUserAuthoritieses() < 1) {

            UserProfile userProfile = UserProfile.findUserProfilesByUsernameEquals("admin").getSingleResult();
            UserAuthority userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_SYS_ADMIN").getSingleResult();
            UserAuthoritiesID id = new UserAuthoritiesID();
            if (userProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(userProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            userProfile = UserProfile.findUserProfilesByUsernameEquals("eventadmin").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult();
            if (userProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(userProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            userProfile = UserProfile.findUserProfilesByUsernameEquals("useradmin").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult();
            if (userProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(userProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            userProfile = UserProfile.findUserProfilesByUsernameEquals("user").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_USER").getSingleResult();
            if (userProfile != null && userAuthority != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(userProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }
        }
      
            // SAMLE EVENT AND RUNNERS
            if(Event.findAllEvents().isEmpty()){
            
	            Event foo = new Event();
	            foo.setName("Kings Canyon Cross Country Run");
	            foo.setCity("Kings Canyon");
	            foo.setState("Colordo");
	            foo.setTimeStart(new DateTime().plusHours(48).toDate());
	            foo.setTimeEnd(new DateTime().plusHours(51).toDate());
	            foo.persist();
	            
	            for(int i = 1; i < 300; i++){
	            	RaceResult user = new RaceResult();
	            	user.setBib(String.valueOf(i));
	            	user.setEvent(foo);
	            	user.setFirstname(generateRandomName());
	            	user.setLastname(generateRandomName());
	            	user.persist();
	            }
	        }
        }
            
        public String generateRandomName(){
        	Random r = new Random();
        	int i = new Random().nextInt(11);
        	StringBuilder sb = new StringBuilder();
        	for(int n=0;n<i;i++){
        		sb.append((i%2>0?"bcdfghjklmnpqrstvwxz":"aeiouy").charAt(r.nextInt(6+(i%2)*14)));
        	}
        	return sb.toString();
        }

}