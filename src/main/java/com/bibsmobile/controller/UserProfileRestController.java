package com.bibsmobile.controller;

import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.UserProfileUtil;
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
@RequestMapping("/rest/userpofiles")
@Controller
public class UserProfileRestController {

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

}
