package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.PaymentAccount;
import com.bibsmobile.model.Payout;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.BankAddRequest;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.SlackUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Recipient;

@RequestMapping("/banking")
@Controller
public class BankingController {
	
    @Value("${stripe.com.secret.key}")
    private String secretKey;
	
    /**
     * @api {post} /banking/add Add Bank
     * @apiName Add Bank
     * @apiDescription Create a recipient in the bibs system restfully with a JSON object using the supplied details. The user must be logged in.
     * @apiGroup banking
     * @apiParam {Number} userGroupId Id of user group requesting bank account addition
     * @apiParam {String} stripeToken Token Reponse from stripe
     * @apiParam {String} holderName name of account holder
     * @apiParam {String=individual,corporation} type type of account holder
     * @apiParam {String} customName account description for user
     * @apiSuccess (200) {String} status Created - Account Associated Successfully
     * @apiError (404) {String=MissingType, MissingHolder, MissingToken, MissingName, MissingOrg, BadAccountType, OrgNotFound, BadRequestGeneric} error Account not created successfully
     */
	@RequestMapping(value ="/add", method = RequestMethod.POST) 
	public ResponseEntity<String> addBank (@RequestBody BankAddRequest bankAddRequest){
		if(bankAddRequest.getType() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingType", HttpStatus.BAD_REQUEST);
		}
		if(bankAddRequest.getHolderName() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingHolder", HttpStatus.BAD_REQUEST);
		}
		if(bankAddRequest.getStripeToken() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingToken", HttpStatus.BAD_REQUEST);
		}
		if(bankAddRequest.getCustomName() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingName", HttpStatus.BAD_REQUEST);
		}
		if(bankAddRequest.getUserGroupId() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingOrg", HttpStatus.BAD_REQUEST);
		}
		if(!(bankAddRequest.getType().contentEquals("individual")|| bankAddRequest.getType().contentEquals("corporation"))) {
			return SpringJSONUtil.returnErrorMessage("BadAccountType", HttpStatus.BAD_REQUEST);
		}
		UserProfile user = UserProfileUtil.getLoggedInUserProfile();
		UserGroup group = null;
		try {
			group = UserGroup.findUserGroup(bankAddRequest.getUserGroupId());
		} catch(Exception e) {
			SpringJSONUtil.returnErrorMessage("OrgNotFound", HttpStatus.BAD_REQUEST);
		}
		
		Stripe.apiKey = this.secretKey;
		
		Map<String,Object> recipientRequest = new HashMap <String, Object>();
		recipientRequest.put("name", bankAddRequest.getHolderName());
		recipientRequest.put("type", bankAddRequest.getType());
		recipientRequest.put("bank_account", bankAddRequest.getStripeToken());
		recipientRequest.put("email", user.getEmail());
		recipientRequest.put("description", bankAddRequest.getCustomName());
		try {
			Recipient created = Recipient.create(recipientRequest);
			PaymentAccount account = new PaymentAccount(user, group, created.getId());
			account.persist();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("BadRequestGeneric", HttpStatus.BAD_REQUEST);
		} catch (InvalidRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return SpringJSONUtil.returnErrorMessage("BadRequestGeneric", HttpStatus.BAD_REQUEST);
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("BadRequestGeneric", HttpStatus.BAD_REQUEST);
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("BadRequestGeneric", HttpStatus.BAD_REQUEST);
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return SpringJSONUtil.returnErrorMessage("BadRequestGeneric", HttpStatus.BAD_REQUEST);
		}

		
		return SpringJSONUtil.returnStatusMessage("Created", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/payouts/event/{id}")
	@ResponseBody
	public ResponseEntity<String> generateEventPayout(@PathVariable("id") Long eventId) {
		Event event = Event.findEvent(eventId);
		Payout payout = new Payout(event.getOrganizer());
		List<Cart> checkedOut = Cart.findCompletedCartsItemsByEventBeforeDate(event, new Date()).getResultList();
		List<Cart> refunded = Cart.findRefundedCartsItemsByEvent(event).getResultList();
		payout.persist();
		SlackUtil.logPayoutGenerate(event.getName());
		return new ResponseEntity<String>("Payout", HttpStatus.OK);
	}
		
		
		
    @RequestMapping(value = "/{id}/{uid}/{aid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long userGroupId, @PathVariable("uid") Long userProfileId, @PathVariable("aid") Long authorityId,
            @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        UserAuthority userAuthority = UserAuthority.findUserAuthority(authorityId);
        if (userAuthority != null && userProfile != null) {
            UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(new UserAuthoritiesID(userProfile, userAuthority));
            UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
            if (userAuthorities != null && userGroup != null) {
                UserGroupUserAuthority userGroupUserAuthority = UserGroupUserAuthority.findUserGroupUserAuthority(new UserGroupUserAuthorityID(userGroup, userAuthorities));
                if (userGroupUserAuthority != null) {
                    userGroupUserAuthority.remove();
                }
            }
        }
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/usergroupuserauthorities?usergroup=" + userGroupId;
    }
}
