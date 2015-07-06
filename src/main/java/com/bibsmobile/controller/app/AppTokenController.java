package com.bibsmobile.controller.app;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Token generation for app calls
 * @author galen
 *
 */
@Controller
@RequestMapping("/app/token")
public class AppTokenController {
	@RequestMapping("/generate")
	@ResponseBody
	String test() {
		return "hello";
	}
}


