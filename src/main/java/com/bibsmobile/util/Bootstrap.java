package com.bibsmobile.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.DeviceInfo;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAwardsConfig;
import com.bibsmobile.model.License;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;

import org.apache.commons.lang3.StringUtils;
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


	private static Random r = new Random();
	
	protected static String generateRandomName(){
		int wordSize = Math.abs(r.nextInt(9));
		StringBuilder sb = new StringBuilder(wordSize);
		if(wordSize < 4) wordSize = 4;
		String cons = "bcdfghjklmnpqrstvwxz";
		String vows = "aeiou";
		for(int n=0;n<wordSize;n++){
			int i = Math.abs(r.nextInt(cons.length()));
			int j = Math.abs(r.nextInt(vows.length()));
			sb.append( (n%2==0)?cons.charAt(i):vows.charAt(j));
		}
		return sb.toString();
	}
	
	protected static int generateRandomAge(){
		int age = Math.abs(r.nextInt(89));
		if(age < 14) age = 14;
		return age;
	}
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
    	if(Event.findAllEvents().isEmpty()){
    	    
            Event foo = new Event();
            foo.setName("Kings Canyon Critical Mass");
            foo.setCity("Kings Canyon");
            foo.setState("Colordo");
            foo.setGunTime(new DateTime().toDate());
            foo.setTimeStart(new DateTime().toDate());
            foo.setAwardsConfig(new EventAwardsConfig());
            foo.persist();
            
            for(long i = 1; i < 300; i++){
            	RaceResult user = new RaceResult();
            	user.setBib(i);
            	user.setEvent(foo);
            	user.setAge(generateRandomAge());
            	user.setGender( (i%2==0) ? "M" : "F");
            	user.setTimeofficial(Math.abs(
            			(int) new DateTime().plusMinutes(r.nextInt(60)).toDate().getTime()));
            	user.setTimeofficialdisplay(
            			RaceResult.toHumanTime(foo.getGunTime().getTime(), user.getTimeofficial()));
            	user.setFirstname(generateRandomName());
            	user.setLastname(generateRandomName());
            	user.setCity("San Francisco");
            	user.setState("CA");
            	user.persist();
            }
            

            // default awards categories
            AwardCategory.createDefaultMedals(foo);
            AwardCategory.createAgeGenderRankings(foo, 
            		AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 
            		AwardCategory.DEFAULT_AGE_SPAN, AwardCategory.DEFAULT_LIST_SIZE);
            
        }

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
            if(BuildTypeUtil.usesRfid()) {
                timerConfig.setType(1);
            }
            timerConfig.persist(); // reader 1
            timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs002.bibsmobile.com");
            timerConfig.setPosition(1);
            if(BuildTypeUtil.usesRfid()) {
                timerConfig.setType(1);
            }
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
        //Generate a deviceinfo if there is not yet one in the system
        if(null == DeviceInfo.findDeviceInfo(new Long(1))) {
        	DeviceInfo info = new DeviceInfo();
        	info.setRunnersUsed(new Long(0));
        	info.persist();
        	// If we have no deviceinfo, we also do not have any licenses:
        	License newLicense = new License();
        	byte[] token = new byte[64];
        	newLicense.setToken(token);
        	newLicense.persist();
        }
        
        
        //store default roles
        if (UserAuthority.countUserAuthoritys() < 1) {
            for (String authorityName : new String[] { UserAuthority.SYS_ADMIN, UserAuthority.EVENT_ADMIN, UserAuthority.USER_ADMIN, UserAuthority.USER }) {
                userAuthority = new UserAuthority();
                userAuthority.setAuthority(authorityName);
                userAuthority.persist();
            }
        }

        if (UserAuthorities.countUserAuthoritieses() < 1) {

            UserProfile tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("admin").getSingleResult();
            UserAuthority userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_SYS_ADMIN").getSingleResult();
            UserAuthoritiesID id = new UserAuthoritiesID();
            if (tmpUserProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("eventadmin").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult();
            if (tmpUserProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }
            
            tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("useradmin").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult();
            if (tmpUserProfile != null && userAuthority1 != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }

            tmpUserProfile = UserProfile.findUserProfilesByUsernameEquals("user").getSingleResult();
            userAuthority1 = UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_USER").getSingleResult();
            if (tmpUserProfile != null && userAuthority != null) {
                id.setUserAuthority(userAuthority1);
                id.setUserProfile(tmpUserProfile);
                if (UserAuthorities.findUserAuthorities(id) == null) {
                    userAuthorities = new UserAuthorities();
                    userAuthorities.setId(id);
                    userAuthorities.persist();
                }
            }
        }
    }
}