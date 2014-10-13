package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.context.SecurityContextHolder;

@RequestMapping("/raceresults")
@Controller
@RooWebScaffold(path = "raceresults", formBackingObject = RaceResult.class)
@RooWebJson(jsonObject = RaceResult.class)
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
	if (loggedinUsername.equals("anonymousUser")) return "";
        UserProfile loggedinUserProfile = UserProfile.findUserProfilesByUsernameEquals(loggedinUsername).getResultList().get(0);
        raceResult.setUserProfile(loggedinUserProfile);
        raceResult.persist();
        return raceResult.toJson();
    }
	
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(value = "event", required = false, defaultValue = "0") Long event, 
						 @RequestParam(value = "name", required = false, defaultValue = "") String name, 
						 @RequestParam(value = "bib", required = false, defaultValue = "") String bib) {
        String rtn = "[]";
        try {
            List<RaceResult> raceResults = RaceResult.search(event,name,bib);
            rtn = RaceResult.toJsonArray(raceResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/byevent/{eventName}", method = RequestMethod.GET)
    @ResponseBody
    public String byEvent(@PathVariable String eventName, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        String rtn = "";
        try {
            Event event = Event.findEventsByNameLike(eventName, page, size).getSingleResult();
            List<RaceResult> raceResults = Event.findRaceResults(event.getId(),page,size);
            rtn = RaceResult.toJsonArray(raceResults);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/bybib/{eventName}/{bib}", method = RequestMethod.GET)
    @ResponseBody
    public String byBib(@PathVariable String eventName, @PathVariable String bib) {
        String rtn = "";
        try {
            RaceResult raceResult = RaceResult.findRaceResultsByEventAndBibEquals(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), bib).getSingleResult();
            rtn = raceResult.toJson();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/byname/{eventName}/{firstName}/{lastName}", method = RequestMethod.GET)
    @ResponseBody
    public String byName(@PathVariable String eventName, @PathVariable String firstName, @PathVariable String lastName) {
        String rtn = "";
        try {
            if (firstName.equals("ANY")) rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndLastnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), lastName).getResultList()); 
            else if (lastName.equals("ANY")) rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), firstName).getResultList()); 
            else rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), firstName, lastName).getResultList());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn;
    }

    @RequestMapping(value = "/bynamefeelinglucky/{eventName}/{firstName}/{lastName}", method = RequestMethod.GET)
    @ResponseBody
    public String byNameFeelingLucky(@PathVariable String eventName, @PathVariable String firstName, @PathVariable String lastName) {
        String rtn = "";
        try {
            rtn = RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(
            		Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), 
            			firstName, lastName).getSingleResult().toJson(); 
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn;
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
        if(event > 0){
        	uiModel.addAttribute("raceresults", Event.findRaceResults(event,firstResult, sizeNo));
            nrOfPages = (float) Event.countRaceResults(event) / sizeNo;
        }else{
        	uiModel.addAttribute("raceresults", RaceResult.findRaceResultEntries(firstResult, sizeNo));
            nrOfPages = (float) RaceResult.countRaceResults() / sizeNo;
        }
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        //addDateTimeFormatPatterns(uiModel);
        return "raceresults/list";
    }

    @RequestMapping(value = "/bibs", method = RequestMethod.GET, produces = "text/html")
    public static String bibs(){

		// license TODO
    	
    	return "raceresults/bibs";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid RaceResult raceResult, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, raceResult);
            return "raceresults/create";
        }
        uiModel.asMap().clear();
        raceResult.persist();
        long eventId = 0;
        if(null!=raceResult.getEvent()){
        	eventId = raceResult.getEvent().getId();
        }
        return "redirect:/raceresults/?form&event="+eventId+"&added=" 
        		 + encodeUrlPathSegment(raceResult.getBib()
        		 +" "+ raceResult.getFirstname()
        		 +" "+ raceResult.getLastname(), httpServletRequest);
    }
    
}
