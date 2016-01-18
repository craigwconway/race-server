/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Badge;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Series;
import com.bibsmobile.model.SeriesRegion;
import com.bibsmobile.util.SpringJSONUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Controls for creating badges to be given out in the series.
 * @author galen
 *
 */
@RequestMapping("/register")
@Controller
public class RegisterController {
	@RequestMapping(value = "", produces = "text/html")
	public String startPage(Model uiModel) {
		return "register/first";
	}
	
	@RequestMapping(value="/step2", produces = "text/html")
	public String accountPage(Model uiModel) {
		return "register/second";
	}
	
	@RequestMapping(value="/step3", produces = "text/html")
	public String contactPage(Model uiModel) {
		return "register/third";
	}
	
	@RequestMapping(value="/step4", produces = "text/html")
	public String orgPage(Model uiModel) {
		return "register/fourth";
	}	
}
