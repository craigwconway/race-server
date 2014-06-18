// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.UserAuthorityController;
import com.bibsmobile.model.UserAuthority;
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

privileged aspect UserAuthorityController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserAuthorityController.showJson(@PathVariable("id") Long id) {
        UserAuthority userAuthority = UserAuthority.findUserAuthority(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userAuthority == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(userAuthority.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserAuthorityController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<UserAuthority> result = UserAuthority.findAllUserAuthoritys();
        return new ResponseEntity<String>(UserAuthority.toJsonArray(result), headers, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> UserAuthorityController.createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        UserAuthority userAuthority = UserAuthority.fromJsonToUserAuthority(json);
        userAuthority.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
        headers.add("Location",uriBuilder.path(a.value()[0]+"/"+userAuthority.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> UserAuthorityController.createFromJsonArray(@RequestBody String json) {
        for (UserAuthority userAuthority: UserAuthority.fromJsonArrayToUserAuthoritys(json)) {
            userAuthority.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> UserAuthorityController.updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserAuthority userAuthority = UserAuthority.fromJsonToUserAuthority(json);
        userAuthority.setId(id);
        if (userAuthority.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> UserAuthorityController.deleteFromJson(@PathVariable("id") Long id) {
        UserAuthority userAuthority = UserAuthority.findUserAuthority(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userAuthority == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        userAuthority.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByAuthorityEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> UserAuthorityController.jsonFindUserAuthoritysByAuthorityEquals(@RequestParam("authority") String authority) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserAuthority.toJsonArray(UserAuthority.findUserAuthoritysByAuthorityEquals(authority).getResultList()), headers, HttpStatus.OK);
    }
    
}