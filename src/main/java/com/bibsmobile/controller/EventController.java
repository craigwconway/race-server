package com.bibsmobile.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.authorize.sim.Fingerprint;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
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

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventRegistration;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;

@RequestMapping("/events")
@Controller
@RooWebScaffold(path = "events", formBackingObject = Event.class)
@RooWebJson(jsonObject = Event.class)
public class EventController { 
	
	@RequestMapping(value = "/registrationComplete", method = RequestMethod.GET)
	public static String registrationComplete(@RequestParam(value = "event", required = true) Long event, Model uiModel){

	    return "events/registrationComplete";
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public static String registration(@RequestParam(value = "event", required = true) Long eventid, Model uiModel){
		
		boolean test=true; //??
		
		Event event = Event.findEvent(eventid);
		uiModel.addAttribute("event", event); 
		
		Cart cart = new Cart();
		EventRegistration reg = new EventRegistration();
		reg.setEvent(event); // set price here?
		Set<CartItem> cartItems = new LinkedHashSet<CartItem>(1);
		cartItems.add(reg);
		cart.setCartItems(cartItems);
		cart.persist();
		
	    String apiLoginId = "7rWKZe476";
	    uiModel.addAttribute("apiLoginId", apiLoginId); 
	    
	    String transactionKey = (test)?"5Fg6846nb7pAS4X4":""+cart.getId();
	    System.out.println(transactionKey+" transactionKey");
	    uiModel.addAttribute("transactionKey", transactionKey); 
	    
	    String relayResponseUrl = "http:/localhost:8080/bibs-server/events/registrationComplete?event=1";
	    uiModel.addAttribute("relayResponseUrl", relayResponseUrl); 

	    double amount = (test)?new Random().nextDouble()+.01:cart.getTotal();
	    
	    NumberFormat df = DecimalFormat.getInstance();
	    df.setMaximumFractionDigits(2);
	    String samount = df.format(amount);
	    uiModel.addAttribute("amount", samount);  
	    
	    Fingerprint fingerprint = Fingerprint.createFingerprint(
	        apiLoginId,
	        transactionKey,
	        1234567890,  // random sequence used for creating the finger print
	        samount);

	    long x_fp_sequence = fingerprint.getSequence();
	    uiModel.addAttribute("x_fp_sequence", x_fp_sequence); 
	    
	    long x_fp_timestamp = fingerprint.getTimeStamp();
	    uiModel.addAttribute("x_fp_timestamp", x_fp_timestamp); 
	    
	    String x_fp_hash = fingerprint.getFingerprintHash();
	    uiModel.addAttribute("x_fp_hash", x_fp_hash); 

	    return "events/registration";
	}
	
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid Event event, BindingResult bindingResult,
			Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, event);
			return "events/create";
		}
		uiModel.asMap().clear();

		// awards

		// add to current usergroup
		String username = SecurityContextHolder.getContext()
				.getAuthentication().getName();
		UserProfile user = UserProfile.findUserProfilesByUsernameEquals(
				username).getSingleResult();
		System.out.println("event group " + user.getUserGroup().getName());
		event.setUserGroup(user.getUserGroup());

		event.persist();

		Set<Event> groupEvents = user.getUserGroup().getEvents();
		groupEvents.add(event);
		user.getUserGroup().setEvents(groupEvents);
		user.getUserGroup().merge();

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
			System.out.println("doPost " + targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			if(json){
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

			e.printStackTrace();
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
			System.out.println(targetURL);
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
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
			System.out.println("doDelete " + targetURL);
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
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
					+ event.getName().replace(" ", "%20") );
			System.out.println("cloud " + eventSync);
			Collection<Event> events = Event.fromJsonArrayToEvents(eventSync);
			System.out.println("cloud matched " + events.size() + " events");
			if (events.size() < 1)
				return "eventnotfound";
			Event event1 = events.iterator().next();
			System.out.println("cloud "+event1.getId() + " " + event1.getName());
			// login TODO
			// remove server runners
			doPost(serverUrl+"/events/"+event1.getId(),"_method=DELETE&x=7&y=10",false);
			System.out.println("cloud delete " + event1.getId() + " " + event1.getName());
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
			System.out.println("cloud sync: "+json);
			doPost(serverUrl + "/raceresults/jsonArray", json.toString(),true);

			System.out.println("cloud synced ");
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
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
		} catch (Exception x) {
			x.printStackTrace();
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
		} catch (Exception x) {
			x.printStackTrace();
			return "false";
		}
		return "true";
	}

	@RequestMapping(value = "/done", method = RequestMethod.GET)
	@ResponseBody
	public static String timerDone(
			@RequestParam(value = "event", required = true) long event) {
		System.out.println("event done " + event);
		try {
			Event e = Event.findEvent(event);
			e.setRunning(0);
			e.merge();
		} catch (Exception x) {
			x.printStackTrace();
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
			e.printStackTrace();
		}
		return "false";
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
			// e.printStackTrace();
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
			// e.printStackTrace();
		}
		return rtn.toString();
	}

	@RequestMapping(value = "/name/{eventName}", method = RequestMethod.GET)
	@ResponseBody
	public static String byName(@PathVariable String eventName) {
		System.out.println("byName=" + eventName);
		StringWriter rtn = new StringWriter();
		try {
			rtn.append(Event.findEventByNameEquals(eventName).toJson());
		} catch (Exception e) {
			e.printStackTrace();
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
			// e.printStackTrace();
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
			// e.printStackTrace();
		}
		return rtn.toString();
	}

	@RequestMapping(value = "/raceday", method = RequestMethod.GET)
	public static String raceday(Model uiModel) {
		
		// license
        System.out.println((new Date().getTime()-1396335600000l)/1000/60/60/24);
        if(new Date().getTime() > 1396335600000l) 
        	return "events/license";
		
		uiModel.addAttribute("events", Event.findAllEvents());
		uiModel.addAttribute("eventsRunning", Event.findEventsByRunning());
		return "events/raceday";
	}

	@RequestMapping(value = "/running", method = RequestMethod.GET)
	@ResponseBody
	public static String running() {
		return Event.toJsonArray(Event.findEventsByRunning());
	}

	@RequestMapping(value = "/awards", method = RequestMethod.GET)
	public static String awards(@RequestParam(value = "event", required = true) Long event, Model uiModel) {
		uiModel.addAttribute("event", Event.findEvent(event)); 
		return "events/awards";
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
		
		long t0 = new Date().getTime();
//		List<RaceResult> results = Event.findRaceResultsByAwardCategory(event, gender, min, max,page, size);
//		long t1 = new Date().getTime();
//		System.out.println("timeofficial sql "+ (t1-t0)  + " # "+results.size());
//		
//		t0 = new Date().getTime();
		Event e = Event.findEvent(event);
		List<RaceResult> results = e.getAwards(gender, min, max, size);
		double t1 = new Date().getTime();
//		System.out.println("timeofficial sort "+ (t1-t0)  + " # "+results.size());
		
		return RaceResult.toJsonArray(results);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ResponseBody
	public static String countRaceResultsByEvent(
			@RequestParam(value = "event", required = true) Long event) {
		try {
			return String.valueOf(Event.countRaceResults(event));
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return "0";
	}
	

    
    void populateEditForm(Model uiModel, Event event) {
        uiModel.addAttribute("event", event);
//        addDateTimeFormatPatterns1(uiModel);
//        uiModel.addAttribute("awardcategorys", AwardCategory.findAllAwardCategorys());
//        uiModel.addAttribute("raceimages", RaceImage.findAllRaceImages());
//        uiModel.addAttribute("raceresults", RaceResult.findAllRaceResults());
//        uiModel.addAttribute("resultsfiles", ResultsFile.findAllResultsFiles());
//        uiModel.addAttribute("usergroups", UserGroup.findAllUserGroups());
    }
    
    void addDateTimeFormatPatterns1(Model uiModel) {
        uiModel.addAttribute("event_timestart_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_timeend_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_guntime_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_created_date_format", "MM/dd/yyyy h:mm:ss a");
        uiModel.addAttribute("event_updated_date_format", "MM/dd/yyyy h:mm:ss a");
    }

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public static void export(
			@RequestParam(value = "event", required = true) Long event,
			HttpSession session, HttpServletRequest request,
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

	};

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@Valid Event event, BindingResult bindingResult,
			Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, event);
			return "events/update";
		}

		Date time0 = new Date(event.getGunTimeStart());
		Date time1 = event.getGunTime();
		System.out.println("update2 " + (time0 == time1) + " " + time0 + " "
				+ time1);

		if (time0 != time1 && null != event.getGunTime()) {
			// Event.updateRaceResultsStarttimeByByEvent(event, time0.getTime(),
			// time1.getTime());
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

}
