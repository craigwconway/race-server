/**
 * 
 */
package com.bibsmobile.controller.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.PictureHashtag;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.model.dto.RaceImageViewDto;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.app.JWTUtil;

/**
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/photos")
public class AppPhotoController {

	/**
	 * @api {post} /app/photos/create Add Photo
	 * @apiName Add Photo
	 * @apiGroup app
	 * @apiDescription Upload a photo to the bibs app.
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 * @apiParamExample {lua} Corona
	 * function uploadPhotoListener(event)
	 * 	if (event.isError) then
	 * 		print ("cave out")
	 * 	else 
	 * 		print ( "cave in: " .. event.response )
	 * 		responseObj = json.decode(event.response)
	 * 		if(event.status == 200 and responseObj.status == "success") then
	 * 			print("Success")
	 * 		elseif(event.status == 400) then
	 * 			if (responseObj.error == "MissingFilepath") then
	 * 				print("URL not added")
	 * 			elseif (responseObj.error == "EventNotFound") then
	 * 				print("User is missing a name")
	 * 			elseif(event.status == 403) then
	 * 				print("Tell user to login")
	 * 			end
	 * 		end
	 * 	end
	 * end
	 * userCredentials.token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NDk0ODkyMjgzNTQsImlzcyI6ImJpYnMiLCJhdWQiOiI1IiwidXNlciI6eyJpZCI6NSwiZmlyc3ROYW1lIjoiR2FsZW4iLCJsYXN0TmFtZSI6IkRhbnppZ2VyIiwiZW1haWwiOiJnZWRhbnppZ2VyQGdtYWlsLmNvbSIsInBob25lIjpudWxsLCJ1c2VybmFtZSI6bnVsbCwicGFzc3dvcmQiOiJjMzQwMTFlYTA0ZjhlZGJjNTc0NjNiMjA5MjQ1YTI0ZDU1ZDgxZmU3ZWVmOTE2ZjI1ODNkNWU5MjdmNzlmZjlkYzlhZmU0MjRjNTAyMWU4MyJ9LCJpYXQiOjE0NTExOTIxOTU2NTB9.UhTdMFtI3VfmwUr6iYC3VIYeYWBNDZnud7Ly4DdkkDc"
	 * -- This function assumes a userCredentials object has been initialized with a valid token
	 * -- Upload Photo Function Definition
	 * -- url -- String of url
	 * -- event -- Number id of event
	 * -- bibs -- Array of bib numbers
	 * -- hashtags -- Array of hashtag strings
	 * function UploadPhoto(url, event, bibs, hashtags)
	 * 	local headers = {}
	 * 	headers["Content-Type"] = "application/json"
	 * 	headers["X-FacePunch"] = userCredentials.token
	 * 	local params = {}
	 * 	local requestObj = {}
	 * 	requestObj.filePath = url;
	 * 	requestObj.event = {};
	 * 	requestObj.event.id = event;
	 * 	results = {};
	 * 	pictureHashtags = {};
	 * 	if bibs ~= nil then
	 * 		for entry = 1,#bibs do
	 * 			local newObject = {}
	 * 			newObject.bib = bibs[entry]
	 * 			table.insert(results, newObject)
	 * 		end
	 * 	end
	 * 	if hashtags ~= nil then
	 * 		for entry =1, #hashtags do
	 * 			local newObject = {}
	 * 			newObject.pictureHashtag = hashtags[entry]
	 * 			table.insert(pictureHashtags, newObject)
	 * 		end
	 * 	end
	 * 	requestObj.raceResults = results;
	 * 	requestObj.pictureHashtags = pictureHashtags;
	 * 	params.headers = headers
	 * 	params.body = json.encode(requestObj);
	 * 	print("body:" .. params.body)
	 * 	network.request( bibsAPI .. "/app/photos/create" , "POST", uploadPhotoListener, params )
	 * end
	 * UploadPhoto("http://canadalandshow.com/sites/default/files/field/image/drake-cover-990.jpeg", 1, {1,2,333} , {"facepunch", "shrug", "drake"});
	 * @apiSuccess (200) {String} status status of the photo upload
	 * @apiError (403) {String} UserNotAuthenticated The supplied authentication token is missing or invalid.
	 * @apiError (400) {String} MissingFilepath The user has uploaded a missing photo
	 * @apiError (400) {String} EventNotFound The image has a bad event supplied
	 * @apiSuccessExample {json} Photo Created
	 * HTTP/1.1 200 OK
	 * {
	 * 	"status":"Created"
	 * }
	 * @apiErrorExample {json} UserNotAuthenticated
	 * HTTP/1.1 403 Forbidden
	 * {
	 * 	"error": "UserNotAuthenticated"
	 * }
	 * @apiErrorExample {json} MissingFilepath
	 * HTTP/1.1 400 Bad Request
	 * {
	 * 	"error": "MissingFilepath"
	 * }
	 * @apiErrorExample {json} EventNotFound
	 * HTTP/1.1 400 Bad Request
	 * {
	 * 	"error": "EventNotFound"
	 * }
	 * @apiSampleRequest http://localhost:8080/bibs-server/app/photos/create
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	ResponseEntity<String> createPhoto(HttpServletRequest request, @RequestBody RaceImage raceImage) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		if(StringUtils.isEmpty(raceImage.getFilePath())) {
			return SpringJSONUtil.returnErrorMessage("MissingFilepath", HttpStatus.BAD_REQUEST);
		}
		Event event;
		try {
			event = Event.findEvent(raceImage.getEvent().getId());
		} catch (Exception e) {
			return SpringJSONUtil.returnErrorMessage("EventNotFound", HttpStatus.BAD_REQUEST);
		}
		RaceImage saveImage = new RaceImage();
		saveImage.setEvent(event);
		saveImage.setFilePath(raceImage.getFilePath());
		saveImage.setUserProfile(UserProfile.findUserProfile(user.getId()));
		Set<RaceResult> saveResults = new HashSet<RaceResult>();
		Set<PictureHashtag> saveHashtags = new HashSet<PictureHashtag>();
		for(RaceResult result : raceImage.getRaceResults()) {
			if(result.getBib() != 0) {
				List<RaceResult> trueResultList = RaceResult.findRaceResultsByEventAndBibEquals(event, result.getBib()).getResultList();
				for(RaceResult rr : trueResultList) {
					saveResults.add(rr);
				}
				if(trueResultList.isEmpty()) {
					RaceResult newResult = new RaceResult();
					newResult.setEvent(event);
					newResult.setBib(result.getBib());
					newResult.persist();
					newResult.flush();
					saveResults.add(RaceResult.findRaceResult(newResult.getId()));
				}
			} 
		}
		for(PictureHashtag hashtag : raceImage.getPictureTypes()) {
			if(!StringUtils.isEmpty(hashtag.getPictureHashtag())) {
				PictureHashtag saveHashtag = PictureHashtag.findPictureHashtagByString(hashtag.getPictureHashtag());
				if(saveHashtag != null) {
					saveHashtags.add(saveHashtag);
				} else {
					saveHashtag = new PictureHashtag();
					saveHashtag.setPictureHashtag(hashtag.getPictureHashtag());
					saveHashtag.persist();
					saveHashtag.flush();
					saveHashtags.add(PictureHashtag.findPictureHashtag(saveHashtag.getId()));
				}
			} 
		}
		saveImage.setRaceResults(saveResults);
		saveImage.setPictureHashtags(saveHashtags);
		saveImage.persist();
		return SpringJSONUtil.returnStatusMessage("Created", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/me", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<String> createPhoto(HttpServletRequest request) {
		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		UserProfile userProfile = UserProfile.findUserProfile(user.getId());
		List<RaceImage> images = RaceImage.findRaceImagesByUser(userProfile).getResultList();
		return new ResponseEntity<String>(RaceImageViewDto.fromRaceImagesToDtoArray(images), HttpStatus.OK);
	}
}
