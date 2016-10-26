/**
 * 
 */
package com.bibsmobile.controller.app;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Series;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.MailgunUtil;
import com.bibsmobile.util.SlackUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.bibsmobile.util.app.JWTUtil;
import com.google.gson.JsonObject;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/password")
public class AppPasswordController {

	private static final Logger log = LoggerFactory.getLogger(AppPasswordController.class);
	
	private final static String baseUrl = "https://connect.bibs.io";
	private final static String controller = "/bibs-server/resetPassword?code=";
	
	
	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<String> claim(HttpServletRequest request, @RequestParam("email") String email) {
		/*
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		user = UserProfile.findUserProfile(user.getId());
		*/
		//Test scheme
		try{
			UserProfile user = UserProfile.findEnabledUserProfilesByEmailEquals(email).getSingleResult();
			user.setForgotPasswordCode(UUID.randomUUID().toString());
			user.merge();
			MailgunUtil.send(email, "Reset your race series password", "Click here to reset your password: " + baseUrl + controller + user.getForgotPasswordCode());
			return SpringJSONUtil.returnStatusMessage("ResetRequested", HttpStatus.OK);
		} catch (Exception e) {
			return SpringJSONUtil.returnErrorMessage("ResetError", HttpStatus.BAD_REQUEST);
		}
		
	}
}
