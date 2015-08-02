package com.bibsmobile.controller.app;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.JSONUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.app.JWTUtil;
import com.google.gson.JsonObject;

/**
 * Token generation for app calls
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/token")
public class AppTokenController {

    @Autowired
    private StandardPasswordEncoder encoder;
	
	@RequestMapping("/generate")
	@ResponseBody
	ResponseEntity<String> login(@RequestBody UserProfile user, HttpServletResponse response) {
		if(user == null) {
			return SpringJSONUtil.returnErrorMessage("Error - null user submitted", HttpStatus.BAD_REQUEST);
		}
		if(user.getEmail() == null || user.getPassword() == null) {
			return SpringJSONUtil.returnErrorMessage("Missing email or password", HttpStatus.BAD_REQUEST);
		}
		UserProfile trueUser = UserProfile.findEnabledUserProfilesByEmailEquals(user.getEmail()).getSingleResult();
		System.out.println(trueUser.getEmail());
		System.out.println(encoder.matches(user.getPassword(), trueUser.getPassword()));
		JsonObject object = new JsonObject();
		String token = null;
		if(encoder.matches(user.getPassword(), trueUser.getPassword())) {
			token = JWTUtil.generate(trueUser);
			System.out.println("Sucessful match");
		}

		if(token != null && !token.isEmpty()) {
			object.addProperty("time", new Date().getTime());
			object.addProperty("status", "success");
			object.addProperty("expires", new Date().getTime() + 1000 * 60 * 60 * 24 * 30);
			response.setHeader("X-FacePunch", token);
			return new ResponseEntity<String>(object.toString(), HttpStatus.CREATED);
		}
		return SpringJSONUtil.returnErrorMessage("error", HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping("/register")
	@ResponseBody
	ResponseEntity<String> register(@RequestBody UserProfile user, HttpServletResponse response) {
		if(user == null) {
			return SpringJSONUtil.returnErrorMessage("Error - null user submitted", HttpStatus.BAD_REQUEST);
		}
		if(user.getEmail() == null || user.getPassword() == null) {
			return SpringJSONUtil.returnErrorMessage("Missing email or password", HttpStatus.BAD_REQUEST);
		}
		if(user.getFirstname() == null || user.getLastname() == null) {
			return SpringJSONUtil.returnErrorMessage("Missing name", HttpStatus.BAD_REQUEST);
		}
		if(UserProfile.countFindEnabledUserProfilesByEmailEquals(user.getEmail()) > 0) {
			return SpringJSONUtil.returnErrorMessage("Duplicate", HttpStatus.BAD_REQUEST);
		}
		UserProfile newUser = new UserProfile();
		newUser.setEmail(user.getEmail());
		newUser.setFirstname(user.getFirstname());
		newUser.setLastname(user.getLastname());
		newUser.setPassword(encoder.encode(user.getPassword()));
		newUser.setEnabled(true);
		newUser.setAccountNonExpired(true);
		newUser.setAccountNonLocked(true);
		newUser.setCredentialsNonExpired(true);
		newUser.persist();
		JsonObject object = new JsonObject();
		String token = JWTUtil.generate(newUser);

		if(token != null && !token.isEmpty()) {
			object.addProperty("time", new Date().getTime());
			object.addProperty("status", "success");
			object.addProperty("expires", new Date().getTime() + 1000 * 60 * 60 * 24 * 30);
			response.setHeader("X-FacePunch", token);
			return new ResponseEntity<String>(object.toString(), HttpStatus.CREATED);
		}
		return SpringJSONUtil.returnErrorMessage("error", HttpStatus.BAD_REQUEST);
	}
}


