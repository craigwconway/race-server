// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.UserProfileController;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect UserProfileController_Roo_Controller {
    
    @Autowired
    UserProfileService UserProfileController.userProfileService;
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String UserProfileController.create(@Valid UserProfile userProfile, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userProfile);
            return "userprofiles/create";
        }
        uiModel.asMap().clear();
        userProfileService.saveUserProfile(userProfile);
        return "redirect:/userprofiles/" + encodeUrlPathSegment(userProfile.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String UserProfileController.createForm(Model uiModel) {
        populateEditForm(uiModel, new UserProfile());
        return "userprofiles/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String UserProfileController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("userprofile", userProfileService.findUserProfile(id));
        uiModel.addAttribute("itemId", id);
        return "userprofiles/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String UserProfileController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("userprofiles", userProfileService.findUserProfileEntries(firstResult, sizeNo));
            float nrOfPages = (float) userProfileService.countAllUserProfiles() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("userprofiles", userProfileService.findAllUserProfiles());
        }
        return "userprofiles/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String UserProfileController.update(@Valid UserProfile userProfile, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userProfile);
            return "userprofiles/update";
        }
        uiModel.asMap().clear();
        userProfileService.updateUserProfile(userProfile);
        return "redirect:/userprofiles/" + encodeUrlPathSegment(userProfile.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String UserProfileController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, userProfileService.findUserProfile(id));
        return "userprofiles/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String UserProfileController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserProfile userProfile = userProfileService.findUserProfile(id);
        userProfileService.deleteUserProfile(userProfile);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/userprofiles";
    }
    
    void UserProfileController.populateEditForm(Model uiModel, UserProfile userProfile) {
        uiModel.addAttribute("userProfile", userProfile);
        uiModel.addAttribute("events", Event.findAllEvents());
        uiModel.addAttribute("raceimages", RaceImage.findAllRaceImages());
        uiModel.addAttribute("userauthoritys", UserAuthority.findAllUserAuthoritys());
        uiModel.addAttribute("userprofiles", userProfileService.findAllUserProfiles());
    }
    
    String UserProfileController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
