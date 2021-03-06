package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.BibsLLRPTimer;
import com.bibsmobile.service.DummyTimer;
import com.bibsmobile.service.Timer;
import com.bibsmobile.util.BuildTypeUtil;

@RequestMapping("/timers")
@Controller
public class TimerConfigController {
	
	private HashMap<TimerConfig,Timer> timers = new HashMap<TimerConfig,Timer>();

	private synchronized Timer getTimer(long id){
		TimerConfig timerConfig = TimerConfig.findTimerConfig(id);
    	Timer timer = null;
    	if(timers.containsKey(timerConfig)){
    		System.out.println("Found cached timer!");
    		timer = timers.get(timerConfig);
    	}else{
    		if(timerConfig.getType() == 0){
    			timer = new DummyTimer();
    		}else if(timerConfig.getType() == 1){
    			timer = new BibsLLRPTimer(timerConfig);
    		}
    		timers.put(timerConfig, timer);
    		System.out.println("Put cached timer!");
    	}
		timer.setTimerConfig(timerConfig);
    	return timer;
	}
	

    @RequestMapping(value = "/bib-report/{id}", method = RequestMethod.GET)
	@ResponseBody
    public String bibReport(@PathVariable(value = "id") long id) {
        return getTimer(id).createReport();
    }

    @RequestMapping(value = "/clear-all-times", method = RequestMethod.GET)
    public String clearAllReports(
    		@RequestParam(value = "eventId", required = true) Long eventId ) {
    	for(Entry<TimerConfig, Timer> timerEntry : timers.entrySet()) {
    		TimerConfig tc = timerEntry.getKey();
    		System.out.println("timerconfig: " + tc.getId() + " pos: " + tc.getPosition() + " ip:" + tc.getUrl());
    		System.out.println("Clearing time for timer: " + timerEntry.getKey().getId() + ", Position: "+ timerEntry.getKey().getPosition());
    		// if timer is connected and a bibs timer, we need to poll it for all remaining tags:
    		if ((2 == timerEntry.getValue().getStatus()) && (1 == timerEntry.getKey().getType())) {
    			timerEntry.getValue().emptyBuffer();
    		}
    		timerEntry.getValue().clearAllTimesByEventAndTimerId(eventId, timerEntry.getKey().getPosition());
    	}
    	return StringUtils.EMPTY;
    }

    @RequestMapping(value = "/clear-finish-times", method = RequestMethod.GET)
    public String clearFinishReports(
    		@RequestParam(value = "eventId", required = true) Long eventId ) {
    	for(Entry<TimerConfig, Timer> timerEntry : timers.entrySet()) {
    		System.out.println("Clearing time for timer: " + timerEntry.getKey().getId() + ", Position: "+ timerEntry.getKey().getPosition());
    		if(timerEntry.getKey().getPosition() != 0) {
        		if ((2 == timerEntry.getValue().getStatus()) && (1 == timerEntry.getKey().getType())) {
        			timerEntry.getValue().emptyBuffer();
        		}
        		timerEntry.getValue().clearAllTimesByEventAndTimerId(eventId, timerEntry.getKey().getPosition());    			
    		}
    	}
    	return StringUtils.EMPTY;
    }    
    
    
    @RequestMapping(value = "/clear-bib-report/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String clearBibReport(
    		@PathVariable(value = "id") long id, 
    		@RequestParam(value = "eventId", required = true) Long eventId ) {
        getTimer(id).clearTimesByEvent(eventId);
        return StringUtils.EMPTY;
    }
	
	@RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String status(@PathVariable(value = "id") long id) {
		return getTimer(id).getStatus()+"";
	}
	
	@RequestMapping(value = "/connect/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String connect(@PathVariable(value = "id") long id){
    	Timer timer = getTimer(id);
    	try{
	    	if(timer.getStatus()==0)
	    		timer.connect();
			if(timer.getStatus()==3){ // write
	    		timer.disconnect(); 
				timer.connect();
			}
    	}catch(Exception e){
    		return "false";
    	}
        return "true";
    }
	
	@RequestMapping(value = "/disconnect/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String disconnect(@PathVariable(value = "id") long id){
    	if(getTimer(id).getStatus()>0)
    		getTimer(id).disconnect();
        return "true";
    }
    
    @RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String start(@PathVariable(value = "id") long id) {
    	Timer timer = getTimer(id);
    	try{
	    	if(timer.getStatus() == 0){
	    		timer.connect();
	    	}
	    	if(timer.getStatus() != 2){
	    		timer.startReader();
	    	}
    	}catch(Exception e){
    		return "false";
    	}
    	return "true";
    }
    
    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String stop(@PathVariable(value = "id") long id){
    	Timer timer = getTimer(id);
    	if(timer.getStatus() > 0) {
	    	timer.stopReader();
	    	timer.disconnect();
    	}
        return "true";
    }
    
    @RequestMapping(value = "/write/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String write(@PathVariable(value = "id") long id){
    	// get first timer
    	Timer timer = getTimer(TimerConfig.findTimerConfigEntries(0, 1).get(0).getId()); // use timer 1 TODO ?
    	try{
	    	if(timer.getStatus()==0)
	    		timer.connect();
	    	else if(timer.getStatus()==2)
	    		timer.stopReader();
    	}catch(Exception e){
    		return "false";
    	}
    	
    	 // mod writes for current usergroup 
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile user = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
     /*   System.out.println("writes group "+user.getUserGroup().getName());
        
        // TODO license check
        
        int writes =  user.getUserGroup().getBibWrites();
        if(writes < 1) return "none";
    	try{
    		timer.write(id); 
    	}catch(Exception e){
            System.out.println("writes error "+e.getMessage());
    		if(e.getMessage().equals("No tags present")){
        		return "notags";
    		}else if(e.getMessage().equals("Too many tags present")){
    	        return "manytags";
    		}else{
    	        return "false";
    		}
    	}
        user.getUserGroup().setBibWrites(writes-1);
        user.getUserGroup().merge();
        */
        return "true";
    } 

    @RequestMapping(value = "/time/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String time(@PathVariable(value = "id") long id) {
    	long server = new Date().getTime();
    	long timer = 0;
    	try{
	    	if(getTimer(id).getStatus() == 0){
	    		getTimer(id).connect();
	    	}
    	}catch(Exception e){
    		return "true"; // reader not connected
    	}
    	timer = getTimer(id).getDateTime();
    	if(server - timer > 1000){ // 1 second
    		System.out.println("Timer/Server "+timer+"/"+server);
    		return "false";
    	}
        return "true";
    }
    

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        TimerConfig timerConfig = TimerConfig.findTimerConfig(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (timerConfig == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(timerConfig.toJson(), headers, HttpStatus.OK);
    }

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<TimerConfig> result = TimerConfig.findAllTimerConfigs();
        return new ResponseEntity<String>(TimerConfig.toJsonArray(result), headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        TimerConfig timerConfig = TimerConfig.fromJsonToTimerConfig(json);
        timerConfig.setConnectionTimeout(10);
        timerConfig.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = (RequestMapping) getClass().getAnnotation(RequestMapping.class);
        headers.add("Location",uriBuilder.path(a.value()[0]+"/"+timerConfig.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (TimerConfig timerConfig: TimerConfig.fromJsonArrayToTimerConfigs(json)) {
            timerConfig.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TimerConfig timerConfig = TimerConfig.fromJsonToTimerConfig(json);
        timerConfig.setId(id);
        timerConfig.setConnectionTimeout(10);
        if (timerConfig.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        TimerConfig timerConfig = TimerConfig.findTimerConfig(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (timerConfig == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        timerConfig.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid TimerConfig timerConfig, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, timerConfig);
            uiModel.addAttribute("build", BuildTypeUtil.getBuild());
            return "timers/create";
        }
        uiModel.asMap().clear();
        timerConfig.setConnectionTimeout(10);
        timerConfig.persist();
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/timers/" + encodeUrlPathSegment(timerConfig.getId().toString(), httpServletRequest);
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel, HttpServletRequest httpServletRequest) {
		TimerConfig tc = new TimerConfig();
		tc.setFilename("/properties/ThingMagic/ADD_ROSPEC.xml");
		tc.setType(1);
		tc.setPorts("1");
		tc.setPosition(1);
		tc.setReadTimeout(20);
		tc.setConnectionTimeout(10);
		tc.persist();
		tc.flush();
        populateEditForm(uiModel, new TimerConfig());
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/timers/" + encodeUrlPathSegment(tc.getId().toString(), httpServletRequest);
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        //uiModel.addAttribute("timerconfig", TimerConfig.findTimerConfig(id));
        //uiModel.addAttribute("itemId", id);
        //uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/timers/" + id + "?form";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("timerconfigs", TimerConfig.findTimerConfigEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) TimerConfig.countTimerConfigs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("timerconfigs", TimerConfig.findAllTimerConfigs(sortFieldName, sortOrder));
        }
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "timers/list";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid TimerConfig timerConfig, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, timerConfig);
            uiModel.addAttribute("build", BuildTypeUtil.getBuild());
            return "timers/update";
        }
        uiModel.asMap().clear();
        timerConfig.setConnectionTimeout(10);
        Integer oldType = TimerConfig.findTimerConfig(timerConfig.getId()).getType();
        timerConfig.merge();
        if(timerConfig.getType() == 0) {
            timers.put(timerConfig, new DummyTimer());
        } else if(timerConfig.getType() == 1) {
        	timers.put(timerConfig, new BibsLLRPTimer(timerConfig));
        }
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/events/raceday";
    }

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, TimerConfig.findTimerConfig(id));
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "timers/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        TimerConfig timerConfig = TimerConfig.findTimerConfig(id);
        timerConfig.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/timers";
    }

	void populateEditForm(Model uiModel, TimerConfig timerConfig) {
        uiModel.addAttribute("timerConfig", timerConfig);
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
	
	void internalClear(long eventId) {
    	for(Entry<TimerConfig, Timer> timerEntry : timers.entrySet()) {
    		System.out.println("Clearing time for timer: " + timerEntry.getKey().getId() + ", Position: "+ timerEntry.getKey().getPosition());
    		// if timer is connected and a bibs timer, we need to poll it for all remaining tags:
    		if ((2 == timerEntry.getValue().getStatus()) && (1 == timerEntry.getKey().getType())) {
    			timerEntry.getValue().emptyBuffer();
    		}
    		timerEntry.getValue().clearAllTimesByEventAndTimerId(eventId, timerEntry.getKey().getPosition());
    	}
		
	}
}
