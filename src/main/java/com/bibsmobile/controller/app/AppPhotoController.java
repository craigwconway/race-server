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
	 * 	"error": "EventNotFOund"
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
		saveImage.setUserProfile(user);
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
					saveResults.add(newResult);
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
					saveHashtags.add(saveHashtag);
				}
			} 
		}
		saveImage.setRaceResults(saveResults);
		saveImage.setPictureHashtags(saveHashtags);
		saveImage.persist();
		return SpringJSONUtil.returnStatusMessage("Created", HttpStatus.OK);
	}
}
