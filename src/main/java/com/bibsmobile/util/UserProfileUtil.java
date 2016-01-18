package com.bibsmobile.util;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

public final class UserProfileUtil {

    private UserProfileUtil() {
        super();
    }

    /**
     * create a user with given authorities
     * @param userProfile
     * @param authority
     * @param userGroupName
     * @return
     */
    public static UserProfile createUser(UserProfile userProfile, String authority, String userGroupName) {
        // get the authority, fail if not found
        UserAuthority userAuthority = UserAuthority.findUserAuthoritysByAuthorityEquals(authority).getSingleResult();
        if (userAuthority == null)
            throw new IllegalArgumentException("Invalid authority '" + authority + "'");

        // associate user profile with the authority
        UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(userAuthority);
        id.setUserProfile(userProfile);
        userAuthorities.setId(id);
        userProfile.getUserAuthorities().add(userAuthorities);

        // save
        userProfile.persist();
        userAuthorities.persist();

        // associate with user group
        if (StringUtils.isNotEmpty(userGroupName)) {
            UserGroup userGroup = new UserGroup();
            userGroup.setName(userGroupName);
            userGroup.setGroupType(UserGroupType.COMPANY);

            Set<UserGroupUserAuthority> userGroupUserAuthorities = new HashSet<>();

            UserGroupUserAuthority userGroupUserAuthority = new UserGroupUserAuthority();
            UserGroupUserAuthorityID userGroupUserAuthorityID = new UserGroupUserAuthorityID();
            userGroupUserAuthorityID.setUserAuthorities(userAuthorities);
            userGroupUserAuthorityID.setUserGroup(userGroup);
            userGroupUserAuthority.setId(userGroupUserAuthorityID);

            userGroupUserAuthorities.add(userGroupUserAuthority);

            userGroup.setUserGroupUserAuthorities(userGroupUserAuthorities);

            userGroup.persist();
            userGroupUserAuthority.persist();
        }

        return userProfile;
    }

    /**
     * create a user with given authorities
     * @param userProfile
     * @param authority
     * @param userGroupName
     * @param userGroupDescription
     * @return
     */
    public static UserProfile createUser(UserProfile userProfile, String authority, String userGroupName, String userGroupDescription) {
        // get the authority, fail if not found
        UserAuthority userAuthority = UserAuthority.findUserAuthoritysByAuthorityEquals(authority).getSingleResult();
        if (userAuthority == null)
            throw new IllegalArgumentException("Invalid authority '" + authority + "'");

        // associate user profile with the authority
        UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(userAuthority);
        id.setUserProfile(userProfile);
        userAuthorities.setId(id);
        Set<UserAuthorities> ua = new HashSet<UserAuthorities>();
        try{ 
            ua = userProfile.getUserAuthorities();
        } catch(Exception e) {
        	e.printStackTrace();
        }
       ua.add(userAuthorities);
       userProfile.setUserAuthorities(ua);
       userProfile.setAccountNonExpired(true);
       userProfile.setAccountNonLocked(true);
       userProfile.setEnabled(true);
       userProfile.setCredentialsNonExpired(true);

        // save
        userProfile.persist();
        userAuthorities.persist();

        // associate with user group
        if (StringUtils.isNotEmpty(userGroupName)) {
            UserGroup userGroup = new UserGroup();
            userGroup.setName(userGroupName);
            userGroup.setDescription(userGroupDescription);
            userGroup.setGroupType(UserGroupType.COMPANY);

            Set<UserGroupUserAuthority> userGroupUserAuthorities = new HashSet<>();

            UserGroupUserAuthority userGroupUserAuthority = new UserGroupUserAuthority();
            UserGroupUserAuthorityID userGroupUserAuthorityID = new UserGroupUserAuthorityID();
            userGroupUserAuthorityID.setUserAuthorities(userAuthorities);
            userGroupUserAuthorityID.setUserGroup(userGroup);
            userGroupUserAuthority.setId(userGroupUserAuthorityID);

            userGroupUserAuthorities.add(userGroupUserAuthority);

            userGroup.setUserGroupUserAuthorities(userGroupUserAuthorities);

            userGroup.persist();
            userGroupUserAuthority.persist();
        }

        return userProfile;
    }    
    
    /**
     * create an user object while only copying safe properties of the user.
     * does not save the new user.
     * @param unsafeUser
     * @return
     */
    public static UserProfile createSafeUserFromUnsafe(UserProfile unsafeUser) {
        return UserProfileUtil.createSafeUserFromUnsafe(unsafeUser, null);
    }

    /**
     * create an user object while only copying safe properties of the user.
     * makes sure that no new user is created when safeUser is not null.
     * does not save the changes.
     * @param unsafeUser
     * @param safeUser
     * @return
     */
    public static UserProfile createSafeUserFromUnsafe(UserProfile unsafeUser, UserProfile safeUser) {
        if (safeUser == null) safeUser = new UserProfile();

        if (unsafeUser.getBirthdate() != null)
            safeUser.setBirthdate(unsafeUser.getBirthdate());
        if (unsafeUser.getPhone() != null)
            safeUser.setPhone(unsafeUser.getPhone());
        if (unsafeUser.getFirstname() != null)
            safeUser.setFirstname(unsafeUser.getFirstname());
        if (unsafeUser.getLastname() != null)
            safeUser.setLastname(unsafeUser.getLastname());
        if (unsafeUser.getCity() != null)
            safeUser.setCity(unsafeUser.getCity());
        if (unsafeUser.getState() != null)
            safeUser.setState(unsafeUser.getState());
        if (unsafeUser.getBirthdate() != null)
            safeUser.setBirthdate(unsafeUser.getBirthdate());
        if (unsafeUser.getGender() != null)
            safeUser.setGender(unsafeUser.getGender());
        if (unsafeUser.getEmail() != null)
            safeUser.setEmail(unsafeUser.getEmail());
        if (unsafeUser.getUsername() != null)
            safeUser.setUsername(unsafeUser.getUsername());
        if (unsafeUser.getPassword() != null)
            safeUser.setPassword(unsafeUser.getPassword());
        if (unsafeUser.getFacebookId() != null)
            safeUser.setFacebookId(unsafeUser.getFacebookId());
        if (unsafeUser.getTwitterId() != null)
            safeUser.setTwitterId(unsafeUser.getTwitterId());
        if (unsafeUser.getGoogleId() != null)
            safeUser.setGoogleId(unsafeUser.getGoogleId());
        if (unsafeUser.getAddressLine1() != null)
            safeUser.setAddressLine1(unsafeUser.getAddressLine1());
        if (unsafeUser.getAddressLine2() != null)
            safeUser.setAddressLine2(unsafeUser.getAddressLine2());
        if (unsafeUser.getZipCode() != null)
            safeUser.setZipCode(unsafeUser.getZipCode());
        if (unsafeUser.getEmergencyContactName() != null)
            safeUser.setEmergencyContactName(unsafeUser.getEmergencyContactName());
        if (unsafeUser.getEmergencyContactPhone() != null)
            safeUser.setEmergencyContactPhone(unsafeUser.getEmergencyContactPhone());
        if (unsafeUser.getHearFrom() != null)
            safeUser.setHearFrom(unsafeUser.getHearFrom());
        return safeUser;
    }

    /**
     * disable a user profile
     * @param userProfile
     */
    public static void disableUserProfile(UserProfile userProfile) {
        userProfile.setUserAuthorities(null);
        userProfile.setUsername(null);
        userProfile.setPassword(null);
        userProfile.setAccountNonExpired(false);
        userProfile.setAccountNonLocked(false);
        userProfile.setCredentialsNonExpired(false);
        userProfile.setEnabled(false);
    }

    /**
     * returns an instance of the user profile which is currently signed in
     * @return null, if anonymous or not logged in
     */
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

    /**
     * returns the dropbox access token of the logged in user
     * @return null if no access token available or not logged in
     */
    public static String getLoggedInDropboxAccessToken() {
        UserProfile up = UserProfileUtil.getLoggedInUserProfile();
        if (up == null)
            return null;
        return up.getDropboxAccessToken();
    }
    
    

}
