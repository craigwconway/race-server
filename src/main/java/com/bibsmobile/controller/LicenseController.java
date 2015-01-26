package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String htmlLicenseResponse(@ModelAttribute("license") MultipartFile uploadFile, Model uiModel) {
    	System.out.println("Recieved post of license");
    	UserProfile user = UserProfileUtil.getLoggedInUserProfile();
    	return "licensing/success";
    }
}
