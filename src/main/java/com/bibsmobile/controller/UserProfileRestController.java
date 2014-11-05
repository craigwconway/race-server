package com.bibsmobile.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.wrapper.UserProfileWrapper;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.UserProfileUtil;

import flexjson.JSONSerializer;

@RequestMapping("/rest/userpofiles")
@Controller
public class UserProfileRestController {

    private static final Map<String, String> userNameExistsResponse = Collections.unmodifiableMap(new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            this.put("status", "error");
            this.put("msg", "Username already exist");
        }
    });

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        // set standard user permissions
        List<UserAuthority> roleUserAuthorities = UserAuthority.findUserAuthoritysByAuthorityEquals(UserAuthority.USER).getResultList();
        UserAuthority roleUserAuthority;
        if (CollectionUtils.isEmpty(roleUserAuthorities)) {
            roleUserAuthority = new UserAuthority();
            roleUserAuthority.setAuthority(UserAuthority.USER);
            roleUserAuthority.persist();
        } else {
            roleUserAuthority = roleUserAuthorities.get(0);
        }
        UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(roleUserAuthority);
        id.setUserProfile(userProfile);
        userAuthorities.setId(id);
        userProfile.setUserAuthorities(new HashSet<UserAuthorities>());
        userProfile.getUserAuthorities().add(userAuthorities);
        this.userProfileService.saveUserProfile(userProfile);
        userAuthorities.persist();

        if (userProfile.getId() == null) {
            UserProfileUtil.disableUserProfile(userProfile);
            this.userProfileService.saveUserProfile(userProfile);
        }

        // automatically authenticate user
        this.authenticateRegisteredUser(userProfile);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new JSONSerializer().serialize(userProfile), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/current", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> updateCurrentUser(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        UserProfile currentUser = UserProfileUtil.getLoggedInUserProfile();
        if (currentUser == null)
            return new ResponseEntity<>("{\"status\": \"error\", \"msg\": \"not logged in\"}", HttpStatus.UNAUTHORIZED);

        UserProfile updatedUser = UserProfile.fromJsonToUserProfile(json);

        // check that username and id are equal
        boolean sameId = currentUser.getId().equals(updatedUser.getId());
        if (!sameId)
            return new ResponseEntity<>("{\"status\": \"error\", \"msg\": \"your user is " + currentUser.getId() + "\"}", HttpStatus.UNAUTHORIZED);
        boolean usernameChanged = !currentUser.getUsername().equals(updatedUser.getUsername());
        if (usernameChanged && CollectionUtils.isNotEmpty(UserProfile.findUserProfilesByUsernameEquals(updatedUser.getUsername()).getResultList())) {
            return new ResponseEntity<>(new JSONSerializer().serialize(userNameExistsResponse), headers, HttpStatus.BAD_REQUEST);
        }

        // only overwrite whitelisted attributes
        if (updatedUser.getBirthdate() != null)
            currentUser.setBirthdate(updatedUser.getBirthdate());
        if (updatedUser.getPhone() != null)
            currentUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getFirstname() != null)
            currentUser.setFirstname(updatedUser.getFirstname());
        if (updatedUser.getLastname() != null)
            currentUser.setLastname(updatedUser.getLastname());
        if (updatedUser.getCity() != null)
            currentUser.setCity(updatedUser.getCity());
        if (updatedUser.getState() != null)
            currentUser.setState(updatedUser.getState());
        if (updatedUser.getBirthdate() != null)
            currentUser.setBirthdate(updatedUser.getBirthdate());
        if (updatedUser.getGender() != null)
            currentUser.setGender(updatedUser.getGender());
        if (updatedUser.getEmail() != null)
            currentUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getUsername() != null)
            currentUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getPassword() != null)
            currentUser.setPassword(updatedUser.getPassword());
        if (updatedUser.getFacebookId() != null)
            currentUser.setFacebookId(updatedUser.getFacebookId());
        if (updatedUser.getTwitterId() != null)
            currentUser.setTwitterId(updatedUser.getTwitterId());
        if (updatedUser.getGoogleId() != null)
            currentUser.setGoogleId(updatedUser.getGoogleId());
        if (updatedUser.getAddressLine1() != null)
            currentUser.setAddressLine1(updatedUser.getAddressLine1());
        if (updatedUser.getAddressLine2() != null)
            currentUser.setAddressLine2(updatedUser.getAddressLine2());
        if (updatedUser.getZipCode() != null)
            currentUser.setZipCode(updatedUser.getZipCode());
        if (updatedUser.getEmergencyContactName() != null)
            currentUser.setEmergencyContactName(updatedUser.getEmergencyContactName());
        if (updatedUser.getEmergencyContactPhone() != null)
            currentUser.setEmergencyContactPhone(updatedUser.getEmergencyContactPhone());
        if (updatedUser.getHearFrom() != null)
            currentUser.setHearFrom(updatedUser.getHearFrom());

        currentUser.persist();

        return new ResponseEntity<>(updatedUser.toJson(), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkusername", headers = "Accept=application/json")
    public ResponseEntity<String> checkUsername(@RequestParam("username") String userName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (CollectionUtils.isNotEmpty(UserProfile.findUserProfilesByUsernameEquals(userName).getResultList())) {
            return new ResponseEntity<>(new JSONSerializer().serialize(userNameExistsResponse), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/regularuser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createRegularUserFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);

        if (CollectionUtils.isNotEmpty(UserProfile.findUserProfilesByUsernameEquals(userProfile.getUsername()).getResultList())) {
            return new ResponseEntity<>(new JSONSerializer().serialize(userNameExistsResponse), headers, HttpStatus.OK);
        }
        List<UserAuthority> roleUserAuthorities = UserAuthority.findUserAuthoritysByAuthorityEquals(UserAuthority.USER).getResultList();
        UserAuthority roleUserAuthority;
        if (CollectionUtils.isEmpty(roleUserAuthorities)) {
            roleUserAuthority = new UserAuthority();
            roleUserAuthority.setAuthority(UserAuthority.USER);
            roleUserAuthority.persist();
        } else {
            roleUserAuthority = roleUserAuthorities.get(0);
        }
        UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(roleUserAuthority);
        id.setUserProfile(userProfile);
        userAuthorities.setId(id);
        userProfile.getUserAuthorities().add(userAuthorities);
        this.userProfileService.saveUserProfile(userProfile);
        userAuthorities.persist();
        this.authenticateRegisteredUser(userProfile);
        return new ResponseEntity<>(new JSONSerializer().exclude("*.class").serialize(userProfile), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/eventadmin", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createEventAdminFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        UserProfileWrapper userProfileWrapper = UserProfileWrapper.fromJsonToUserProfileWrapper(json);
        UserProfile userProfile = userProfileWrapper.getUserProfile();

        if (CollectionUtils.isNotEmpty(UserProfile.findUserProfilesByUsernameEquals(userProfile.getUsername()).getResultList())) {
            return new ResponseEntity<>(new JSONSerializer().serialize(userNameExistsResponse), headers, HttpStatus.OK);
        }

        String userGroupName = userProfileWrapper.getUserGroupName();
        List<UserAuthority> roleUserAuthorities = UserAuthority.findUserAuthoritysByAuthorityEquals(UserAuthority.EVENT_ADMIN).getResultList();
        UserAuthority roleUserAuthority;
        if (CollectionUtils.isEmpty(roleUserAuthorities)) {
            roleUserAuthority = new UserAuthority();
            roleUserAuthority.setAuthority(UserAuthority.EVENT_ADMIN);
            roleUserAuthority.persist();
        } else {
            roleUserAuthority = roleUserAuthorities.get(0);
        }
        UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(roleUserAuthority);
        id.setUserProfile(userProfile);
        userAuthorities.setId(id);
        userProfile.getUserAuthorities().add(userAuthorities);
        this.userProfileService.saveUserProfile(userProfile);
        userAuthorities.persist();

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

            // Cascade.ALL to userGroupUserAuthorities
            userGroup.persist();
        }
        this.authenticateRegisteredUser(userProfile);
        return new ResponseEntity<>(new JSONSerializer().exclude("*.class").serialize(userProfile), headers, HttpStatus.CREATED);
    }

    private void authenticateRegisteredUser(UserProfile userProfile) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userProfile, null, userProfile.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
