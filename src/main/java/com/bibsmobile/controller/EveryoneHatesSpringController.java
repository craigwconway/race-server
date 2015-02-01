package com.bibsmobile.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bibsmobile.model.Event;

/**
 * this module is the love child of comic sans and the dmv
 * @author galen
 *
 */
@RequestMapping("/")
@Controller
public class EveryoneHatesSpringController {
	@RequestMapping(produces="text/html")
	public String index(Model uiModel) {
		List<Event> events = Event.findAllEvents();
		uiModel.addAttribute("events", events);
	    return "index";
	}
}
