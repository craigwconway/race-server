/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controls for creating badges to be given out in the series.
 * @author galen
 *
 */
@RequestMapping("/badges")
@Controller
public class BadgeController {
	@RequestMapping(value = "/form", produces = "text/html")
	public String createForm(Model uiModel) {
	return "badges/create";
	}
}
