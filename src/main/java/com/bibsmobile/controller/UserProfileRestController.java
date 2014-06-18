package com.bibsmobile.controller;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.UserProfileUtil;
import flexjson.JSONSerializer;
import org.apache.commons.collections.CollectionUtils;
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

import java.util.List;

/**
 * Created by Jevgeni on 11.06.2014.
 */
@RequestMapping("/rest/userpofiles")
@Controller
public class UserProfileRestController {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_EVENT_ADMIN = "ROLE_EVENT_ADMIN";
    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        UserProfileUtil.disableUserProfile(userProfile);
        userProfileService.saveUserProfile(userProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(userProfile.toJson(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/regularuser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createRegularUserFromJson(@RequestBody String json) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        List<UserAuthority> roleUserAuthorities = UserAuthority.findUserAuthoritysByAuthorityEquals(ROLE_USER).getResultList();
        UserAuthority roleUserAuthority;
        if (CollectionUtils.isEmpty(roleUserAuthorities)) {
            roleUserAuthority = new UserAuthority();
            roleUserAuthority.setAuthority(ROLE_USER);
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
        userProfileService.saveUserProfile(userProfile);
        userAuthorities.persist();
        authenticateRegisteredUser(userProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new JSONSerializer().exclude("*.class").serialize(userProfile), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/eventadmin", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createEventAdminFromJson(@RequestBody String json) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        List<UserAuthority> roleUserAuthorities = UserAuthority.findUserAuthoritysByAuthorityEquals(ROLE_EVENT_ADMIN).getResultList();
        UserAuthority roleUserAuthority;
        if (CollectionUtils.isEmpty(roleUserAuthorities)) {
            roleUserAuthority = new UserAuthority();
            roleUserAuthority.setAuthority(ROLE_EVENT_ADMIN);
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
        userProfileService.saveUserProfile(userProfile);
        userAuthorities.persist();
        authenticateRegisteredUser(userProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new JSONSerializer().exclude("*.class").serialize(userProfile), headers, HttpStatus.CREATED);
    }

    private void authenticateRegisteredUser(UserProfile userProfile) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userProfile, null, userProfile.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


}
