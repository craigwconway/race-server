package com.bibsmobile.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.UserProfile;

@RequestMapping("/userprofiles")
@Controller
@RooWebJson(jsonObject = UserProfile.class)
@RooWebScaffold(path = "userprofiles", formBackingObject = UserProfile.class)
public class UserProfileController {

    public UserProfile byUsernameEquals(String username) {
        UserProfile user = null;
        try {
            user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
