package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;

@RequestMapping("/userprofiles")
@Controller
public class UserProfileController {

    @RequestMapping(value = "/byusername/{username}", produces = "text/html")
    public static String showUsername(@PathVariable("username") String username, Model uiModel) {
        UserProfile u = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
        uiModel.addAttribute("userprofile", u);
        uiModel.addAttribute("itemId", u.getId());
        return "userprofiles/show";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        UserProfile u = UserProfile.findUserProfile(id);
        uiModel.addAttribute("userprofile", u);
        uiModel.addAttribute("itemId", u.getId());
        return "userprofiles/show";
    }
    
    
    @RequestMapping(value = "/search/byusergroup/{userGroupId}", method = RequestMethod.GET)
    @ResponseBody
    public String findByUserGroup(@PathVariable Long userGroupId) {
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        Map<Long, UserProfile> userProfiles = new HashMap<>();
        if (userGroup != null) {
            for (UserGroupUserAuthority userGroupUserAuthority : userGroup.getUserGroupUserAuthorities()) {
                UserProfile userProfile = userGroupUserAuthority.getUserAuthorities().getUserProfile();
                if (!userProfiles.containsKey(userProfile.getId())) {
                    userProfiles.put(userProfile.getId(), userProfile);
                }
            }
        }
        return UserProfile.toJsonArray(userProfiles.values());
    }

    @RequestMapping(value = "/search/teamcaptainsbyevent/{eventId}", method = RequestMethod.GET)
    @ResponseBody
    public String findTeamCaptainsByEvent(@PathVariable Long eventId) {
        Event event = Event.findEvent(eventId);
        Map<Long, UserProfile> teamCaptains = new HashMap<>();
        if (event != null) {
            for (EventUserGroup eventUserGroup : event.getEventUserGroups()) {
                for (UserGroupUserAuthority userGroupUserAuthority : eventUserGroup.getUserGroup().getUserGroupUserAuthorities()) {
                    UserAuthorities userAuthorities = userGroupUserAuthority.getUserAuthorities();
                    UserProfile userProfile = userAuthorities.getUserProfile();
                    if (userAuthorities.getUserAuthority().getAuthority().equals("ROLE_TEAMCAPTAIN") && !teamCaptains.containsKey(userProfile.getId())) {
                        teamCaptains.put(userProfile.getId(), userProfile);
                    }
                }
            }
        }
        return UserProfile.toJsonArray(teamCaptains.values());
    }

    void populateEditForm(Model uiModel, UserProfile userProfile) {
        uiModel.addAttribute("userProfile", userProfile);
        uiModel.addAttribute("userauthoritys", UserAuthority.findAllUserAuthoritys());
        this.addDateTimeFormatPatterns(uiModel);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        UserProfile userProfile = this.userProfileService.findUserProfile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userProfile == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userProfile.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<UserProfile> result = this.userProfileService.findAllUserProfiles();
        return new ResponseEntity<>(UserProfile.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        this.userProfileService.saveUserProfile(userProfile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = this.getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + userProfile.getId()).build().toUriString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (UserProfile userProfile : UserProfile.fromJsonArrayToUserProfiles(json)) {
            this.userProfileService.saveUserProfile(userProfile);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserProfile userProfile = UserProfile.fromJsonToUserProfile(json);
        userProfile.setId(id);
        if (this.userProfileService.updateUserProfile(userProfile) == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        UserProfile userProfile = this.userProfileService.findUserProfile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userProfile == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        this.userProfileService.deleteUserProfile(userProfile);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByDropboxIdEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserProfilesByDropboxIdEquals(@RequestParam("dropboxId") String dropboxId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserProfile.toJsonArray(UserProfile.findUserProfilesByDropboxIdEquals(dropboxId).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEmailEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserProfilesByEmailEquals(@RequestParam("email") String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserProfile.toJsonArray(UserProfile.findUserProfilesByEmailEquals(email).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByForgotPasswordCodeEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserProfilesByForgotPasswordCodeEquals(@RequestParam("forgotPasswordCode") String forgotPasswordCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserProfile.toJsonArray(UserProfile.findUserProfilesByForgotPasswordCodeEquals(forgotPasswordCode).getResultList()), headers,
                HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByUsernameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserProfilesByUsernameEquals(@RequestParam("username") String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserProfile.toJsonArray(UserProfile.findUserProfilesByUsernameEquals(username).getResultList()), headers, HttpStatus.OK);
    }

    @Autowired
    UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserProfile userProfile, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, userProfile);
            return "userprofiles/create";
        }
        uiModel.asMap().clear();
        this.userProfileService.saveUserProfile(userProfile);
        return "redirect:/userprofiles/" + this.encodeUrlPathSegment(userProfile.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new UserProfile());
        return "userprofiles/create";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("userprofiles", UserProfile.findUserProfileEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) this.userProfileService.countAllUserProfiles() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("userprofiles", UserProfile.findAllUserProfiles(sortFieldName, sortOrder));
        }
        this.addDateTimeFormatPatterns(uiModel);
        return "userprofiles/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid UserProfile userProfile, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, userProfile);
            return "userprofiles/update";
        }
        uiModel.asMap().clear();
        this.userProfileService.updateUserProfile(userProfile);
        return "redirect:/userprofiles/" + this.encodeUrlPathSegment(userProfile.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, this.userProfileService.findUserProfile(id));
        return "userprofiles/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        UserProfile userProfile = this.userProfileService.findUserProfile(id);
        this.userProfileService.deleteUserProfile(userProfile);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/userprofiles";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("userProfile_birthdate_date_format", "MM-dd-yyyy");
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
}
