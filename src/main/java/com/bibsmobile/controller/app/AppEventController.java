/**
 * 
 */
package com.bibsmobile.controller.app;

import java.util.List;
import java.util.Set;

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
import com.google.gson.JsonObject;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/events")
public class AppEventController {

	private static final Logger log = LoggerFactory.getLogger(AppEventController.class);
	
	/**
	 * @api {put} /app/results/follow/:id Follow Event
	 * @apiName Follow Event
	 * @apiGroup app
	 * @apiPermission User
	 * @apiDescription Allow a user to follow an event by id
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
	 * 	"status": "EventFollowed"
	 * }
	 * @apiSuccessExample {json} EventFollowed
	 * HTTP/1.1 202 Accepted
	 * {
	 * 	"status": "EventAlreadyFollowed"
	 * }
	 * @apiErrorExample {json} ResultNotFound
	 * HTTP/1.1 404 Not Found
	 * {
	 * 	"error": "EventNotFound"
	 * }
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiParamExample {lua} CoronaSDK
	 * bibsAPI = "https://overmind.bibs.io/bibs-server"
	 * function followEventListener(event)
	 * 	if (event.isError) then
	 * 		print ("cave out")
	 * 	else 
	 * 		print ( "cave in: " .. event.response )
	 * 	end
	 * end
	 * function followEvent(id)
	 * 	local headers = {}
	 * 	headers["Content-Type"] = "application/json"
	 * 	headers["X-FacePunch"] = "YOUR.TOKEN.HERE"
	 * 	local params = {}
	 * 	params.headers = headers
	 * 	network.request( bibsAPI .. "/app/event/follow/" .. id , "PUT", claimResultListener, params )
	 * end
	 * followEvent(2)
	 */
	@RequestMapping(value = "/follow/{id}", method = RequestMethod.PUT)
	@ResponseBody
	ResponseEntity<String> claim(HttpServletRequest request, @PathVariable("id") Long id) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		Event event = Event.findEvent(id);
		if(event == null) {
			return SpringJSONUtil.returnErrorMessage("EventNotFound", HttpStatus.NOT_FOUND);
		}
		if(user.getEvents().contains(event)) {
			log.warn("User " + user.getId() + " - " + user.getFirstname() + " " + user.getLastname() + " refollowed " + event.getId());
			return SpringJSONUtil.returnStatusMessage("EventAlreadyFollowed", HttpStatus.ACCEPTED);
		}
		user = UserProfile.findUserProfile(user.getId());
		// First release claim on all other results in this event:
		Set <Event> followedEvents = user.getEvents();
		followedEvents.add(event);
		user.setEvents(followedEvents);
		user.merge();
		return SpringJSONUtil.returnStatusMessage("EventFollowed", HttpStatus.OK);
	}
}
