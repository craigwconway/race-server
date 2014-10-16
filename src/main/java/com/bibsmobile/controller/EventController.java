package com.bibsmobile.controller;

import com.bibsmobile.model.*;
import com.bibsmobile.util.UserProfileUtil;

import flexjson.JSONSerializer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RequestMapping("/events")
@Controller
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private SimpleMailMessage eventMessage;


    @RequestMapping(value = "/registrationComplete", method = RequestMethod.GET)
    public static String registrationComplete(@RequestParam(value = "event", required = true) Long event, Model uiModel) {

        return "events/registrationComplete";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Event event, BindingResult bindingResult,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, event);
            return "events/create";
        }
        uiModel.asMap().clear();

        // add to current usergroup
        boolean addGroup = false;
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        UserProfile user = UserProfile.findUserProfilesByUsernameEquals(
                username).getSingleResult();
       /* if (user.getUserGroup() != null) {
            addGroup = true;
            log.info("event group " + user.getUserGroup().getName());
            event.setUserGroup(user.getUserGroup());
        }

        event.persist();

        if (addGroup) {
            Set<Event> groupEvents = user.getUserGroup().getEvents();
            groupEvents.add(event);
            user.getUserGroup().setEvents(groupEvents);
            user.getUserGroup().merge();
        }*/
        event.persist();
        
        // default awards categories
        AwardCategory.createDefaultMedals(event);
        AwardCategory.createAgeGenderRankings(event, 
        		AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 
        		AwardCategory.DEFAULT_AGE_SPAN, AwardCategory.DEFAULT_LIST_SIZE);

        return "redirect:/events/"
                + encodeUrlPathSegment(event.getId().toString(),
                httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        Event event = Event.fromJsonToEvent(json);
        event.persist();
        return event.toJson();
    }

    public static String doPost(String targetURL, String data, boolean json) {
        URL url;
        HttpURLConnection connection = null;
        try {
            // Create connection
            url = new URL(targetURL);
            log.info("doPost " + targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            if (json) {
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type",
                        "application/json; charset=UTF-8");
            }
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(data.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                // response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String doGet(String targetURL) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            log.info(targetURL);
            url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public static String doDelete(String targetURL) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            log.info("doDelete " + targetURL);
            url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @RequestMapping(value = "/cloud", method = RequestMethod.GET)
    @ResponseBody
    public static String cloud(
            @RequestParam(value = "event", required = true) long eventId) {
        final String serverUrl = "http://54.225.209.173:8080/bibs-server";
        Event event = Event.findEvent(eventId);
        try {
            // client event id
            long _eventId = event.getId();

            // find matching event
            String eventSync = doGet(serverUrl + "/events/byname/"
                    + event.getName().replace(" ", "%20"));
            log.info("cloud " + eventSync);
            Collection<Event> events = Event.fromJsonArrayToEvents(eventSync);
            log.info("cloud matched " + events.size() + " events");
            if (events.size() < 1)
                return "eventnotfound";
            Event event1 = events.iterator().next();
            log.info("cloud " + event1.getId() + " " + event1.getName());
            // login TODO
            // remove server runners
            doPost(serverUrl + "/events/" + event1.getId(), "_method=DELETE&x=7&y=10", false);
            log.info("cloud delete " + event1.getId() + " " + event1.getName());
            // loop through and add runners
            List<RaceResult> runners = Event.findRaceResults(_eventId, 1, 9999);
            StringWriter json = new StringWriter();
            int i = 0;
            json.append("[");
            for (RaceResult runner : event.getRaceResults()) {
                if (i > 0)
                    json.append(",");
                i++;
                json.append("{\"event\":").append(event1.toJson());
                json.append(",\"firstname\":\"").append(runner.getFirstname());
                json.append("\",\"lastname\":\"").append(runner.getLastname());
                if (null != runner.getCity())
                    json.append("\",\"city\":\"").append(runner.getCity());
                if (null != runner.getState())
                    json.append("\",\"state\":\"").append(runner.getState());
                json.append("\",\"bib\":\"").append(runner.getBib());
                json.append("\",\"age\":\"").append(runner.getAge());
                json.append("\",\"gender\":\"").append(runner.getGender());
                json.append("\",\"timeoverall\":\"").append(
                        runner.getTimeofficialdisplay());
                json.append("\"}");
            }
            json.append("]");
            log.info("cloud sync: " + json);
            doPost(serverUrl + "/raceresults/jsonArray", json.toString(), true);

            log.info("cloud synced ");
            return "true";
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return "false";
    }

    @RequestMapping(value = "/gun", method = RequestMethod.GET)
    @ResponseBody
    public static String timerGun(
            @RequestParam(value = "event", required = true) long event) {
        try {
            Event e = Event.findEvent(event);
            e.setGunFired(true);
            e.setGunTime(new Date());
            e.merge();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "false";
        }
        return "true";
    }

    @RequestMapping(value = "/run", method = RequestMethod.GET)
    @ResponseBody
    public static String run(
            @RequestParam(value = "event", required = true) long event,
            @RequestParam(value = "order", required = false, defaultValue = "1") int order) {
        try {
            Event e = Event.findEvent(event);
            e.setRunning(order);
            e.merge();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "false";
        }
        return "true";
    }

    @RequestMapping(value = "/done", method = RequestMethod.GET)
    @ResponseBody
    public static String timerDone(
            @RequestParam(value = "event", required = true) long event) {
        log.info("event done " + event);
        try {
            Event e = Event.findEvent(event);
            e.setRunning(0);
            e.merge();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return "false";
        }
        return "true";
    }

    @RequestMapping(value = "/results", method = RequestMethod.GET)
    @ResponseBody
    public static String resultsQuery(
            @RequestParam(value = "event", required = true) long event_id) {
        try {
            return RaceResult.toJsonArray(Event.findRaceResultsForAnnouncer(
                    event_id, 1, 15));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "false";
    }

    @RequestMapping(value = "/manual", method = RequestMethod.GET)
    @ResponseBody
    public static String setTimeManual(
            @RequestParam(value = "event", required = true) long event_id,
            @RequestParam(value = "bib", required = true) String bib) {
    	RaceResult result = new RaceResult();
        try {
        	long bibtime = System.currentTimeMillis();
        	Event event = Event.findEvent(event_id);
        	result = RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getSingleResult();
			// bib vs chip start
			long starttime = 0l;
			if(result.getTimestart()>0){
				starttime = Long.valueOf(result.getTimestart()) ;
			}else{
				starttime = event.getGunTime().getTime(); 
				result.setTimestart( starttime );
			}
			final String strTime = RaceResult.toHumanTime(starttime, bibtime);
			result.setTimeofficial( bibtime );
			result.setTimeofficialdisplay( strTime );
			result.merge();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result.toJson();
    }

    @RequestMapping(value = "/featured", method = RequestMethod.GET)
    @ResponseBody
    public static String featured(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(Event.toJsonArray(Event.findEventsByFeaturedGreaterThan(
                    0, page, size).getResultList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/byname/{eventName}", method = RequestMethod.GET)
    @ResponseBody
    public static String byName(
            @PathVariable String eventName,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(Event.toJsonArray(Event.findEventsByNameLike(eventName,
                    page, size).getResultList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/name/{eventName}", method = RequestMethod.GET)
    @ResponseBody
    public static String byName(@PathVariable String eventName) {
        log.info("byName=" + eventName);
        StringWriter rtn = new StringWriter();
        try {
            rtn.append(Event.findEventByNameEquals(eventName).toJson());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/future", method = RequestMethod.GET)
    @ResponseBody
    public static String future(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            Integer featured) {
        StringBuffer rtn = new StringBuffer();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        try {
            rtn.append(Event.toJsonArray(Event
                    .findEventsByTimeStartGreaterThanAndFeaturedEquals(
                            today.getTime(), 0, page, size).getResultList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/past", method = RequestMethod.GET)
    @ResponseBody
    public static String past(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        try {
            rtn.append(Event.toJsonArray(Event.findEventsByTimeStartLessThan(
                    today.getTime(), page, size).getResultList()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/raceday", method = RequestMethod.GET)
    public static String raceday(Model uiModel) {

        // license TODO

        uiModel.addAttribute("events", Event.findAllEvents());
        uiModel.addAttribute("eventsRunning", Event.findEventsByRunning());
        return "events/raceday";
    }

    @RequestMapping(value = "/running", method = RequestMethod.GET)
    @ResponseBody
    public static String running() {
        return Event.toJsonArray(Event.findEventsByRunning());
    }

    // medals
    @RequestMapping(value = "/awards", method = RequestMethod.GET)
    public static String awards(
    		@RequestParam(value = "event", required = true) Long eventId,
    		Model uiModel) {
    	Event event = Event.findEvent(eventId);
    	List<String> mastersBibs = new ArrayList<String>();
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	
    	// if not allow masters in overall, collect masters bibs, pass into non-masters
    	if(!event.getAwardsConfig().isAllowMastersInNonMasters()){
        	for(AwardCategory c:event.getAwardCategorys()){
        		if(c.isMedal() && c.isMaster()){
        			List<RaceResult> masters = event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize());
        			for(RaceResult m:masters){
        				mastersBibs.add(m.getBib());
        			}
        		}
        	}
    	}
    	
		// filter medals
    	for(AwardCategory c:event.getAwardCategorys()){
    		if(c.isMedal()){
    			c.setName(c.getName().replaceAll(AwardCategory.MEDAL_PREFIX, StringUtils.EMPTY)); // hack
    			List<RaceResult> r = (c.isMaster()) ? event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize())
    					: event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize(), mastersBibs);
    			System.out.println(c.getName()+" "+r.size());
    			results.add(new AwardCategoryResults(c,r));
    		}
    	}
    	
    	Collections.sort(results);
        uiModel.asMap().clear();
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("awardCategoryResults", results);
        return "events/awards";
    }

    @RequestMapping(value = "/ageGenderRankings", method = RequestMethod.GET)
    public static String ageGenderRankings(
    		@RequestParam(value = "event", required = true) Long eventId,
    		@RequestParam(value = "gender", required = false, defaultValue = "M") String gender,
    		Model uiModel) {
    	Event event = Event.findEvent(eventId);
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	List<AwardCategory> list = event.getAwardCategorys();
    	List<String> medalsBibs = new ArrayList<String>();

    	// if not allow medals in age/gender, collect medals bibs, pass into non-medals
    	if(!event.getAwardsConfig().isAllowMedalsInAgeGenderRankings()){
        	for(AwardCategory c:list){
        		if(c.isMedal()){
        			List<RaceResult> medals = event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize());
        			for(RaceResult m:medals){
        				medalsBibs.add(m.getBib());
        			}
        		}
        	}
    	}
    	
		// filter age/gender
    	for(AwardCategory c:list){
    		if(!c.isMedal() && c.getGender().toUpperCase().equals(gender.toUpperCase())){
    			results.add(new AwardCategoryResults(c,event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize(), medalsBibs)));
    		}
    	}
    	
    	Collections.sort(results);
        uiModel.asMap().clear();
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("awardCategoryResults", results);
        return "events/ageGenderRankings";
    }

    @RequestMapping(value = "/ageGenderRankings/update", method = RequestMethod.GET)
    public static String updateAgeGenderRankings(
    		@RequestParam(value = "event", required = true) Long eventId,
    		@RequestParam(value = "gender", required = false, defaultValue = "M") String gender,
    		HttpServletRequest httpServletRequest) {
    	Event event = Event.findEvent(eventId);
    	EventAwardsConfig awardsConfig = event.getAwardsConfig();
    	awardsConfig.setAllowMastersInNonMasters(httpServletRequest.getParameterMap().containsKey("master"));
    	awardsConfig.setAllowMedalsInAgeGenderRankings(httpServletRequest.getParameterMap().containsKey("duplicate"));
    	event.setAwardsConfig(awardsConfig);
    	event.merge();
        return "redirect:/events/ageGenderRankings?event="+eventId+"&gender=M";
    }

    @RequestMapping(value = "/awards/update", method = RequestMethod.GET)
    public static String updateAwards(
    		@RequestParam(value = "event", required = true) Long eventId,
    		HttpServletRequest httpServletRequest) {
    	Event event = Event.findEvent(eventId);
    	EventAwardsConfig awardsConfig = event.getAwardsConfig();
    	awardsConfig.setAllowMastersInNonMasters(httpServletRequest.getParameterMap().containsKey("master"));
    	awardsConfig.setAllowMedalsInAgeGenderRankings(httpServletRequest.getParameterMap().containsKey("duplicate"));
    	event.setAwardsConfig(awardsConfig);
    	event.merge();
        return "redirect:/events/awards?event="+eventId;
    }

    @RequestMapping(value = "/timeofficial", method = RequestMethod.GET)
    @ResponseBody
    public static String byTimeOfficial(
            @RequestParam(value = "event", required = true) Long event,
            @RequestParam(value = "gender", required = false, defaultValue = "") String gender,
            @RequestParam(value = "min", required = false, defaultValue = "0") int min,
            @RequestParam(value = "max", required = false, defaultValue = "0") int max,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        Event e = Event.findEvent(event);
        List<RaceResult> results = e.getAwards(gender, min, max, size);
        Collections.sort(results);
        return RaceResult.toJsonArray(results);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public static String countRaceResultsByEvent(
            @RequestParam(value = "event", required = true) Long event) {
        try {
            return String.valueOf(Event.countRaceResults(event));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "0";
    }

    @RequestMapping(value = "/countstarted", method = RequestMethod.GET)
    @ResponseBody
    public static String countRaceResultsStartedByEvent(
            @RequestParam(value = "event", required = true) Long event) {
        try {
            return String.valueOf(Event.countRaceResultsStarted(event));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "0";
    }

    @RequestMapping(value = "/countcomplete", method = RequestMethod.GET)
    @ResponseBody
    public static String countRaceResultsCompleteByEvent(
            @RequestParam(value = "event", required = true) Long event) {
        try {
            return String.valueOf(Event.countRaceResultsComplete(event));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "0";
    }


    void populateEditForm(Model uiModel, Event event) {
        uiModel.addAttribute("event", event);
        addDateTimeFormatPatterns1(uiModel);
    }

    void addDateTimeFormatPatterns1(Model uiModel) {
        uiModel.addAttribute("event_timestart_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_timeend_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_guntime_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_created_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_updated_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_regstart_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_regend_date_format", "MM/dd/yyyy h:mm:ss a");
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public static void export(
            @RequestParam(value = "event", required = true) Long event,
            HttpServletResponse response) throws IOException {
        Event _event = Event.findEvent(event);
        response.setContentType("text/csv;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + _event.getName() + ".csv\"");
        OutputStream resOs = response.getOutputStream();
        OutputStream buffOs = new BufferedOutputStream(resOs);
        OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);

        List<RaceResult> runners = Event.findRaceResults(event, 0, 99999);
        for (RaceResult r : runners) {
            outputwriter.write(r.getBib() + "," + r.getFirstname() + ","
                    + r.getLastname() + "," + r.getCity() + "," + r.getState()
                    + "," + r.getTimeofficialdisplay() + "," + r.getGender()
                    + "," + r.getAge() + "\r\n");
        }
        outputwriter.flush();
        outputwriter.close();

    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Event event, BindingResult bindingResult,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, event);
            return "events/update";
        }

        Date time0 = new Date(event.getGunTimeStart());
        Date time1 = event.getGunTime();
        log.info("update2 " + (time0 == time1) + " " + time0 + " "
                + time1);

        if (time0 != time1 && null != event.getGunTime()) {
            for (RaceResult r : RaceResult.findRaceResultsByEvent(event)
                    .getResultList()) {
                r.setTimestart(time1.getTime());
                r.merge();
            }
            event.setGunTimeStart(event.getGunTime().getTime());
        }

        uiModel.asMap().clear();
        event.merge();
        return "redirect:/events/"
                + encodeUrlPathSegment(event.getId().toString(),
                httpServletRequest);
    }

    @RequestMapping(value = "/bystateandcity", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsByStateAndrCity(@RequestParam("state") String state,
                                            @RequestParam(value = "city", required = false) String city,
                                            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        if (!StringUtils.isEmpty(city)) {
            return Event.toJsonArray(Event.findEventsByStateEqualsAndCityEquals(state, city, (page - 1) * size, size).getResultList());
        }
        return Event.toJsonArray(Event.findEventsByStateEquals(state, (page - 1) * size, size).getResultList());
    }

    @RequestMapping(value = "/bytype", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsByType(@RequestParam("type") String type,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return Event.toJsonArray(Event.findEventsByTypeEquals(type, (page - 1) * size, size).getResultList());
    }

    @RequestMapping(value = "/cities", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsCities(@RequestParam(value = "country", required = false) String country) {
        if (StringUtils.isEmpty(country)) {
            return new JSONSerializer().serialize(Event.findAllEventsCities().getResultList());
        }
        return new JSONSerializer().serialize(Event.findAllEventsCitiesByCountry(country).getResultList());
    }

    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsCountries() {
        return new JSONSerializer().serialize(Event.findAllEventsCountries().getResultList());
    }


    @RequestMapping(value = "/search/byusergroup/{userGroupId}", method = RequestMethod.GET)
    @ResponseBody
    public String findByUserGroup(@PathVariable Long userGroupId) {
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        Map<Long, Event> events = new HashMap<>();
        if (userGroup != null) {
            for (EventUserGroup eventUserGroup : userGroup.getEventUserGroups()) {
                Event event = eventUserGroup.getEvent();
                if (!events.containsKey(event.getId())) {
                    events.put(event.getId(), event);
                }
            }
        }
        return Event.toJsonArray(events.values());
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Event> result = Event.findAllEvents();
        return new ResponseEntity<String>(Event.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/notify")
    public @ResponseBody String notifyParticipants(@RequestParam Long eventId) {
        Event event = Event.findEvent(eventId);
        if (event == null) {
            return "[]";
        }
        List<EventCartItem> eventCartItems = EventCartItem.findEventCartItemsByEvent(event).getResultList();
        List<EventCartItem> validEventCartItems = new ArrayList<>();
        for (EventCartItem eventCartItem : eventCartItems) {
            if (eventCartItem.getType().equals(EventCartItemTypeEnum.TICKET)) {
                validEventCartItems.add(eventCartItem);
            }
        }
        if (validEventCartItems.isEmpty()) {
            return "[]";
        }
        List<CartItem> cartItems = CartItem.findCartItemsByEventCartItems(validEventCartItems, null, null).getResultList();
        List<UserProfile> users = new ArrayList<>();
        List<String> sentTo = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getCart().getStatus() == Cart.COMPLETE) {
                users.add(cartItem.getCart().getUser());
            }
        }
        for (UserProfile user : users) {
            if (StringUtils.isNotEmpty(user.getEmail())) {
                if (!sentTo.contains(user.getEmail())) {
                	try{
                        eventMessage.setTo(user.getEmail());
                        mailSender.send(eventMessage);
                        sentTo.add(user.getEmail());
                	}catch(Exception e){
                		System.out.println("EXCEPTION: Email Send Fail - "+e.getMessage());
                	}
                }
            }
        }
        return sentTo.toString();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Event event = Event.findEvent(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (event == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        event.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Event event = Event.fromJsonToEvent(json);
        event.setId(id);
        if (event.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Event());
        return "events/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel, HttpServletRequest request) {
        addDateTimeFormatPatterns(uiModel);
        Event e = Event.findEvent(id);
        ResultsFile latestImportFile = e.getLatestImportFile();
        ResultsImport latestImport = ((latestImportFile == null) ? null : latestImportFile.getLatestImport());
        ResultsFileMapping latestMapping = ((latestImport == null) ? null : latestImport.getResultsFileMapping());
        uiModel.addAttribute("event", e);
        uiModel.addAttribute("dropboxUnlink", (UserProfileUtil.getLoggedInDropboxAccessToken() != null));
        uiModel.addAttribute("lastImport", latestImport);
        uiModel.addAttribute("lastMapping", latestMapping);
        uiModel.addAttribute("itemId", id);
        return "events/show";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id, Model uiModel) {
        Event event = Event.findEvent(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (event == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(event.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("events", Event.findEventEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Event.countEvents() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("events", Event.findAllEvents(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "events/list";
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Event.findEvent(id));
        return "events/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Event event = Event.findEvent(id);
        event.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/events";
    }
    
    @RequestMapping(value = "/createAwardCategories", produces = "text/html")
    public String createAgeGenderRankings(
    		@RequestParam(value = "event") long eventId,
    		@RequestParam(value = "ageMin") int ageMin,
    		@RequestParam(value = "ageMax") int ageMax,
    		@RequestParam(value = "ageRange") int ageRange,
    		@RequestParam(value = "listSize") int listSize,
    		Model uiModel){
    	Event event = Event.findEvent(eventId);
    	// delete old categories
    	int deleted = new AwardCategory().removeAgeGenderRankingsByEvent(event);
    	System.out.println("DELETED CATS "+deleted);
    	// make new categories
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	List<AwardCategory> list = AwardCategory.createAgeGenderRankings(event, ageMin, ageMax, ageRange, listSize);
    	for(AwardCategory c:list){
    		if(c.getGender().trim().isEmpty()){
    			results.add(new AwardCategoryResults(c,event.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize())));
    		}
    	}
    	Collections.sort(results);
        uiModel.asMap().clear();
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("awardCategoryResults", results);
        return "redirect:/events/ageGenderRankings?event="+eventId+"&gender=M";
    }
    
    @RequestMapping(value = "/overall", produces = "text/html")
    public String overallResults(
    		@RequestParam(value = "event") long eventId,
    		Model uiModel){
    	Event event = Event.findEvent(eventId);
    	List<RaceResult> results = event.getAwards(StringUtils.EMPTY, AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 9999);
    	uiModel.asMap().clear();
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("results", results);
        return "events/overall";
    }

    
    class OfficialtimeComparator implements Comparator<RaceResult> {
        @Override
        public int compare(RaceResult a, RaceResult b) {
            return a.getTimeofficialdisplay().compareTo(b.getTimeofficialdisplay());
        }
    }
    
    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("event_timestart_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_timeend_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_regstart_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_regend_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_guntime_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_created_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_updated_date_format", "MM/dd/yyyy h:mm:ss a");
    }
    
    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

}
