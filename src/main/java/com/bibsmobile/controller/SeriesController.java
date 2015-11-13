/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Badge;

import java.util.List;
import java.util.ArrayList;

/**
 * Controls for creating badges to be given out in the series.
 * @author galen
 *
 */
@RequestMapping("/series")
@Controller
public class SeriesController {
	@RequestMapping(value = "/form", produces = "text/html")
	public String createForm(Model uiModel) {
		List <Badge> eventBadges = new ArrayList<Badge>();
		Event event = Event.findEvent(new Long(1));
		uiModel.addAttribute("event", event);
		System.out.println("Found event: " + event);
		uiModel.addAttribute("badges", eventBadges);
		return "series/create";
	}
}
