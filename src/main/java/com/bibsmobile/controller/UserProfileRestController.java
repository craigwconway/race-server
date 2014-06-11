package com.bibsmobile.controller;

import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Jevgeni on 11.06.2014.
 */
@Controller("/rest/userpofiles")
public class UserProfileRestController {

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        disableUserProfile(userProfile);
        userProfileService.saveUserProfile(userProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(userProfile.toJson(), headers, HttpStatus.CREATED);
    }

    private void disableUserProfile(UserProfile userProfile) {
        userProfile.setUserAuthorities(null);
        userProfile.setUsername(null);
        userProfile.setPassword(null);
        userProfile.setAccountNonExpired(false);
        userProfile.setAccountNonLocked(false);
        userProfile.setCredentialsNonExpired(false);
        userProfile.setEnabled(false);
    }

}
