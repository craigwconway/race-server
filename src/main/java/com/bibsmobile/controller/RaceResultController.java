package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.UserProfileUtil;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.EventUserGroup;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/raceresults")
@Controller
public class RaceResultController {

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        RaceResult raceResult = RaceResult.fromJsonToRaceResult(json);
        raceResult.persist();
        return raceResult.toJson();
    }

    @RequestMapping(value = "/addProfile/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String addProfile(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        String loggedinUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (loggedinUsername.equals("anonymousUser"))
            return "";
        UserProfile loggedinUserProfile = UserProfile.findUserProfilesByUsernameEquals(loggedinUsername).getResultList().get(0);
        raceResult.setUserProfile(loggedinUserProfile);
        raceResult.persist();
        return raceResult.toJson();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(value = "event", required = false, defaultValue = "0") Long event,
            @RequestParam(value = "name", required = false, defaultValue = "") String name, @RequestParam(value = "bib", required = false, defaultValue = "") Long bib) {
        String rtn = "[]";
        try {
            List<RaceResult> raceResults = RaceResult.search(event, name, bib);
            rtn = RaceResult.toJsonArray(raceResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/byevent/{eventName}", method = RequestMethod.GET)
    @ResponseBody
    public String byEvent(@PathVariable String eventName, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        String rtn = "";
        try {
            Event event = Event.findEventsByNameLike(eventName, page, size).getSingleResult();
            List<RaceResult> raceResults = Event.findRaceResults(event.getId(), page, size);
            rtn = RaceResult.toJsonArray(raceResults);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/bybib/{eventName}/{bib}", method = RequestMethod.GET)
    @ResponseBody
    public String byBib(@PathVariable String eventName, @PathVariable long bib) {
        String rtn = "";
        try {
            RaceResult raceResult = RaceResult.findRaceResultsByEventAndBibEquals(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), bib).getSingleResult();
            rtn = raceResult.toJson();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/byname/{eventName}/{firstName}/{lastName}", method = RequestMethod.GET)
    @ResponseBody
    public String byName(@PathVariable String eventName, @PathVariable String firstName, @PathVariable String lastName) {
        String rtn = "";
        try {
            if (firstName.equals("ANY"))
                rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndLastnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), lastName)
                        .getResultList());
            else if (lastName.equals("ANY"))
                rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), firstName)
                        .getResultList());
            else
                rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(),
                        firstName, lastName).getResultList());
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/bynamefeelinglucky/{eventName}/{firstName}/{lastName}", method = RequestMethod.GET)
    @ResponseBody
    public String byNameFeelingLucky(@PathVariable String eventName, @PathVariable String firstName, @PathVariable String lastName) {
        String rtn = "";
        try {
            rtn = RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), firstName, lastName)
                    .getSingleResult().toJson();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    
    @RequestMapping(value = "/myresults", produces = "text/html")
    public static String listmyevents(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size, 
			@RequestParam(value = "event", required = false, defaultValue = "0") Long event, 
			Model uiModel) {
    	Map<Long, Event> events = new HashMap<>();
    	UserProfile loggedInUser = UserProfileUtil.getLoggedInUserProfile();
    	if (null != loggedInUser) {
    		for(UserAuthorities ua : loggedInUser.getUserAuthorities()) {
    			for(UserGroupUserAuthority ugua : ua.getUserGroupUserAuthorities()) {
    				UserGroup ug = ugua.getUserGroup();
	    		        if (ug != null) {
	    		            for (EventUserGroup eventUserGroup : ug.getEventUserGroups()) {
	    		                Event e = eventUserGroup.getEvent();
	    		                if (!events.containsKey(e.getId())) {
	    		                    events.put(e.getId(), e);
	    		                }
	    		            }
	    		        }    				
    			}
    		}
    	} else {
    		return "redirect:/raceresults";
    	}
        uiModel.addAttribute("events", events.values());
        int sizeNo = size == null ? 10 : size.intValue();
        final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
        float nrOfPages = 0;
        if(event > 0){
        	uiModel.addAttribute("raceresults", Event.findRaceResults(event,firstResult, sizeNo));
            nrOfPages = (float) Event.countRaceResults(event) / sizeNo;
        }else{
        	uiModel.addAttribute("raceresults", Event.findRaceResults(events.keySet().iterator().next(), firstResult, sizeNo));
            nrOfPages = (float) RaceResult.countRaceResults() / sizeNo;
        }
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        //addDateTimeFormatPatterns(uiModel);
        return "raceresults/myresults";
    }    
    
    @RequestMapping(produces = "text/html")
    public static String list(
    						@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
    						@RequestParam(value = "size", required = false, defaultValue = "10") Integer size, 
    						@RequestParam(value = "event", required = false, defaultValue = "0") Long event, 
    						Model uiModel) {
        uiModel.addAttribute("events", Event.findAllEvents());
        int sizeNo = size == null ? 10 : size.intValue();
        final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
        float nrOfPages = 0;
        if (event > 0) {
            uiModel.addAttribute("raceresults", Event.findRaceResults(event, firstResult, sizeNo));
            nrOfPages = (float) Event.countRaceResults(event) / sizeNo;
        } else {
            uiModel.addAttribute("raceresults", RaceResult.findRaceResultEntries(firstResult, sizeNo));
            nrOfPages = (float) RaceResult.countRaceResults() / sizeNo;
        }
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        // addDateTimeFormatPatterns(uiModel);
        return "raceresults/list";
    }

    @RequestMapping(value = "/bibs", method = RequestMethod.GET, produces = "text/html")
    public static String bibs() {

        // license TODO

        return "raceresults/bibs";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid RaceResult raceResult, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, raceResult);
            return "raceresults/create";
        }
        uiModel.asMap().clear();
        raceResult.persist();
        long eventId = 0;
        if (null != raceResult.getEvent()) {
            eventId = raceResult.getEvent().getId();
        }
        return "redirect:/raceresults/?form&event=" + eventId + "&added="
                + this.encodeUrlPathSegment(raceResult.getBib() + " " + raceResult.getFirstname() + " " + raceResult.getLastname(), httpServletRequest);
    }

    @Autowired
    UserProfileService userProfileService;

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new RaceResult());
        return "raceresults/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
    	// sketchy bait-and-switch because that's what bibs does
        this.populateEditForm(uiModel, RaceResult.findRaceResult(id));
        return "raceresults/update";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid RaceResult raceResult, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, raceResult);
            return "raceresults/update";
        }
        uiModel.asMap().clear();
        // New race result stuff here //
        System.out.println("[RESULTS] Updating Chip Time");
        System.out.println("[RESULTS] Timechip: " + raceResult.getTimechip() + " Timestart:" + raceResult.getTimestart());
    	System.out.println("[RESULTS] Null get timechip, proceeding");
    	// We have a result without a chip time, we can compute the timeofficialdisplay
    	if(null != raceResult.getEvent().getGunTime() && 0 < raceResult.getTimeofficial()) {
    		System.out.println("[RESULTS] Event details:");
    		System.out.println(raceResult.getEvent().toJson());
    		System.out.println("[RESULTS] Calculating new timeofficialdisplay:");
    		System.out.println("[RESULTS] Event Gun Time Start: " + raceResult.getEvent().getGunTime().getTime() + " Time Official: " + raceResult.getTimeofficial());
    		// There is a gun time in the event and a timeofficial set
    		raceResult.setTimestart(raceResult.getEvent().getGunTime().getTime());
    		System.out.println("[RESULTS] Computed gun time: " + raceResult.getTimeofficialdisplay());
    	}
    	// Add logic to handle timeofficialdisplay updates if we have a nontrivial timeofficialdisplay value:
    	if("" != raceResult.valueOfTimeofficialdisplay()) {
    		DateFormat timeparser = new SimpleDateFormat("kk:mm:ss");
    		// Define new time difference as timeofficialdisplay
    		long newtimediff;
			try {
				
				Date newdatetimediff = timeparser.parse(raceResult.valueOfTimeofficialdisplay());
				newtimediff = newdatetimediff.getTime() - (long) (newdatetimediff.getTimezoneOffset()*60000);
	    		System.out.println("newtimediff: " + newtimediff);
	    		System.out.println("timezone offset: " + (long) (newdatetimediff.getTimezoneOffset()*60));

	    		// Define an old time difference quantity, compare to timeofficialdisplay
	    		long oldtimediff = raceResult.getTimeofficial()-raceResult.getTimestart();
	    		System.out.println("oldtimediff: " + oldtimediff);
	    		long difference = newtimediff - oldtimediff;
	    		System.out.println("adding: " + difference);
	    		// adjust timeofficial by the difference between timeofficialdisplay and timeofficial
	    		raceResult.setTimeofficial(raceResult.getTimeofficial() + newtimediff - oldtimediff);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		raceResult.merge();
    	return "redirect:/raceresults/" + encodeUrlPathSegment(raceResult.getId().toString(), httpServletRequest);

    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, RaceResult.findRaceResult(id));
        return "raceresults/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        raceResult.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/raceresults";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("raceResult_created_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("raceResult_updated_date_format", "MM/dd/yyyy h:mm:ss a");
    }

    void populateEditForm(Model uiModel, RaceResult raceResult) {
        uiModel.addAttribute("raceResult", raceResult);
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("events", Event.findAllEvents());
        uiModel.addAttribute("raceimages", RaceImage.findAllRaceImages());
        uiModel.addAttribute("userprofiles", this.userProfileService.findAllUserProfiles());
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (raceResult == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(raceResult.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
    	// TODO: Restrict this to RFID only builds. If we do this with the live results
    	// and someone leaves announcermode running, the server becomes dead.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<RaceResult> result = RaceResult.findAllRaceResults();
        return new ResponseEntity<>(RaceResult.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (RaceResult raceResult : RaceResult.fromJsonArrayToRaceResults(json)) {
            raceResult.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RaceResult raceResult = RaceResult.fromJsonToRaceResult(json);
        raceResult.setId(id);
        if (raceResult.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (raceResult == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        raceResult.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceResultsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEventAndBibEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceResultsByEventAndBibEquals(@RequestParam("event") Event event, @RequestParam("bib") long bib) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEventAndFirstnameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceResultsByEventAndFirstnameLike(@RequestParam("event") Event event, @RequestParam("firstname") String firstname) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLike(event, firstname).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEventAndFirstnameLikeAndLastnameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceResultsByEventAndFirstnameLikeAndLastnameLike(@RequestParam("event") Event event, @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(event, firstname, lastname).getResultList()),
                headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEventAndLastnameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceResultsByEventAndLastnameLike(@RequestParam("event") Event event, @RequestParam("lastname") String lastname) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndLastnameLike(event, lastname).getResultList()), headers, HttpStatus.OK);
    }
}
