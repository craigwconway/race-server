// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.UserProfileController;
import com.bibsmobile.model.UserProfile;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

privileged aspect UserProfileController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserProfileController.showJson(@PathVariable("id") Long id) {
        UserProfile userProfile = userProfileService.findUserProfile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userProfile == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(userProfile.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserProfileController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<UserProfile> result = userProfileService.findAllUserProfiles();
        return new ResponseEntity<String>(UserProfile.toJsonArray(result), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> UserProfileController.createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        userProfileService.saveUserProfile(userProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
        headers.add("Location",uriBuilder.path(a.value()[0]+"/"+userProfile.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> UserProfileController.createFromJsonArray(@RequestBody String json) {
        for (UserProfile userProfile: UserProfile.fromJsonArrayToUserProfiles(json)) {
            userProfileService.saveUserProfile(userProfile);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> UserProfileController.updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        userProfile.setId(id);
        if (userProfileService.updateUserProfile(userProfile) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> UserProfileController.deleteFromJson(@PathVariable("id") Long id) {
        UserProfile userProfile = userProfileService.findUserProfile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userProfile == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        userProfileService.deleteUserProfile(userProfile);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByDropboxIdEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserProfileController.jsonFindUserProfilesByDropboxIdEquals(@RequestParam("dropboxId") String dropboxId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserProfile.toJsonArray(UserProfile.findUserProfilesByDropboxIdEquals(dropboxId).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEmailEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserProfileController.jsonFindUserProfilesByEmailEquals(@RequestParam("email") String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserProfile.toJsonArray(UserProfile.findUserProfilesByEmailEquals(email).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByForgotPasswordCodeEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserProfileController.jsonFindUserProfilesByForgotPasswordCodeEquals(@RequestParam("forgotPasswordCode") String forgotPasswordCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserProfile.toJsonArray(UserProfile.findUserProfilesByForgotPasswordCodeEquals(forgotPasswordCode).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByUsernameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserProfileController.jsonFindUserProfilesByUsernameEquals(@RequestParam("username") String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserProfile.toJsonArray(UserProfile.findUserProfilesByUsernameEquals(username).getResultList()), headers, HttpStatus.OK);
    }
    
}
