package com.bibsmobile.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.BuildTypeUtil;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.UserProfileUtil;

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
		// Make our list of events;
		List<Event> events;
		// Check what type of user we have.
		UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
		if(PermissionsUtil.isSysAdmin(loggedInUser)) {
			// Case: Sysadmin: the user is using this on raceday.
			events = Event.findEventEntries(0, 3, "id", "DESC");			
		} else if(PermissionsUtil.isVaguelyEventAdmin(loggedInUser)) {
			System.out.println("evenadmin detected");
			// Case: Eventadmin: the user is using this on the bibs website.
			events = Event.findNonHiddenEventsForUser(loggedInUser, 0, 3, "id", "DESC");
			System.out.println(events);
			if(null == events) {
				uiModel.addAttribute("events", null);
				uiModel.addAttribute("build", BuildTypeUtil.getBuild());
				return "index";
			} else {
				uiModel.addAttribute("events", events);
				uiModel.addAttribute("build", BuildTypeUtil.getBuild());
				return "index";
			
			}
			// Now we need to filter events by date
			/*
			events.toArray();
			Collections.sort(events, new Comparator<Event>() {
				public int compare(Event e1, Event e2) {
				    if (e1.getTimeStart() == null || e2.getTimeStart() == null) 
				    	  return 0;
					return e1.getTimeStart().compareTo(e2.getTimeStart());
				}
			});
			// Always show one old, two new, or something like that
			Date current = new Date();
			Event oldEvent = null;
			Event currentEvent = null;
			while(events.iterator().hasNext()) {
				oldEvent = currentEvent;
				currentEvent = events.iterator().next();
				if(currentEvent.getTimeStart().after(current)) {
					// maybe add a future event:
					if(events.iterator().hasNext()) {
						Event nextEvent = events.iterator().next();
						if(null != oldEvent) {
							uiModel.addAttribute("events", new ArrayList<Event>(Arrays.asList(oldEvent, currentEvent, nextEvent)));
						} else {
							uiModel.addAttribute("events", new ArrayList<Event>(Arrays.asList(currentEvent, nextEvent)));
						}
					} else {
						if(null != oldEvent) {
							uiModel.addAttribute("events", new ArrayList<Event>(Arrays.asList(oldEvent, currentEvent)));
						} else {
							uiModel.addAttribute("events", new ArrayList<Event>(Arrays.asList(currentEvent)));
						}
					}
					uiModel.addAttribute("build", BuildTypeUtil.getBuild());
					return "index";
				}
			}
			if(events.size() < 4) {
				uiModel.addAttribute("events", events);
				return "index";
			} else {
			    List<Event> tmpEvents = new ArrayList<Event>();
			    tmpEvents.add(events.get(events.size()-1)); // The last
			    tmpEvents.add(events.get(events.size()-2)); // The one before the last
			    tmpEvents.add(events.get(events.size()-3)); // The one before the one before the last
			    uiModel.addAttribute("events", tmpEvents);
			    uiModel.addAttribute("build", BuildTypeUtil.getBuild());
			    return "index";
			}
			*/
		} else {
			events = null;
		}
		// now we want to filter by event
		uiModel.addAttribute("events", events);
		uiModel.addAttribute("build", BuildTypeUtil.getBuild());
	    return "index";
	}
}
