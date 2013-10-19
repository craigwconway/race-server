package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;

import java.util.List;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/raceresults")
@Controller
@RooWebScaffold(path = "raceresults", formBackingObject = RaceResult.class)
@RooWebJson(jsonObject = RaceResult.class)
public class RaceResultController {

    @RequestMapping(value = "/byevent/{eventName}", method = RequestMethod.GET)
    @ResponseBody
    public String byEvent(@PathVariable String eventName, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        String rtn = "";
        try {
            Event event = Event.findEventsByNameLike(eventName, page, size).getSingleResult();
            List<RaceResult> raceResults = RaceResult.findRaceResultsByEvent(event,page,size).getResultList();
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

    @RequestMapping(value = "/awards/overall", method = RequestMethod.GET)
    @ResponseBody
    public String byOverallTime(
    		@RequestParam(value = "event", required = true) Long event, 
    		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
    		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(RaceResult.toJsonArray(RaceResult.findRaceResultsByOverallTime(
            		Event.findEvent(event),page,size)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn.toString();
    }
    
    @RequestMapping(value = "/awards/gender", method = RequestMethod.GET)
    @ResponseBody
    public String byOverallTimeFemale(
    		@RequestParam(value = "event", required = true) Long event, 
    		@RequestParam(value = "gender", required = true) String gender,  
    		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
    		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(RaceResult.toJsonArray(RaceResult.findRaceResultsByOverallTimeAndGender(
            		Event.findEvent(event),gender,page,size)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/awards/gender/age", method = RequestMethod.GET)
    @ResponseBody
    public String byOverallTimeMaleAge(
    		@RequestParam(value = "event", required = true) Long event,  
    		@RequestParam(value = "gender", required = true) String gender,  
    		@RequestParam(value = "min", required = false, defaultValue = "1") Integer min, 
    		@RequestParam(value = "max", required = false, defaultValue = "19") Integer max, 
    		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
    		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(RaceResult.toJsonArray(RaceResult.findRaceResultsByOverallTimeAndGenderAndAge(
            		Event.findEvent(event),gender,min,max,page,size)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public Integer countRaceResultsByEvent(
    		@RequestParam(value = "event", required = true) Long event) {
        try {
        	return RaceResult.countRaceResultsByEvent(Event.findEvent(event));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}
