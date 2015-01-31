package com.bibsmobile.controller;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
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
import com.google.gson.JsonObject;

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
    
    @RequestMapping(method = RequestMethod.GET)
    public String getRemainingLicenseUnits() {
    	JsonObject json = new JsonObject();
    	License current = License.findCurrentLicense();
    	DeviceInfo systemInfo = DeviceInfo.findDeviceInfo(new Long(1));
    	// TODO: DO not do this for versions of the software that do not require licensing
    	
    	// Handle null license
    	if(null == current) {
    		json.addProperty("error", "NO_LICENSE_FOUND");
    		return json.toString();
    	}
    	// Handle null systemInfo
    	if(null == systemInfo) {
    		json.addProperty("error", "CORRUPT_SYSTEM_INFO");
    		return json.toString();
    	}
    	// Check current units allocated in device info vs system info
    	long remainingUnits = current.getEndunits() - systemInfo.getRunnersUsed();
    	Date currentTime = new Date();
    	long remainingTime = current.getExpiretime() - currentTime.getTime();
    	if(0 < remainingUnits) {
    		json.addProperty("units", remainingUnits);
    	} else {
    		json.addProperty("units", 0);
    	}
    	int days = (int) (remainingTime / (1000*60*60*24));
    	if(0 < days) {
    		json.addProperty("days", days);
    	} else {
    		json.addProperty("days", 0);
    	}
    	return json.toString();
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
			DeviceInfo systemInfo = DeviceInfo.findDeviceInfo(new Long(1));
			newLicense.setToken(tokenBytes);
			System.out.println(newLicense.getMacAddress().length);
			System.out.println("get macaddress for system: " +  systemInfo );
			System.out.println(systemInfo.getMacAddress().length);
			System.out.println(Hex.encodeHexString(systemInfo.getMacAddress()));
			
			byte[] macaddr = systemInfo.getMacAddress();
			System.out.println(macaddr);
			if (!Arrays.equals(newLicense.getMacAddress(), systemInfo.getMacAddress())) {
				throw new Exception("License for another machine");
			}
			if (!newLicense.validateLength()) {
				throw new Exception("Invalid length license");
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
