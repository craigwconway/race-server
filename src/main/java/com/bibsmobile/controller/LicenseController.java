package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.UserProfileUtil;

/**
 * License Controller -- Used to apply licenses to reader and extract license tokens
 * to give to the liceman.
 * 
 * @author galen
 *
 */
@RequestMapping("/licensing")
@Controller
public class LicenseController {
    @RequestMapping(produces = "text/html")
    public String list( Model uiModel) {
        UserProfile user = UserProfileUtil.getLoggedInUserProfile();
        return "licensing/upload";
    }
}
