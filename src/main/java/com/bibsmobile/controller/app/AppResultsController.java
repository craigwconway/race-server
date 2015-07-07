/**
 * 
 */
package com.bibsmobile.controller.app;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.bibsmobile.util.app.JWTUtil;
import com.google.gson.JsonObject;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/results")
public class AppResultsController {

	private static final Logger log = LoggerFactory.getLogger(AppResultsController.class);
	
	@RequestMapping("/claim/{id}")
	@ResponseBody
	ResponseEntity<String> claim(HttpServletRequest request, @PathVariable("id") Long id) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("Authentication failed", HttpStatus.FORBIDDEN);
		}
		RaceResult result = RaceResult.findRaceResult(id);
		if(result == null) {
			return SpringJSONUtil.returnErrorMessage("Result " + id + " not found", HttpStatus.NOT_FOUND);
		}
		if(result.getUserProfile() != null) {
			log.info("User " + user.getId() + " - " + user.getFirstname() + " " + user.getLastname() + " has requested result id " + result.getId());
			return SpringJSONUtil.returnStatusMessage("Request for result submitted.", HttpStatus.ACCEPTED);
		}
		user = UserProfile.findUserProfile(user.getId());
		// First release claim on all other results in this event:
		List <RaceResult> results = RaceResult.findRaceResultsByEventAndUser(result.getEvent(), user);
		for(RaceResult rr : results) {
			rr.setUserProfile(null);
			rr.merge();
		}
		result.setUserProfile(user);
		result.merge();
		return new ResponseEntity<String>(result.toJson(), HttpStatus.OK);
	}
}
