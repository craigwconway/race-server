/**
 * 
 */
package com.bibsmobile.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.bibsmobile.model.Badge;
import com.bibsmobile.model.BadgeTriggerEnum;
import com.bibsmobile.model.Event;
import com.bibsmobile.util.SpringJSONUtil;

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
		Event event = Event.findEvent(new Long(1));
		List <Badge> eventBadges = Badge.findBadgesByEvent(event);
		uiModel.addAttribute("event", event);
		System.out.println("Found event: " + event);
		uiModel.addAttribute("badges", eventBadges);
		
		//Hack cause I can't do frontend (gedanziger)
		//TODO: clean this up
		ArrayList<String> wellKnownCreatedBadges = new ArrayList<String>();
		for(Badge badge : eventBadges) {
			if(badge.getBadgeTrigger() == BadgeTriggerEnum.PHOTO_UPLOAD) {
				if(badge.getTriggerQuantity() == 1) {
					wellKnownCreatedBadges.add("one-photo");
				} else if(badge.getTriggerQuantity() == 3) {
					wellKnownCreatedBadges.add("three-photo");
				} else if(badge.getTriggerQuantity() == 10) {
					wellKnownCreatedBadges.add("ten-photo");
				}
			} else if( badge.getBadgeTrigger() == BadgeTriggerEnum.CLAIM_RESULT) {
				wellKnownCreatedBadges.add("result-claim");
			} else if (badge.getBadgeTrigger() == BadgeTriggerEnum.SOCIAL_SHARE) {
				wellKnownCreatedBadges.add("social-share");
			}
		}
		uiModel.addAttribute("commonBadges", wellKnownCreatedBadges);
		return "badges/create";
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<String> postBadge(@RequestBody Badge badge)  {
		System.out.println("Badge Type: " + badge.getBadgeTrigger());
		System.out.println("Badge Event: " + badge.getEvent());
		System.out.println("Badge Series: " + badge.getSeries());
		if(badge.getEvent() == null && badge.getSeries() == null) {
			return SpringJSONUtil.returnErrorMessage("Please specify a valid Event or Series", HttpStatus.BAD_REQUEST);
		}
		if(badge.getEvent() != null) {
			Event event = Event.findEvent(badge.getEvent().getId());
			if(event == null) {
				return SpringJSONUtil.returnErrorMessage("Please Specify a valid Event", HttpStatus.UNAUTHORIZED);
			}
			//TODO: Auth check here
			badge.setEvent(event);
			badge.persist();
		}
		return SpringJSONUtil.returnStatusMessage("Created", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/eventinsights", method = RequestMethod.GET)
	public String eventInsights(Model uiModel, @RequestParam Long eventid) {
		Event event = Event.findEvent(eventid);
		uiModel.addAttribute("event", event);
		uiModel.addAttribute("totalBadges", 120);
		uiModel.addAttribute("photo", 60);
		uiModel.addAttribute("claim", 40);
		uiModel.addAttribute("social", 20);
		
		uiModel.addAttribute("availableBadges", Badge.findBadgesByEvent(event).size());
		
		return "badges/eventinsights";
	}
}
