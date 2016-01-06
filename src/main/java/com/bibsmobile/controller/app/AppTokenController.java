package com.bibsmobile.controller.app;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.QueryTimeoutException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
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
	
    /**
     * @api {post} /app/token/generate Login
     * @apiGroup app
     * @apiName Login
     * @apiDescription Pass up a User profile object for authentication. If successful, pass back a token in X-FacePunch.
     * @apiParam {String} email Email of user to authenticate
     * @apiParam {String} password Password of user to authenticate
     * @apiParamExample {json} Sample Json Login
     * 	{
     * 		"email":"gedanziger@gmail.com",
     * 		"password":"shrugsallday1234"
     * 	}
     * @apiErrorExample {json} Null User Submitted
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Error - null user submitted"
     * 	}
     * @apiErrorExample {json} Username or password is Null
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Missing email or password"
     * 	}
     * @apiSuccess (201) Successful Login
     * 	{
     * 		"time":123123123,
     * 		"status":"success",
     * 		"exp":1441607658000
     * 	}
     * @apiSampleRequest https://condor.bibs.io/bibs-server/app/token/register
     * @param response
     * @return
     */
	@RequestMapping("/generate")
	@ResponseBody
	ResponseEntity<String> login(@RequestBody UserProfile user, HttpServletResponse response) {
		if(user == null) {
			return SpringJSONUtil.returnErrorMessage("Error - null user submitted", HttpStatus.BAD_REQUEST);
		}
		if(user.getEmail() == null || user.getPassword() == null) {
			return SpringJSONUtil.returnErrorMessage("Missing email or password", HttpStatus.BAD_REQUEST);
		}
		UserProfile trueUser;
		try{
			trueUser = UserProfile.findEnabledUserProfilesByEmailEquals(user.getEmail()).setMaxResults(1).getResultList().get(0);
		} catch(Exception e) {
			return SpringJSONUtil.returnErrorMessage("NotFound", HttpStatus.NOT_FOUND);
		}
		 
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
		return SpringJSONUtil.returnErrorMessage("BadCredentials", HttpStatus.BAD_REQUEST);
	}

    /**
     * @api {post} /app/token/register Register
     * @apiGroup app
     * @apiName Register
     * @apiDescription Pass up a user Profile to register. Minimum required fields are Email, Password, Firstname and Lastname.
     * A user can also optionally pass extra information in a second step, or in updating the profile to enable more functionality
     * in the app. Upon a successful registration, a token authenticating the user is returned in X-FacePunch.
     * @apiParam {String} email Email of user to authenticate
     * @apiParam {String} password Password of user to authenticate
     * @apiParam {String} firstname First Name of user to register
     * @apiParam {String} lastname Last Name of user to register
     * @apiParam {String="M,F"} [gender] Gender of user
     * @apiParam {Date} [birthdate] User's birthdate in UTC time
     * @apiParam {String} [username] User's handle to appear in app
     * @apiSampleRequest https://condor.bibs.io/bibs-server/app/token/register
     * @apiParamExample {json} Minimal Json Registration
     * 	{
     * 		"email":"gedanziger@gmail.com",
     * 		"password":"shrugsallday1234",
     * 		"firstname":"Galen",
     * 		"lastname":"Danziger"
     * 	}
     * @apiParamExample {lua} Corona Minimal
     * -- Network call requirements
     * local json = require "json"
     * local bibsAPI = "http://localhost:8080/bibs-server"
     * -- Time Checker Requirements
     * local os = require "os"
     * -- Call Description
     * -- Use the user credentials object to pass further app requests
     * -- This is generated on login or register
     * -- userCredentials.token holds the auth token
     * -- userCredentials.granted holds the grant time
     * -- userCredentials.expires holds the expiration time
     * userCredentials = {}
     * -- After the expiration time, the token must be refreshed by the regenerate token call
     * -- See this sample check token refresh call that checks a credentials object:
     * function needsRefresh(userCredentials)
     * 	if(os.time('!*t') > userCredentials.expires) then
     * 		return true
     * 	else
     * 		return false
     * 	end
     * end
     * 
     * -- Listener function for registration
     * function registerListener(event)
     * 	if (event.isError) then
     * 		print ("cave out")
     * 	else 
     * 		print ( "cave in: " .. event.response )
     * 		responseObj = json.decode(event.response)
     * 		if(event.status == 201 and responseObj.status == "success") then
     * 			userCredentials.token = event.responseHeaders["X-FacePunch"];
     * 			userCredentials.granted = responseObj.time;
     * 			userCredentials.expires = responseObj.expires;
     * 			print("created a user at: " .. userCredentials.granted .. ".")
     * 			print("App needs to refresh credentials after " .. userCredentials.expires)
     * 			print("Token: " .. userCredentials.token)
     * 		elseif(event.status == 400) then
     * 			if (responseObj.error == "Duplicate") then
     * 				print("You are trying to use credentials that already exist. Inform user.")
     * 			elseif (responseObj.error == "NullUser") then
     * 				print("You fucked up")
     * 			elseif (responseObj.error == "MissingName") then
     * 				print("User is missing a name")
     * 			elseif (responseObj.error == "MissingCredentials") then
     * 				print("User is missing a username or password")
     * 			end
     * 		end
     * 	end
     * end
     * function RegisterUserMinimal(email, password, firstname, lastname)
     * 	local headers = {}
     * 	headers["Content-Type"] = "application/json"
     * 	local params = {}
     * 	local requestObj = {}
     * 	requestObj.email = email;
     * 	requestObj.password = password;
     * 	requestObj.firstname = firstname;
     * 	requestObj.lastname = lastname;
     * 	params.headers = headers
     * 	params.body = json.encode(requestObj);
     * 	network.request( bibsAPI .. "/app/token/register" , "POST", registerListener, params )
     * end
     * RegisterUserMinimal("gedanziger@gmail.com", "punchpatrick", "Shrug", "Bot");
     * @apiParamExample {lua} Corona With Gender
     * -- Network call requirements
     * local json = require "json"
     * local bibsAPI = "http://localhost:8080/bibs-server"
     * -- Time Checker Requirements
     * local os = require "os"
     * -- Call Description
     * -- Use the user credentials object to pass further app requests
     * -- This is generated on login or register
     * -- userCredentials.token holds the auth token
     * -- userCredentials.granted holds the grant time
     * -- userCredentials.expires holds the expiration time
     * userCredentials = {}
     * -- After the expiration time, the token must be refreshed by the regenerate token call
     * -- See this sample check token refresh call that checks a credentials object:
     * function needsRefresh(userCredentials)
     * 	if(os.time('!*t') > userCredentials.expires) then
     * 		return true
     * 	else
     * 		return false
     * 	end
     * end
     * 
     * -- Listener function for registration
     * function registerListener(event)
     * 	if (event.isError) then
     * 		print ("cave out")
     * 	else 
     * 		print ( "cave in: " .. event.response )
     * 		responseObj = json.decode(event.response)
     * 		if(event.status == 201 and responseObj.status == "success") then
     * 			userCredentials.token = event.responseHeaders["X-FacePunch"];
     * 			userCredentials.granted = responseObj.time;
     * 			userCredentials.expires = responseObj.expires;
     * 			print("created a user at: " .. userCredentials.granted .. ".")
     * 			print("App needs to refresh credentials after " .. userCredentials.expires)
     * 			print("Token: " .. userCredentials.token)
     * 		elseif(event.status == 400) then
     * 			if (responseObj.error == "Duplicate") then
     * 				print("You are trying to use credentials that already exist. Inform user.")
     * 			elseif (responseObj.error == "NullUser") then
     * 				print("You fucked up")
     * 			elseif (responseObj.error == "MissingName") then
     * 				print("User is missing a name")
     * 			elseif (responseObj.error == "MissingCredentials") then
     * 				print("User is missing a username or password")
     * 			end
     * 		end
     * 	end
     * end
     * function RegisterUserGender(email, password, firstname, lastname, gender)
     * 	local headers = {}
     * 	headers["Content-Type"] = "application/json"
     * 	local params = {}
     * 	local requestObj = {}
     * 	requestObj.email = email;
     * 	requestObj.password = password;
     * 	requestObj.firstname = firstname;
     * 	requestObj.lastname = lastname;
     * 	requestObj.gender = gender;
     * 	params.headers = headers
     * 	params.body = json.encode(requestObj);
     * 	network.request( bibsAPI .. "/app/token/register" , "POST", registerListener, params )
     * end
     * RegisterUserGender("gedanziger@gmail.com", "punchpatrick", "Shrug", "Bot", "M");
     * @apiErrorExample {json} Null User Submitted
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"NullUser"
     * 	}
     * @apiErrorExample {json} Email or password is Null
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"MissingCredentials"
     * 	}
     * @apiErrorExample {json} Firstname / Lastname missing
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"MissingName"
     * 	}
     * @apiErrorExample {json} Duplicate
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Duplicate"
     * 	}
     * @apiSuccess (201) Successful Registration
     * 	{
     * 		"time":123123123,
     * 		"status":"success",
     * 		"expires":1441607658000
     * 	}
     * @param response
     * @return
     */	
	@RequestMapping("/register")
	@ResponseBody
	ResponseEntity<String> register(@RequestBody UserProfile user, HttpServletResponse response) {
		if(user == null) {
			return SpringJSONUtil.returnErrorMessage("NullUser", HttpStatus.BAD_REQUEST);
		}
		if(user.getEmail() == null || user.getPassword() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingCredentials", HttpStatus.BAD_REQUEST);
		}
		if(user.getFirstname() == null || user.getLastname() == null) {
			return SpringJSONUtil.returnErrorMessage("MissingName", HttpStatus.BAD_REQUEST);
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
	
    /**
     * @api {post} /app/token/register/series/:id Register (Series)
     * @apiGroup app
     * @apiName Register (Series)
     * @apiDescription Pass up a user Profile to register. Minimum required fields are Email, Password, Firstname and Lastname.
     * A user can also optionally pass extra information in a second step, or in updating the profile to enable more functionality
     * in the app. Upon a successful registration, a token authenticating the user is returned in X-FacePunch.
     * @apiParam {String} email Email of user to authenticate
     * @apiParam {String} password Password of user to authenticate
     * @apiParam {String} firstname First Name of user to register
     * @apiParam {String} lastname Last Name of user to register
     * @apiParam {String="M,F"} [gender] Gender of user
     * @apiParam {Date} [birthdate] User's birthdate in UTC time
     * @apiParam {String} [username] User's handle to appear in app
     * @apiSampleRequest https://condor.bibs.io/bibs-server/app/token/register/series/:id
     * @apiParamExample {json} Minimal Json Registration
     * 	{
     * 		"email":"gedanziger@gmail.com",
     * 		"password":"shrugsallday1234",
     * 		"firstname":"Galen",
     * 		"lastname":"Danziger"
     * 	}
     * @apiErrorExample {json} Null User Submitted
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Error - null user submitted"
     * 	}
     * @apiErrorExample {json} Username or password is Null
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Missing email or password"
     * 	}
     * @apiErrorExample {json} Firstname / Lastname missing
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Missing name"
     * 	}
     * @apiErrorExample {json} Duplicate
     * HTTP 1.1 400 Bad Request
     * 	{
     * 		"error":"Duplicate"
     * 	}
     * @apiSuccess (201) Successful Registration
     * 	{
     * 		"time":123123123,
     * 		"status":"success",
     * 		"exp":1441607658000
     * 	}
     * @param response
     * @return
     */	
	@RequestMapping("/register/series/{id}")
	@ResponseBody
	ResponseEntity<String> registerSeries(@RequestBody UserProfile user, 
			@PathVariable Long id,
			HttpServletResponse response) {
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
		UserGroup userGroup = UserGroup.findUserGroup(id);
		newUser.setEmail(user.getEmail());
		newUser.setFirstname(user.getFirstname());
		newUser.setLastname(user.getLastname());
		newUser.setPassword(encoder.encode(user.getPassword()));
		newUser.setEnabled(true);
		newUser.setAccountNonExpired(true);
		newUser.setAccountNonLocked(true);
		newUser.setCredentialsNonExpired(true);
		// Set Permission Level to USER
		UserAuthority roleUserAuthority = new UserAuthority();
        roleUserAuthority.setAuthority(UserAuthority.USER);
        roleUserAuthority.persist();
        // Create Authority set for User Profile
        UserAuthorities userAuthorities = new UserAuthorities();
        UserAuthoritiesID uid = new UserAuthoritiesID();
        uid.setUserAuthority(roleUserAuthority);
        uid.setUserProfile(newUser);
        userAuthorities.setId(uid);
        UserGroupUserAuthority ugua = new UserGroupUserAuthority();
        ugua.setUserGroup(userGroup);
        ugua.setUserAuthorities(userAuthorities);
        newUser.setUserAuthorities(new HashSet<UserAuthorities>());
        newUser.getUserAuthorities().add(userAuthorities);
        userAuthorities.persist();
        ugua.persist();
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


