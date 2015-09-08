package com.bibsmobile.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.util.KinesisUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.app.JWTUtil;
import com.google.gson.JsonObject;

@RequestMapping("/app/sample")
@Controller
public class AppTest {

	@RequestMapping("/test")
	@ResponseBody
	ResponseEntity<String> test(HttpServletRequest request) {
		if(JWTUtil.authenticate(request.getHeader("X-FacePunch")) == null) {
			return SpringJSONUtil.returnErrorMessage("Authentication failed", HttpStatus.FORBIDDEN);
		}
		JsonObject json = new JsonObject();
		json.addProperty("test", "call");
		return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	}
	
	@RequestMapping("/kinesis")
	@ResponseBody
	ResponseEntity<String> kinesisTest(HttpServletRequest request) {
		KinesisUtil.test();
		return SpringJSONUtil.returnStatusMessage("thanks for letting us know.", HttpStatus.OK);
	}
}
