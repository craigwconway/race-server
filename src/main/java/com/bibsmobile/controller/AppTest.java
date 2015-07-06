package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/app/sample")
@Controller
public class AppTest {

	@RequestMapping("/test")
	@ResponseBody
	String test() {
		return "hello";
	}
}
