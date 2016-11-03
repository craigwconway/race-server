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
import com.bibsmobile.model.Series;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserProfile;
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
@RequestMapping("/app/prizes")
public class AppPrizeController {

	private static final Logger log = LoggerFactory.getLogger(AppPrizeController.class);

	/**
	 * @api {get} /app/prizes/spin Spin Wheel
	 * @apiName Spin Wheel
	 * @apiGroup app
	 * @apiPermission User
	 * @apiDescription Spin the wheel to see if you win a prize
	 * @apiHeader X-FacePunch Access token for user account
	 * @apiHeader Content-Type Application/json
	 * @apiHeaderExample {json} JSON Headers
	 * {
	 * 	"Content-Type": "Application/json",
	 * 	"X-FacePunch": "YOUR.TOKEN.HERE"
	 *  }
	 */
	@RequestMapping(value = "/spin/{id}", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<String> claim(HttpServletRequest request, @PathVariable("id") Long seriesId) {

		UserProfile user =  JWTUtil.authenticate(request.getHeader("X-FacePunch"));
		if( user == null) {
			return SpringJSONUtil.returnErrorMessage("UserNotAuthenticated", HttpStatus.FORBIDDEN);
		}
		user = UserProfile.findUserProfile(user.getId());

		//Test scheme

		Series series = Series.findSeries(seriesId);
		if(Math.random() * 4 > 3) {
			SlackUtil.logPrizeWin(series.getName(), user.getEmail());
			return SpringJSONUtil.returnErrorMessage("Win", HttpStatus.OK);
		}
		return SpringJSONUtil.returnStatusMessage("Loss", HttpStatus.OK);
	}
}
