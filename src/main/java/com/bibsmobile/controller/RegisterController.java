/**
 * 
 */
package com.bibsmobile.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.bibsmobile.model.PaymentAccount;
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
import com.bibsmobile.util.UserProfileUtil;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Recipient;

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
	
    @Value("${stripe.com.secret.key}")
    private String secretKey;
	
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
		uiModel.addAttribute("account", account);
		System.out.println("Submitted step 2");
		if(UserProfile.countFindUserProfilesByUsernameEquals(account.getUsername()) > 0) {
			uiModel.addAttribute("duplicate", true);
			return "register/second";
		}
		return "register/third";
	}
	
	@RequestMapping(value="checkusername", method=RequestMethod.GET)
	public ResponseEntity<String> checkUsername(@RequestParam("username") String username) { 
		if(StringUtils.isEmpty(username)) {
			return new ResponseEntity<String>("bad", HttpStatus.OK);
		}
		try{
			long profiles = UserProfile.countFindUserProfilesByUsernameEquals(username);
			if(profiles == 0) {
				return new ResponseEntity<String>("good", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("bad", HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("bad", HttpStatus.OK);
		}
		
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
	
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public String editBankInfo(@RequestParam(value = "group", required= false) Long userGroupId,
			Model uiModel) {
		UserGroup group = null;
		UserProfile user = UserProfileUtil.getLoggedInUserProfile();
		if(userGroupId == null) {
			for(UserAuthorities authority : user.getUserAuthorities()) {
				System.out.println("got authority");
				if(authority.getUserAuthority().isAuthority(UserAuthority.EVENT_ADMIN)) {
					System.out.println("eventadmin");
					for(UserGroupUserAuthority ugua : authority.getUserGroupUserAuthorities()) {
						group = ugua.getUserGroup();
						System.out.println("got group");
						break;
					}
				}
			}
		} else {
			group = UserGroup.findUserGroup(userGroupId);
		}
		List<PaymentAccount> accounts = PaymentAccount.findActivePaymentAccountsForUser(user);
		if(accounts.isEmpty()) {
			uiModel.addAttribute("hasAccount", false);
		}
		if(accounts.size() == 1) {
			uiModel.addAttribute("activeAccount", accounts.get(0));
			Stripe.apiKey = this.secretKey;
			try {
				Recipient recipient = Recipient.retrieve(accounts.get(0).getStripeToken());
				uiModel.addAttribute("bankAccount", recipient.getActiveAccount());
			} catch (Exception e) {
				uiModel.addAttribute("accountDetailsError", true);
			}
			uiModel.addAttribute("activeAccountDetails", accounts.get(0).getStripeToken());
		}
		uiModel.addAttribute("accounts", accounts);
		uiModel.addAttribute("user", user);
		uiModel.addAttribute("userGroup", group);
		uiModel.addAttribute("orgId", group.getId());
		return "register/account";
	}
	
	@RequestMapping(value = "/signupbank", method = RequestMethod.POST, produces = "text/html")
	public String addAccount(Model uiModel, 
			@RequestParam(value="id", required=false) Long userId,
			@RequestParam(value="org", required=false) Long orgId,
			@RequestParam(value="username", required=false) String username,
			@RequestParam(value="password", required=false) String password) {
		uiModel.addAttribute("orgId", orgId);
		return "register/bankadd";
	}

	@RequestMapping(value = "/recipientadd", method = RequestMethod.POST, produces = "text/html")
	public String addAccount(Model uiModel, 
			@RequestParam(value="token") String stripeToken,
			@RequestParam(value="org", required = false) Long orgId) {
		System.out.println("Recieved a token, adding a recipient...");
		
		return "register/recipient";
	}	
	
    private void authenticateRegisteredUser(UserProfile userProfile) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userProfile, null, userProfile.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
	
}
