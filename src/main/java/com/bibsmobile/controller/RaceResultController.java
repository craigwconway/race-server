package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Series;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import com.bibsmobile.util.BuildTypeUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.bibsmobile.model.CustomResultField;
import com.bibsmobile.model.DeviceInfo;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.License;
import com.bibsmobile.model.Split;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.dto.LeaderboardTeamDto;
import com.bibsmobile.model.dto.RaceResultDetailDto;
import com.bibsmobile.model.dto.RaceResultEnhancedViewDto;
import com.bibsmobile.model.dto.RaceResultViewDto;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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
        if(raceResult.getEvent() != null) {
        	raceResult.setEvent(Event.findEvent(raceResult.getEvent().getId()));
        }
        System.out.println("Splits:");
        if(raceResult.getSplits() != null) {
        	//for( raceResult.getSplits()) {
        	for(Entry<Integer, Split> splitEntry :raceResult.getSplits().entrySet()) {
        		System.out.println(splitEntry);        		
        	}
        	//}
        }
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
    
    @RequestMapping(value = "/seriessearch", method = RequestMethod.GET)
    @ResponseBody
    public String searchSeries(@RequestParam(value = "series") Long seriesId, 
    		@RequestParam(value = "name") String name,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
    		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        String rtn = "[]";
        try {
        	Series series = Series.findSeries(seriesId);
            List<RaceResult> raceResults = RaceResult.searchSeriesPaginated(series, name, page, size);
            rtn = RaceResultEnhancedViewDto.fromRaceResultsToDtoArray(raceResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }
    
    @RequestMapping(value = "teamrankings", method = RequestMethod.GET)
    public ResponseEntity<String> teamLeaderboard(
    		@RequestParam(value = "type") Long typeId,
    		@RequestParam(value = "start", defaultValue = "") String start,
    		@RequestParam(value = "gender", required = false) String gender,
    		@RequestParam(value = "size", defaultValue = "4") int pageSize){
    	
    	EventType type = EventType.findEventType(typeId);
    	List<String> teams;
    	start = StringUtils.lowerCase(start);
    	String searchGender = null;
    	if(StringUtils.equalsIgnoreCase(gender, "M")) searchGender = "M";
    	if(StringUtils.equalsIgnoreCase(gender, "F")) searchGender = "F";
    	if(searchGender != null) {
    		teams = RaceResult.getGenderTeamsForEventType(type, searchGender);
    	} else {
    		teams = RaceResult.getTeamsForEventType(type);
    	}
    	int index = 0;
    	for(int i = 0; i < teams.size(); i++) {
    		if(StringUtils.lowerCase(teams.get(i)).compareTo(start) > 0) {
    			index = i;
    			break;
    		}
    		if(i == teams.size() - 1) return new ResponseEntity<String>(HttpStatus.OK);
    	}
    	List<String> displayTeams;
    	if((teams.size() - index) < pageSize) {
    		displayTeams = teams.subList(index, teams.size()-1);
    	} else {
    		displayTeams = teams.subList(index, index + pageSize);
    	}
    	List<LeaderboardTeamDto> response = new ArrayList<LeaderboardTeamDto>();
    	for(String team : displayTeams) {
    		response.add(new LeaderboardTeamDto(RaceResult.getRankingForTeam(type, team, searchGender, 5), team));
    	}
    	System.out.println("Teams Found: " + response.size());
    	return new ResponseEntity<String>(LeaderboardTeamDto.toJsonArray(response), HttpStatus.OK);
    }
    
    @RequestMapping(value = "teamdetails", method = RequestMethod.GET)
    public ResponseEntity<String> teamDetails(
    		@RequestParam(value = "type") Long typeId,
    		@RequestParam(value = "team") String team,
    		@RequestParam(value = "gender", required = false) String gender,
    		@RequestParam(value = "size", required = false) Integer size){
    	EventType type = EventType.findEventType(typeId);
    	String searchGender = null;
    	if(StringUtils.equalsIgnoreCase(gender, "M")) searchGender = "M";
    	if(StringUtils.equalsIgnoreCase(gender, "F")) searchGender = "F";
    	LeaderboardTeamDto response = null;
    	if(size == null) {
    		response = new LeaderboardTeamDto(RaceResult.getRankingForTeam(type, team, searchGender), team);
    	} else {
    		response = new LeaderboardTeamDto(RaceResult.getRankingForTeam(type, team, searchGender, size), team);
    	}
    	return new ResponseEntity<String>(response.toJson(), HttpStatus.OK);
    }
    

    /**
     * @api {get} /raceresults/search Search
     * @apiName Search
     * @apiParam {Number} event Event to search from
     * @apiParam {Number} type Event Type to search from
     * @apiParam {Number} [bib] Bib number to search for
     * @apiParam {String} [name] First last or fullname to search
     * @apiParam {String=M,F} [gender] Gender to serch
     * @apiParam {Number} [page=1] Page of results to get
     * @apiParam {Number} [size=10] Number of results per page
     * @apiGroup raceresults
     * @apiPermission none
     * @apiDescription Search for a Race Result by event id and either first/last/fullname or bib. Results are returned in pages of 10
     * and start from page number 1.
     * @apiSuccess (200) {Object} RaceResult object returned
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(value = "event", required = false, defaultValue = "0") Long event,
    		@RequestParam(value = "type", required = false) Long type,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "bib", required = false, defaultValue = "") Long bib,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "team", required = false) String team,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
    		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size){
        String rtn = "[]";
        try {
            List<RaceResult> raceResults = RaceResult.searchPaginated(event, type, name, bib, page, size, gender, team);
            rtn = RaceResultViewDto.fromRaceResultsToDtoArray(raceResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }
    
    /**
     * @api {get} /raceresults/search Search
     * @apiName Search
     * @apiParam {Number} event Event to search from
     * @apiParam {Number} type Event Type to search from
     * @apiParam {Number} [bib] Bib number to search for
     * @apiParam {String} [name] First last or fullname to search
     * @apiParam {String=M,F} [gender] Gender to serch
     * @apiParam {Number} [page=1] Page of results to get
     * @apiParam {Number} [size=10] Number of results per page
     * @apiGroup raceresults
     * @apiPermission none
     * @apiDescription Search for a Race Result by event id and either first/last/fullname or bib. Results are returned in pages of 10
     * and start from page number 1.
     * @apiSuccess (200) {Object} RaceResult object returned
     * @return
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public String count(@RequestParam(value = "event", required = false, defaultValue = "0") Long event,
    		@RequestParam(value = "type", required = false) Long type,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "bib", required = false, defaultValue = "") Long bib,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "team", required = false) String team){
        String rtn = "";
        try {
            rtn += RaceResult.searchCount(event, type, name, bib, gender, team);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    /**
     * @api {get} /raceresults/byevent/:eventname
     * @apiName getRaceResultByEvent
     * @apiPermission none
     * @apiParam {String} eventName urlencoded name of event
     * @apiGroup raceresults
     * @apiSuccess (200) {Object[]} raceResults		Array of all raceResult objects in event.
     */
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

    /**
     * @api {get} /raceresults/byIdAndBib/:eventId/:bib
     * @apiName getRaceResultByEventIdAndBib
     * @apiGroup raceresults
     * @apiParam {Number} eventId id of event searched
     * @apiParam {Number} bib bib number of athlete
     * @apiPermission none
     * @apiSuccess (200) {Object} raceResult raceResult information
     * @param eventId long event.id searched
     * @param bib long raceResult.bib searched
     * @return JSON object of raceresult. If no raceresult is found, return emptystring.
     */
    @RequestMapping(value = "/byIdAndBib/{eventId}/{bib}", method = RequestMethod.GET)
    @ResponseBody
    public String byBib(@PathVariable long eventId, @PathVariable long bib) {
        String rtn = "";
        try {
        	Event event = Event.findEvent(eventId);
        	RaceResult raceResult = RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getSingleResult();
            //RaceResult raceResult = RaceResult.findRaceResultsByEventAndBibEquals(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), bib).getSingleResult();
            rtn = raceResult.toJson();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return rtn;
    }    
    
    /**
     * @api {get} /raceresults/bybib/:eventName/:bib
     * @apiName getRaceResultByBibAndEventName
     * @apiGroup raceresults
     * @apiParam {Number} bib bib number of runner
     * @apiParam {String} urlencoded name of event
     * @apiPermission none
     * @apiSuccess (200) {Object} raceResult	raceResult information
     * @param eventName String of event.name
     * @param bib long bib number to search for
     * @return JSON object of raceresult. If no raceresult is found, return emptystring.
     */
    @RequestMapping(value = "/bybib/{eventName}/{bib}", method = RequestMethod.GET)
    @ResponseBody
    public String byBib(@PathVariable String eventName, @PathVariable long bib) {
        String rtn = "";
        try {
            RaceResult raceResult = RaceResult.findRaceResultsByEventAndBibEquals(Event.findEventsByNameLike(eventName, 1, 1).getSingleResult(), bib).getSingleResult();
            return RaceResultDetailDto.fromRaceResultToDto(raceResult);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    /**
     * @api {get} /raceresults/byname/:eventName/:firstName/:lastName
     * @apiGroup raceresults
     * @apiName getRaceResultByEventNameFirstnameLastname
     * @apiParam {String} eventName Name of event to query
     * @apiParam {String} firstName First Name of athlete searched, ANY is wildcard
     * @apiParam {String} lastName Last Name of athlete searched, ANY is wildcard
     * @apiSuccess (200) {Object[]} raceResults list of raceresults matching query
     * @apiPermission none
     * @param eventName String name of event searched
     * @param firstName String firstname of athlete searched, ANY matches all
     * @param lastName String lastname of athlete searched, ANY matches all
     * @return Json array containing matching raceresults
     */
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

    /**
     * @api {get} /raceresults/byIdAndName/:eventName/:firstName/:lastName
     * @apiGroup raceresults
     * @apiName getRaceResultByEventIdFirstnameLastname
     * @apiParam {Number} eventId Name of event to query
     * @apiParam {String} [firstName] First Name of athlete searched, ANY is wildcard
     * @apiParam {String} [lastName] Last Name of athlete searched, ANY is wildcard
     * @apiSuccess (200) {Object[]} raceResults list of raceresults matching query
     * @apiPermission none
     * @param eventName String name of event searched
     * @param firstName String firstname of athlete searched, ANY matches all
     * @param lastName String lastname of athlete searched, ANY matches all
     * @return Json array containing matching raceresults
     */
    @RequestMapping(value = "/byIdAndName/{eventId}/{firstName}/{lastName}", method = RequestMethod.GET)
    @ResponseBody
    public String byIdAndName(@PathVariable Long eventId, @PathVariable String firstName, @PathVariable String lastName) {
        String rtn = "";
        try {
            if (firstName.equals("ANY"))
                rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndLastnameLike(Event.findEvent(eventId), lastName)
                        .getResultList());
            else if (lastName.equals("ANY"))
                rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLike(Event.findEvent(eventId), firstName)
                        .getResultList());
            else
                rtn = RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(Event.findEvent(eventId),
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
    						@RequestParam(value = "event") Long event, 
    						@RequestParam(value = "type", required = false) Long type,
    						Model uiModel) {
    	Event parentEvent =Event.findEvent(event);
    	EventType eventType = null;
    	if(type != null) {
    		eventType = EventType.findEventType(type);
    	}
    	uiModel.addAttribute("event", parentEvent);
        uiModel.addAttribute("types", EventType.findEventTypesByEvent(parentEvent));
        uiModel.addAttribute("eventType", eventType);
        int sizeNo = size == null ? 10 : size.intValue();
        final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
        float nrOfPages = 0;
        if (eventType != null){
        	nrOfPages = (float) RaceResult.countFindRaceResultsByEventType(eventType) / sizeNo;
        	uiModel.addAttribute("raceresults", RaceResult.findRaceResultsByEventType(eventType).setFirstResult(firstResult).setMaxResults(size).getResultList());
        	
        } else {
        	nrOfPages = (float) RaceResult.countFindRaceResultsByEvent(parentEvent) / sizeNo;
        	uiModel.addAttribute("raceresults", RaceResult.findRaceResultsByEvent(parentEvent).setFirstResult(firstResult).setMaxResults(size).getResultList());

        }
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        // addDateTimeFormatPatterns(uiModel);
        return "raceresults/list";
    }

    @RequestMapping(value="/unassigned", produces = "text/html")
    public static String manageUnassigned(
    						@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, 
    						@RequestParam(value = "size", required = false, defaultValue = "10") Integer size, 
    						@RequestParam(value = "event", required = true) Long event, 
    						Model uiModel) {
        int sizeNo = size == null ? 10 : size.intValue();
        float nrOfPages = 1;
        Event e = Event.findEvent(event);
        uiModel.addAttribute("raceresults", RaceResult.findUnassignedRaceResultsByEventPaginated(e, page, size));
        nrOfPages = (float) RaceResult.countFindUnassignedRaceResultsByEvent(e) / sizeNo;
        uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        // addDateTimeFormatPatterns(uiModel);
        return "raceresults/unassigned";
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
        System.out.println("Incoming result display time: " + raceResult.getTimeofficialdisplay() + " true time: " + raceResult.valueOfTimeofficialdisplay());
        // If this is an RFID build, do not allow the user to double up on a bib number:
        if(!StringUtils.isEmpty(raceResult.valueOfTimeofficialdisplay())){
        	raceResult.setTimeofficialmanual(raceResult.valueOfTimeofficialdisplay());
        }
        if(BuildTypeUtil.usesRfid()) {
        	Long matches = RaceResult.countFindRaceResultsByBibEquals(raceResult.getBib());
        	if (matches > 0) {
        		if(raceResult.getEvent() != null) {
        			uiModel.addAttribute("selectedEventID", raceResult.getEvent().getId());
        		}
        		bindingResult.rejectValue("bib", "Duplicate Bib");
        		uiModel.addAttribute("errors", "bib.duplicate");
        		uiModel.addAttribute("raceResult", raceResult);
        		uiModel.addAttribute("events", Event.findAllEvents());
        		return "raceresults/create";
        	}
        }
        
        uiModel.asMap().clear();
        
        raceResult.persist();	

        
        long eventId = 0;
        if (null != raceResult.getEvent()) {
            eventId = raceResult.getEvent().getId();
        }
        
        // Tell user that they have a licensed raceresult, if that is the case
        if (BuildTypeUtil.usesLicensing()) {
        	if(raceResult.isLicensed()) {
                return "redirect:/raceresults/?form&event=" + eventId + "&added="
                        + this.encodeUrlPathSegment(raceResult.getBib() + " " + raceResult.getFirstname() + " " + raceResult.getLastname() + " (licensed)", httpServletRequest);
        	} else {
                return "redirect:/raceresults/?form&event=" + eventId + "&added="
                        + this.encodeUrlPathSegment(raceResult.getBib() + " " + raceResult.getFirstname() + " " + raceResult.getLastname() + " (unlicensed)", httpServletRequest);
        	}
        }
        return "redirect:/raceresults/?form&event=" + eventId + "&added="
                + this.encodeUrlPathSegment(raceResult.getBib() + " " + raceResult.getFirstname() + " " + raceResult.getLastname() + "", httpServletRequest);
    }

    @Autowired
    UserProfileService userProfileService;

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel, @RequestParam("event") Long eventId) {
    	Event event = Event.findEvent(eventId);
    	List<EventType> eventTypes = EventType.findEventTypesByEvent(event);
    	uiModel.addAttribute("event", event);
    	uiModel.addAttribute("eventTypes", eventTypes);
        this.populateEditForm(uiModel, new RaceResult());
        return "raceresults/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
    	// sketchy bait-and-switch because that's what bibs does
    	RaceResult result = RaceResult.findRaceResult(id);
    	Event event = result.getEvent();
        List<EventType> eventTypes = EventType.findEventTypesByEvent(event);
        EventType eventType = result.getEventType();
        this.populateEditForm(uiModel, result);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventTypes", eventTypes);
        uiModel.addAttribute("splits", result.getSplits());
        uiModel.addAttribute("custom", result.getCustomFields());
        return "raceresults/update";
    }
    
    @RequestMapping(value = "/advancedupdate", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> updateAdvanced(@RequestBody RaceResult result) {
    	RaceResult trueResult = RaceResult.findFullRaceResult(result.getId());
    	if(result.getSplits() != null) {
    		System.out.println("Updating splits");
    		trueResult.setSplits(result.getSplits());
    	}
    	trueResult.merge();
    	return SpringJSONUtil.returnStatusMessage("ResultUpdated", HttpStatus.OK);
    }

    @RequestMapping(value = "/advanced/{id}", produces = "text/html")
    public String getExtras(@PathVariable("id") Long id, Model uiModel) {
    	// sketchy bait-and-switch because that's what bibs does
    	RaceResult result = RaceResult.findRaceResult(id);
    	System.out.println("Result id: " + result.getId() + " bib: " + result.getBib());
    	Event event = result.getEvent();
        EventType eventType = result.getEventType();
        Map<Integer, Split> splits = result.getSplits();
        Map<String, String> customFields = result.getCustomFields();
        this.populateEditForm(uiModel, result);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventType", eventType);
        uiModel.addAttribute("splits", splits);
        uiModel.addAttribute("custom", customFields);
        return "raceresults/advanced";
    }    
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid RaceResult raceResult, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, raceResult);
            return "raceresults/update";
        }
        uiModel.asMap().clear();
        System.out.println("Incoming Result: id:" + raceResult.getId() + " bib: "  + raceResult.getBib() + "timeofficial" + raceResult.valueOfTimeofficialdisplay());
        // Load true result from database, only modify fields on page.
        RaceResult trueResult = RaceResult.findRaceResult(raceResult.getId());
        trueResult.setFirstname(raceResult.getFirstname());
        trueResult.setLastname(raceResult.getLastname());
        trueResult.setAge(raceResult.getAge());
        trueResult.setGender(raceResult.getGender());
        trueResult.setBib(raceResult.getBib());
        trueResult.setEventType(raceResult.getEventType());
        trueResult.setTeam(raceResult.getTeam());
        trueResult.setCity(raceResult.getCity());
        trueResult.setState(raceResult.getState());
        trueResult.setLaps(raceResult.getLaps());
        trueResult.setTimeofficialdisplay(raceResult.getTimeofficialdisplay());
    	// Add logic to handle timeofficialdisplay updates if we have a nontrivial timeofficialdisplay value:
    	trueResult.setTimeofficialmanual(raceResult.valueOfTimeofficialdisplay());
		trueResult.merge();
    	return "redirect:/raceresults/" + encodeUrlPathSegment(trueResult.getId().toString(), httpServletRequest);

    }
    /**
     * Open the HTML view of edit raceresult. 
     * @param id ID of raceresult to edit (long raceResult.id)
     * @param uiModel
     * @return raceresult/update html view.
     */
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, RaceResult.findRaceResult(id));
        return "raceresults/update";
    }
    
    /**
     * Delete a race result by HTML. This is currently only accessible to sysadmin/eventadmin.
     * TODO: restrict this to eventadmin who are managing the particular race result
     * @param id - Unique id of raceresult to delete (raceResult.id)
     * @param page - Page of list view to return to
     * @param size - entries per page in list view
     * @param uiModel
     * @return Returns a redirect to the HTML list of raceresults.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        if(BuildTypeUtil.usesLicensing() && raceResult.isLicensed()) {
        	// If the timeofficialdisplay for this raceresults is nontrivial
        	// refund the read
        	if(raceResult.getTimeofficialdisplay().isEmpty() && !raceResult.isTimed()) {
        		DeviceInfo.quickBlindRemoveRunner();
        	}
        }
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

    /**
     * @api {get} /raceresults/:id Get Race Result Details
     * @apiName Get Race Result Details
     * @apiDescription Pass up an id to get a detailed description of a RaceResult
     * @apiGroup raceresults
     * @apiName getRaceResultById
     * @apiUse raceResultDetailDto
     * @apiPermission none
     * @param id Long raceResult.id to get
     * @return JSON object of raceResult with HTTP 200, or HTTP 404 Not found
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (raceResult == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(RaceResultDetailDto.fromRaceResultToDto(raceResult), headers, HttpStatus.OK);
    }
    
    /**
     * @api {get} /raceresults/details/:id Get Race Result Details
     * @apiName Get Race Result Details
     * @apiDescription Pass up an id to get a detailed description of a RaceResult
     * @apiGroup raceresults
     * @apiName getRaceResultById
     * @apiUse raceResultDetailDto
     * @apiPermission none
     * @param id Long raceResult.id to get
     * @return JSON object of raceResult with HTTP 200, or HTTP 404 Not found
     */
    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showDetails(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (raceResult == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(RaceResultDetailDto.fromRaceResultToDto(raceResult), headers, HttpStatus.OK);
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
        if(true == raceResult.isLicensed()) {
        	if(raceResult.getTimeofficial() == 0) {
        		DeviceInfo.quickBlindRemoveRunner();
        	}
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
