/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Badge;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Series;
import com.bibsmobile.model.SeriesRegion;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.AccountCreationDto;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Controls for creating badges to be given out in the series.
 * @author galen
 *
 */
@RequestMapping("/register")
@Controller
public class RegisterController {
	
    @Autowired
    private StandardPasswordEncoder encoder;
	
	@RequestMapping(value = "", produces = "text/html")
	public String startPage(Model uiModel) {
		return "register/first";
	}

	@RequestMapping(value = "first", method= RequestMethod.POST, produces = "text/html")
	public String submitStartPage(Model uiModel, AccountCreationDto account) {
		System.out.println("Submitted step 1");
		uiModel.addAttribute("account", account);
		return "register/second";
	}
	
	@RequestMapping(value = "second", method= RequestMethod.POST, produces = "text/html")
	public String submitSecondPage(Model uiModel, AccountCreationDto account) {
		System.out.println("Submitted step 2");
		uiModel.addAttribute("account", account);
		return "register/third";
	}

	@RequestMapping(value = "third", method= RequestMethod.POST, produces = "text/html")
	public String submitThirdPage(Model uiModel, AccountCreationDto account) {
		System.out.println("Submitted step 3");
		uiModel.addAttribute("account", account);
		return "register/fourth";
	}
	
	@RequestMapping(value = "fourth", method= RequestMethod.POST, produces = "text/html")
	public String submitFourthPage(Model uiModel, AccountCreationDto account) {
		System.out.println("Submitted step 4");
		uiModel.addAttribute("account", account);
		
		UserProfile user = new UserProfile();
		user.setUsername(account.getUsername());
		user.setPassword(encoder.encode(account.getPassword()));
		user.setPhone(account.getPhone());
		user.setEmail(account.getEmail());
		user.setFirstname(account.getFirstname());
		user.setLastname(account.getLastname());
		
		UserProfileUtil.createUser(user, UserAuthority.EVENT_ADMIN, account.getOrgName(), account.getOrgDescription());
		return "register/created";
	}
	
	@RequestMapping(value = "/signupbank", method = RequestMethod.POST, produces = "text/html")
	public String addAccount(Model uiModel, 
			@RequestParam(value="id", required=false) Long userId,
			@RequestParam(value="org", required=false) Long orgId,
			@RequestParam(value="username", required=false) String username,
			@RequestParam(value="password", required=false) String password) {
		return "register/bankadd";
	}
	
}
