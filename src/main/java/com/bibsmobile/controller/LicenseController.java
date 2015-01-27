package com.bibsmobile.controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bibsmobile.model.DeviceInfo;
import com.bibsmobile.model.License;
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
    public String htmlLicenseResponse(@ModelAttribute("license") MultipartFile license, Model uiModel) {
    	System.out.println("Recieved post of license");
		UserProfile user = UserProfileUtil.getLoggedInUserProfile();
    	// Break down the license token:
    	try {
    		// Check that this file has the length of a licensefile. Otherwise it is likely an attack on the bibs system
			byte[] tokenBytes = license.getBytes();
			if(null == tokenBytes || 0 == tokenBytes.length) {
				throw new Exception("Empty license token");
			}
			License newLicense = new License();
			DeviceInfo systemInfo = DeviceInfo.findDeviceInfo((long) 1);
			newLicense.setToken(tokenBytes);
			if (!Arrays.equals(newLicense.getMacAddress(), systemInfo.getMacAddress())) {
				throw new Exception("License for another machine");
			}
			System.out.println(newLicense);
			newLicense.persist();
	    	return "licensing/success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "licensing/invalid";
		} catch (Exception e) {
			e.printStackTrace();
			return "licensing/invalid";
		}

    }
}
