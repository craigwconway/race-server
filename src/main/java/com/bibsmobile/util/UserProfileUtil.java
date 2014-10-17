package com.bibsmobile.util;

import com.bibsmobile.model.UserProfile;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public static UserProfile getLoggedInUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // checks whether user is unauthenticated
        // TODO needs to be done differently, very unperformant
        if (auth instanceof AnonymousAuthenticationToken)
            return null;
        // this is hibernate detached causing all kind of problems
        UserProfile detachedProfile = (UserProfile) auth.getPrincipal();
        return UserProfile.findUserProfile(detachedProfile.getId()); // this is
                                                                     // attached
                                                                     // now
    }

    public static String getLoggedInDropboxAccessToken() {
        UserProfile up = UserProfileUtil.getLoggedInUserProfile();
        if (up == null)
            return null;
        return up.getDropboxAccessToken();
    }

}
