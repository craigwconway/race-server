package com.bibsmobile.controller;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;

@RequestMapping("/userprofiles")
@Controller
@RooWebJson(jsonObject = UserProfile.class)
@RooWebScaffold(path = "userprofiles", formBackingObject = UserProfile.class)
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

    void populateEditForm(Model uiModel, UserProfile userProfile) {
        uiModel.addAttribute("userProfile", userProfile);
        uiModel.addAttribute("userauthoritys", UserAuthority.findAllUserAuthoritys());
        addDateTimeFormatPatterns(uiModel);
    }
    
}
