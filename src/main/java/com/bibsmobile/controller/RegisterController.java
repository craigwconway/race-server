/**
 * 
 */
package com.bibsmobile.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.AccountCreationDto;
import com.bibsmobile.util.SpringJSONUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

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
		
		//Create account:
        List<UserAuthority> roleUserAuthorities = UserAuthority.findUserAuthoritysByAuthorityEquals(UserAuthority.EVENT_ADMIN).getResultList();
        UserAuthority roleUserAuthority;
        if (CollectionUtils.isEmpty(roleUserAuthorities)) {
            roleUserAuthority = new UserAuthority();
            roleUserAuthority.setAuthority(UserAuthority.EVENT_ADMIN);
            roleUserAuthority.persist();
        } else {
            roleUserAuthority = roleUserAuthorities.get(0);
        }
        
		UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(roleUserAuthority);
        id.setUserProfile(user);
        userAuthorities.setId(id);
        Set<UserAuthorities> ua = new HashSet<UserAuthorities>();
        try{ 
            ua = user.getUserAuthorities();
            ua = ua==null ? new HashSet<UserAuthorities>() : ua;
        } catch(Exception e) {
        	e.printStackTrace();
        }
       ua.add(userAuthorities);
       user.setUserAuthorities(ua);
       user.setAccountNonExpired(true);
       user.setAccountNonLocked(true);
       user.setEnabled(true);
       user.setCredentialsNonExpired(true);

        // save
        user.persist();
        userAuthorities.persist();

        // associate with user group

        UserGroup userGroup = new UserGroup();
        userGroup.setName(account.getOrgName());
        userGroup.setDescription(account.getOrgDescription());
        userGroup.setGroupType(UserGroupType.COMPANY);

        Set<UserGroupUserAuthority> userGroupUserAuthorities = new HashSet<>();

        UserGroupUserAuthority userGroupUserAuthority = new UserGroupUserAuthority();
        UserGroupUserAuthorityID userGroupUserAuthorityID = new UserGroupUserAuthorityID();
        userGroupUserAuthorityID.setUserAuthorities(userAuthorities);
        userGroupUserAuthorityID.setUserGroup(userGroup);
        userGroupUserAuthority.setId(userGroupUserAuthorityID);

        userGroupUserAuthorities.add(userGroupUserAuthority);

        userGroup.setUserGroupUserAuthorities(userGroupUserAuthorities);

        userGroup.persist();
        userGroupUserAuthority.persist();

        authenticateRegisteredUser(user);

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
	
    private void authenticateRegisteredUser(UserProfile userProfile) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userProfile, null, userProfile.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
	
}
