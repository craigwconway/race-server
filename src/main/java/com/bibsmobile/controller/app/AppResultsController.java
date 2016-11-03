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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.bibsmobile.util.app.JWTUtil;
import com.bibsmobile.util.SlackUtil;
import com.google.gson.JsonObject;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/results")
public class AppResultsController {

	private static final Logger log = LoggerFactory.getLogger(AppResultsController.class);

	/**
	 * @api {put} /app/results/claim/:id Claim Result
	 * @apiName Claim Result
	 * @apiGroup app
	 * @apiPermission User
	 * @apiParam {Number} id Path Variable containing id of result to claim
	 * @apiDescription Allow a user to claim a Race Result by id. Each user may only claim one result per event. If they
	 * attempt to claim an additional result, all other results in the event owned by them are released, then the new one is claimed.
	 * If the result has previously been claimed by another user, a request is sent to an administrator for approval.
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 * @apiSuccess (200) {String} ResultClaimed Result with <code>id</code> successfully matched with your user account.
	 * @apiSuccess (200) {String} ResultReplaced Result with <code>id</code> now replaces user's other result for this event.
	 * @apiSuccess (202) {String} ResultClaimSubmitted A claim to associate the result with <code>id</code> has been submitted.
	 * @apiError (404) {String} ResultNotFound A result with <code>id</code> does not exist or does is incomplete.
	 * @apiError (403) {String} UserNotAuthenticated The supplied authentication token is missing or invalid.
	 * @apiSuccessExample {json} ResultClaimed
	 * HTTP/1.1 200 OK
	 * {
	 * 	"status": "ResultClaimed"
	 * }
	 * @apiSuccessExample {json} ResultClaimSubmitted
	 * HTTP/1.1 202 Accepted
	 * {
	 * 	"status": "ResultClaimSubmitted"
	 * }
	 * @apiErrorExample {json} ResultNotFound
	 * HTTP/1.1 404 Not Found
	 * {
	 * 	"error": "ResultNotFound"
	 * }
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiParamExample {lua} CoronaSDK
	 * bibsAPI = "https://overmind.bibs.io/bibs-server"
	 * function claimResultListener(event)
	 * 	if (event.isError) then
	 * 		print ("cave out")
	 * 	else
	 * 		print ( "cave in: " .. event.response )
	 * 	end
	 * end
	 * function ClaimResult(id)
	 * 	local headers = {}
	 * 	headers["Content-Type"] = "application/json"
	 * 	headers["X-FacePunch"] = "YOUR.TOKEN.HERE"
	 * 	local params = {}
	 * 	params.headers = headers
	 * 	network.request( bibsAPI .. "/app/results/claim/" .. id , "PUT", claimResultListener, params )
	 * end
	 * ClaimResult(2)
	 */
	@RequestMapping(value = "/claim/{id}", method = RequestMethod.PUT)
	@ResponseBody
	ResponseEntity<String> claim(HttpServletRequest request, @PathVariable("id") Long id) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		RaceResult result = RaceResult.findRaceResult(id);
		if(result == null || result.getEvent() == null) {
			return SpringJSONUtil.returnErrorMessage("ResultNotFound", HttpStatus.NOT_FOUND);
		}
		if(result.getUserProfile() != null) {
			log.info("User " + user.getId() + " - " + user.getFirstname() + " " + user.getLastname() + " has requested result id " + result.getId());
			Event event = result.getEvent();
			SlackUtil.logResultContested( event.getName(),  result.getId(),  result.getEmail());
			return SpringJSONUtil.returnStatusMessage("ResultClaimSubmitted", HttpStatus.ACCEPTED);
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
		return SpringJSONUtil.returnStatusMessage("ResultClaimed", HttpStatus.OK);
	}
}
