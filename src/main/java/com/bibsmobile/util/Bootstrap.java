package com.bibsmobile.util;

import com.bibsmobile.model.*;
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
            UserAuthoritiesID id = new UserAuthoritiesID(UserProfile.findUserProfilesByUsernameEquals("admin").getSingleResult(), UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_SYS_ADMIN").getSingleResult());

            userAuthorities = new UserAuthorities();
            userAuthorities.setId(id);
            userAuthorities.persist();

            id = new UserAuthoritiesID(UserProfile.findUserProfilesByUsernameEquals("eventadmin").getSingleResult(), UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_EVENT_ADMIN").getSingleResult());
            userAuthorities = new UserAuthorities();
            userAuthorities.setId(id);
            userAuthorities.persist();

            id = new UserAuthoritiesID(UserProfile.findUserProfilesByUsernameEquals("useradmin").getSingleResult(), UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_USER_ADMIN").getSingleResult());
            userAuthorities = new UserAuthorities();
            userAuthorities.setId(id);
            userAuthorities.persist();

            id = new UserAuthoritiesID(UserProfile.findUserProfilesByUsernameEquals("user").getSingleResult(), UserAuthority.findUserAuthoritysByAuthorityEquals("ROLE_USER").getSingleResult());
            userAuthorities = new UserAuthorities();
            userAuthorities.setId(id);
            userAuthorities.persist();
        }

    }

}