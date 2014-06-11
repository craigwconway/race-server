package com.bibsmobile.util;

import com.bibsmobile.model.UserProfile;

/**
 * Created by Jevgeni on 11.06.2014.
 */
public class UserProfileUtil {

    public static void disableUserProfile(UserProfile userProfile) {
        userProfile.setUserAuthorities(null);
        userProfile.setUsername(null);
        userProfile.setPassword(null);
        userProfile.setAccountNonExpired(false);
        userProfile.setAccountNonLocked(false);
        userProfile.setCredentialsNonExpired(false);
        userProfile.setEnabled(false);
    }

}
