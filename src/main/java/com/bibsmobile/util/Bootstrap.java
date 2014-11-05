package com.bibsmobile.util;

import org.quartz.SchedulerException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.bibsmobile.job.BaseJob;
import com.bibsmobile.job.CartExpiration;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        UserAuthority userAuthority = null;
        UserAuthorities userAuthorities = null;
        UserProfile userProfile = null;

        // expire leftover carts at startup
        try {
            BaseJob.scheduleNow(CartExpiration.class);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        // store default readers
        if (TimerConfig.countTimerConfigs() < 1) {
            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs001.bibsmobile.com");
            timerConfig.persist(); // reader 1
            timerConfig = new TimerConfig();
            timerConfig.setUrl("tmr://bibs002.bibsmobile.com");
            timerConfig.setPosition(1);
            timerConfig.persist(); // reader 2
        }

        // store default users
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

        // store default roles
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