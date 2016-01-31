/**
 * 
 */
package com.bibsmobile.controller.app;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
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
import com.bibsmobile.model.UserBadge;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.UserDetailsShortDto;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.bibsmobile.util.app.JWTUtil;
import com.google.gson.JsonObject;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/user")
public class AppUserController {

	private static final Logger log = LoggerFactory.getLogger(AppResultsController.class);
	
	/**
	 * @api {get} /app/user/me/short Short User Details
	 * @apiName Me
	 * @apiGroup app
	 * @apiPermission User
	 * @apiDescription Get short description of logged-in user.
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 * @apiSuccess (200) {Object} user UserProfile object returned for logged in user
	 * @apiError (403) {String} UserNotAuthenticated The supplied authentication token is missing or invalid.
	 * @apiUse userDetailsShortDto
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiSampleRequest https://condor.bibs.io/bibs-server/app/user/me/short
	 */
	@RequestMapping(value = "/me/short", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<String> shortDescription(HttpServletRequest request) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		UserProfile trueUser = UserProfile.findUserProfile(user.getId());
		System.out.println("User " + trueUser + " Requested data");
		
		return new ResponseEntity<String>(UserDetailsShortDto.fromUserProfileToDto(trueUser), HttpStatus.OK);
	}

	/**
	 * @api {get} /app/user/me/friends Get Friends
	 * @apiName Get Friends
	 * @apiGroup app
	 * @apiPermission User
	 * @apiDescription Get all friends for a the current user
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 * @apiSuccess (200) {Object[]} friends UserProfile objects returned for each of logged in user's friends
	 * @apiSuccess (200) {String} friends.firstname firstname of user
	 * @apiSuccess (200) {String} friends.lastname lastname of user
	 * @apiSuccess (200) {String} friends.gender gender of user
	 * @apiSuccess (200) {String} friends.username username of user
	 * @apiError (403) {String} UserNotAuthenticated The supplied authentication token is missing or invalid.
	 * @apiSuccessExample {json} Success
	 * HTTP/1.1 200 OK
	 * [
	 * 	{
	 * 		"firstname": "Galen",
	 * 		"lastname": "Danziger",
	 * 		"gender": "M",
	 * 		"username": "galen"
	 * 	},
	 * 	{
	 * 		"firstname": "Patrick",
	 * 		"lastname": "McLain",
	 * 		"gender": "M",
	 * 		"username": "shouldershrug"
	 * 	}
	 * ]
	 * 	
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiSampleRequest https://condor.bibs.io/bibs-server/app/user/me/friends
	 */
	@RequestMapping(value = "/me/friends", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<String> userFriends(HttpServletRequest request) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		user = UserProfile.findUserProfile(user.getId());
		Hibernate.initialize(user.getFriends());
		
		return new ResponseEntity<String>(UserProfile.toJsonArray(user.getFriends()), HttpStatus.OK);
	}

	/**
	 * @api {get} /app/user/me/badges Get Badges
	 * @apiName Get Badges
	 * @apiGroup app
	 * @apiPermission User
	 * @apiDescription Get all badges for a the current user
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 * @apiSuccess (200) {Object[]} badges array of badges returned for the current user
	 * @apiSuccess (200) {String} badges.created Timestamp of badge creation
	 * @apiSuccess (200) {Boolean} badges.active switch to indicate whether the badge has been granted or is invalidated
	 * @apiSuccess (200) {Number} badges.id Id of earned badge copy
	 * @apiSuccess (200) {Object} badges.badge True badge on server
	 * @apiSuccess (200) {Number} badges.badge.id Numeric id of badge
	 * @apiSuccess (200) {String} badges.badge.name Name of assigned badge
	 * @apiSuccess (200) {String} badges.badge.url URL containing thumbnail image for badge
	 * @apiError (403) {String} UserNotAuthenticated The supplied authentication token is missing or invalid.
	 * @apiSuccessExample {json} Success
	 * HTTP/1.1 200 OK
	 * [
	 * 	{
	 * 		"active": true,
	 * 		"badge": {
	 * 			"active":true,
	 * 			"badgeTrigger":"INSTAGRAM_WEBHOOK",
	 * 			"id":2,
	 * 			"name":"5 Face Punches",
	 * 			"timeEnd":null,
	 * 			"timeStart":null,
	 * 			"url":"http://dgbc7tshfzi70.cloudfront.net/smedia/minus/images/emojis/facepunch.png"
	 * 		},
	 * 		"created": 1441673906000,
	 * 		"id": 2,
	 * 	},
	 * 	{
	 * 		"active": true,
	 * 		"badge": {
	 * 			"active":true,
	 * 			"badgeTrigger":"ADD_FRIEND",
	 * 			"id":1,
	 * 			"name":"Most Face Punches Recieved",
	 * 			"timeEnd":null,
	 * 			"timeStart":null,
	 * 			"url":"http://dgbc7tshfzi70.cloudfront.net/smedia/minus/images/emojis/facepunch.png"
	 * 		},
	 * 		"created": 1441673985000,
	 * 		"id": 1,
	 * 	}
	 * ]
	 * 	
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiSampleRequest https://condor.bibs.io/bibs-server/app/user/me/badges
	 */
	@RequestMapping(value = "/me/badges", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<String> userBadges(HttpServletRequest request) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		user = UserProfile.findUserProfile(user.getId());
		Hibernate.initialize(user.getBadges());
		return new ResponseEntity<String>(UserBadge.toJsonArray(user.getBadges()), HttpStatus.OK);
	}	
	
	/**
	 * @api {post} /app/user/friends/add/:id Add Friend
	 * @apiName Add Friend
	 * @apiGroup app
	 * @apiDescription Send/Confirm a friend request for the current user.
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 * @apiSuccess (200) {String} status Status of the friend Request
	 * @apiSuccess (202) {String} status Status of the friend Request
	 * @apiError (403) {String} UserNotAuthenticated The supplied authentication token is missing or invalid.
	 * @apiError (404) {String} UserNotFound The user has requested a friend that does not exist.
	 * @apiSuccessExample {json} Sucessfully Requested
	 * HTTP/1.1 200 OK
	 * {
	 * 	"status":"FriendRequested"
	 * }
	 * @apiSuccessExample {json} Successfully Added
	 * HTTP/1.1 202 ACCEPTED
	 * {
	 * 	"status":"FriendAdded"
	 * }
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiSampleRequest https://condor.bibs.io/bibs-server/app/user/friends/add/:id
	 */
	@RequestMapping(value = "/friends/add/{id}", method = RequestMethod.POST)
	@ResponseBody
	ResponseEntity<String> addFriend(HttpServletRequest request, @PathVariable("id") Long id) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		user = UserProfile.findUserProfile(user.getId());
		UserProfile friend;
		try {
		friend = UserProfile.findFriendRequestForUserById(user, id);
		user.getFriends().add(friend);
		user.getFriendRequests().remove(friend);
		user.merge();
		return SpringJSONUtil.returnStatusMessage("FriendAdded", HttpStatus.ACCEPTED);

		} catch (Exception e) {
			System.out.println("No friend found");
			friend = UserProfile.findUserProfile(id);
			if (friend != null) {
				friend.getFriendRequests().add(user);
				friend.merge();
				return SpringJSONUtil.returnStatusMessage("FriendRequested", HttpStatus.OK);
			}
			return SpringJSONUtil.returnErrorMessage("UserNotFound", HttpStatus.NOT_FOUND);
		}
		
		
		
	}
}