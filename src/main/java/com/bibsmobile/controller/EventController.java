package com.bibsmobile.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.service.Timer;

@RequestMapping("/events")
@Controller
@RooWebScaffold(path = "events", formBackingObject = Event.class)
@RooWebJson(jsonObject = Event.class)
public class EventController {

	 
    @RequestMapping(value = "/gun", method = RequestMethod.GET)
    @ResponseBody
    public String timerGun(@RequestParam(value = "event", required = true) long event){
    	try{
        	Event e = Event.findEvent(event);
        	e.setGunFired(true);
    		e.setGunTime(new Date());
    		e.merge();
    	}catch(Exception x){
    		x.printStackTrace();
    		return "false";
    	}
        return "true";
    }
	
    @RequestMapping(value = "/run", method = RequestMethod.GET)
    @ResponseBody
    public static String run(@RequestParam(value = "event", required = true) long event,
    		@RequestParam(value = "order", required = false, defaultValue = "1") int order){
    	try{
        	Event e = Event.findEvent(event);
    		e.setRunning(order);
    		e.merge();
    	}catch(Exception x){
    		x.printStackTrace();
    		return "false";
    	}
        return "true";
    }
    
    @RequestMapping(value = "/done", method = RequestMethod.GET)
    @ResponseBody
    public static String timerDone(@RequestParam(value = "event", required = true) long event){
		System.out.println("event done "+event);
    	try{
        	Event e = Event.findEvent(event);
    		e.setRunning(0);
    		e.merge();
    	}catch(Exception x){
    		x.printStackTrace();
    		return "false";
    	}
        return "true";
    }
	
    
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    @ResponseBody
    public String resultsQuery(@RequestParam(value = "event", required = true) long event_id){ 
        try {
        	return RaceResult.toJsonArray(
        			Event.findRaceResultsForAnnouncer(event_id, 1, 15));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }
    
    @RequestMapping(value = "/featured", method = RequestMethod.GET)
    @ResponseBody
    public static String featured(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(Event.toJsonArray(Event.findEventsByFeaturedGreaterThan(0, page, size).getResultList()));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/byname", method = RequestMethod.GET)
    @ResponseBody
    public static String byName() {
        StringBuffer rtn = new StringBuffer("[");
        boolean first = true;
        for (Event event : Event.findAllEvents()) {
            if (!first) rtn.append(",");
            first = false;
            rtn.append("{");
            rtn.append("\"id\":" + event.getId());
            rtn.append(",\"name\":\"" + event.getName() + "\"");
            rtn.append(",\"featured\":" + event.getFeatured());
            rtn.append("}");
        }
        return rtn.append("]").toString();
    }

    @RequestMapping(value = "/byname/{eventName}", method = RequestMethod.GET)
    @ResponseBody
    public static String byName(@PathVariable String eventName, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(Event.toJsonArray(Event.findEventsByNameLike(eventName, page, size).getResultList()));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/future", method = RequestMethod.GET)
    @ResponseBody
    public static String future(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size, Integer featured) {
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
            //e.printStackTrace();
        }
        return rtn.toString();
    }

    @RequestMapping(value = "/past", method = RequestMethod.GET)
    @ResponseBody
    public static String past(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        StringBuffer rtn = new StringBuffer();
        Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
        try {
            rtn.append(Event.toJsonArray(Event
            	.findEventsByTimeStartLessThan(
            		today.getTime(), page, size).getResultList()));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }
    
    @RequestMapping(value = "/raceday", method = RequestMethod.GET)
    public static String raceday(Model uiModel){
    	uiModel.addAttribute("events",Event.findAllEvents());
    	uiModel.addAttribute("eventsRunning",Event.findEventsByRunning());
    	return "events/raceday";
    }    
    
    @RequestMapping(value = "/running", method = RequestMethod.GET)
    @ResponseBody
    public static String running(){
    	return Event.toJsonArray(Event.findEventsByRunning());
    }
    
    @RequestMapping(value = "/awards", method = RequestMethod.GET)
    public static String awards(){
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
        StringBuffer rtn = new StringBuffer();
        try {
            rtn.append(RaceResult.toJsonArray(Event.findRaceResultsByAwardCategory(event,gender, min, max, page,size)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn.toString();
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

    @RequestMapping(value="/export", method = RequestMethod.GET)
    public void export(@RequestParam(value = "event", required = true) Long event,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
        Event _event = Event.findEvent(event);
        response.setContentType("text/csv;charset=utf-8"); 
        response.setHeader("Content-Disposition","attachment; filename=\""+_event.getName()+".csv\"");
        OutputStream resOs= response.getOutputStream();  
        OutputStream buffOs= new BufferedOutputStream(resOs);   
        OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);  

        List<RaceResult> runners = Event.findRaceResults(event, 0, 99999);
        for(RaceResult r:runners){              
        	outputwriter.write(r.getBib()+","+r.getFirstname()+","+r.getLastname()+","+r.getCity()+
        			","+r.getState()+","+r.getTimeofficialdisplay()+","+r.getGender()+","+r.getAge()+"\r\n");  
        }     
        outputwriter.flush();   
        outputwriter.close();

    };
    

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Event event, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, event);
            return "events/update";
        }
        
        Date time0 = new Date(event.getGunTimeStart());
        Date time1 = event.getGunTime();
        System.out.println("update2 "+(time0==time1)+" "+time0 + " "+time1);
        
        if(time0!=time1){
        	//Event.updateRaceResultsStarttimeByByEvent(event, time0.getTime(), time1.getTime());
        	for(RaceResult r : RaceResult.findRaceResultsByEvent(event).getResultList()){
        		r.setTimestart(time1.getTime());
        		r.merge();
        	}
        	event.setGunTimeStart(event.getGunTime().getTime());
        }
        
        uiModel.asMap().clear();
        event.merge();
        return "redirect:/events/" + encodeUrlPathSegment(event.getId().toString(), httpServletRequest);
    }

}
