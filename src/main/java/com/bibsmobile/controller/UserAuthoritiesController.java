package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;

@RequestMapping("/userauthorities")
@Controller
public class UserAuthoritiesController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "userprofile", required = true) Long userProfileId, Model uiModel) {
        UserAuthorities userAuthorities = new UserAuthorities();
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        UserAuthoritiesID id = new UserAuthoritiesID(userProfile, userProfile.getNotAddedAuthorities().get(0));
        userAuthorities.setId(id);

        this.populateEditForm(uiModel, userAuthorities);
        return "userauthorities/createUA";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserAuthorities userAuthorities, BindingResult bindingResult, Model uiModel) {
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(userAuthorities.getUserAuthority());
        id.setUserProfile(userAuthorities.getUserProfile());
        userAuthorities.setId(id);
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, userAuthorities);
            return "userauthorities/createUA";
        }
        uiModel.asMap().clear();
        userAuthorities.persist();
        return "redirect:/userauthorities?userprofile=" + userAuthorities.getUserProfile().getId();
    }

    @RequestMapping(value = "/{id}/{aid}", produces = "text/html")
    public String show(@PathVariable("id") Long userProfileId, @PathVariable("aid") Long authorityId, Model uiModel) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(new UserAuthoritiesID(UserProfile.findUserProfile(userProfileId), UserAuthority
                .findUserAuthority(authorityId)));
        uiModel.addAttribute("userauthorities", userAuthorities);
        uiModel.addAttribute("itemId", userProfileId + "/" + authorityId);
        return "userauthorities/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "userprofile", required = true) Long userProfileId, Model uiModel) {
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        if (userProfile != null) {
            TypedQuery<UserAuthorities> userAuthoritiesesByUserProfile = UserAuthorities.findUserAuthoritiesesByUserProfile(userProfile);
            uiModel.addAttribute("userauthoritieses", userAuthoritiesesByUserProfile.getResultList());
        }
        uiModel.addAttribute("userprofile", userProfile);
        return "userauthorities/listUA";
    }

    @RequestMapping(value = "/{id}/{aid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long userProfileId, @PathVariable("aid") Long authorityId, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        UserAuthority userAuthority = UserAuthority.findUserAuthority(authorityId);
        if (userAuthority != null && userProfile != null) {
            UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(new UserAuthoritiesID(userProfile, userAuthority));
            if (userAuthorities != null) {
                userAuthorities.remove();
            }
        }
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/userauthorities?userprofile=" + userProfileId;
    }

    void populateEditForm(Model uiModel, UserAuthorities userAuthorities) {
        uiModel.addAttribute("userAuthorities", userAuthorities);
        uiModel.addAttribute("userauthoritys", userAuthorities.getId().getUserProfile().getNotAddedAuthorities());
        List<UserProfile> userProfiles = new ArrayList<>();
        userProfiles.add(userAuthorities.getId().getUserProfile());
        uiModel.addAttribute("userprofiles", userProfiles);
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") UserAuthoritiesID id) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userAuthorities == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userAuthorities.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<UserAuthorities> result = UserAuthorities.findAllUserAuthoritieses();
        return new ResponseEntity<>(UserAuthorities.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        UserAuthorities userAuthorities = UserAuthorities.fromJsonToUserAuthorities(json);
        userAuthorities.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = this.getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + userAuthorities.getId()).build().toUriString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (UserAuthorities userAuthorities : UserAuthorities.fromJsonArrayToUserAuthoritieses(json)) {
            userAuthorities.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") UserAuthoritiesID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserAuthorities userAuthorities = UserAuthorities.fromJsonToUserAuthorities(json);
        userAuthorities.setId(id);
        if (userAuthorities.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") UserAuthoritiesID id) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userAuthorities == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        userAuthorities.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByUserAuthority", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserAuthoritiesesByUserAuthority(@RequestParam("userAuthority") UserAuthority userAuthority) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserAuthorities.toJsonArray(UserAuthorities.findUserAuthoritiesesByUserAuthority(userAuthority).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByUserProfile", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserAuthoritiesesByUserProfile(@RequestParam("userProfile") UserProfile userProfile) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserAuthorities.toJsonArray(UserAuthorities.findUserAuthoritiesesByUserProfile(userProfile).getResultList()), headers, HttpStatus.OK);
    }

}
