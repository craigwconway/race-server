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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	@Autowired // inject see applicationContext.xml
	private Timer timer;

    @RequestMapping(value = "/timer/connect", method = RequestMethod.GET)
    @ResponseBody
    public String timerConnect(){
    	String rtn = "false";
    	try{
    		timer.init();
    		if(timer.getStatus()==1)
    			rtn = "true";
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return rtn;
    }
    @RequestMapping(value = "/timer/reconnect", method = RequestMethod.GET)
    @ResponseBody
    public String timerReconnect(){
    	String rtn = "false";
    	try{
    		timer.disconnect();
    		timer.init();
    		if(timer.getStatus()==1)
    			rtn = "true";
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return rtn;
    }

    @RequestMapping(value = "/timer/status", method = RequestMethod.GET)
    @ResponseBody
    public String timerStatus(){
		System.out.println("timer status "+timer.toString());
		final String rtn = Integer.valueOf(timer.getStatus()).toString();
		System.out.println("timer status returned "+rtn);
    	return rtn;
    }

    @RequestMapping(value = "/timer/purge", method = RequestMethod.GET)
    @ResponseBody
    public String timerPurge(){
		System.out.println("timer Purge");
    	try{
    	    timer.purge();
    	}catch(Exception x){
    		x.printStackTrace();
    		return "false";
    	}
        return "true";
    }
	
    @RequestMapping(value = "/gun", method = RequestMethod.GET)
    @ResponseBody
    public String timerGun(@RequestParam(value = "event", required = true) long event){
    	try{
        	Event e = Event.findEvent(event);
        	e.setGunFired(true);
    		e.setTimerStart(timer.getTime());
    		e.merge();
    		System.out.println("event time "+e.getTimerStart());
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
		System.out.println("event run "+event);
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
        	e.setGunFired(false);
    		e.merge();
    	}catch(Exception x){
    		x.printStackTrace();
    		return "false";
    	}
        return "true";
    }
	
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public String timerStart(){
    	String rtn = "false";
        try {
        	timer.start();
        	rtn = "true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }
    
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    @ResponseBody
    public String timerStop(){
    	String rtn = "false";
        try {
        	timer.stop();
        	rtn = "true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }
    
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    @ResponseBody
    public synchronized String timerQuery(@RequestParam(value = "event", required = true) int event_id){ 
        try {
        	List<RaceResult> runners = new ArrayList<RaceResult>();
        	Map <Integer,Long> bibtime = timer.getTimes(); // only bibs not yet returned
        	if(bibtime.isEmpty())
        		System.out.println("No times returned from reader");
        	for(Integer bib:bibtime.keySet()){
        		System.out.println("Reader: "+bib+" : "+bibtime.get(bib));
        		Event event = Event.findEvent(Long.valueOf(event_id).longValue());
    			if(null==event) break;
    			// make sure we have an event start time
    			if(null==event.getTimerStart()){ // no gun
    				if(null!=event.getTimeStart()) // set timer to event start
    					event.setTimerStart(event.getTimeStart().getTime());
    				else event.setTimerStart(new Date().getTime()); // now
    				event.merge();
    			}
        		System.out.println("found "+bib+" "+bibtime.get(bib).toString());
        		RaceResult result = new RaceResult();
        		boolean found = false;
    			try{
    				result = RaceResult.findRaceResultsByEventAndBibEquals(
    							event, bib.toString())
    							.getSingleResult();
    				found = true;
    			}catch(Exception e){
            		System.out.println("No Runner found for "+bib+" in "+event.getName());
    				// no runner assigned to bib
    				// e.printStackTrace();
    			}
        		result.setBib(bib.toString());
        		result.setEvent(event);
        		long timerTime = bibtime.get(bib);
        		long startTime = event.getTimerStart();
        		result.setTimeofficial( getHoursMinutesSeconds(timerTime-startTime)	);
        		if (found) result.merge();
        		//else result.persist();
        		runners.add(result);
        	}
        	return RaceResult.toJsonArray(runners);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }
    
    public static String getHoursMinutesSeconds(long l) {
    	String rtn = "";
    	l=Math.abs(l);
		int hours = (int) ((l / 3600000) );
		System.out.println("Hours:" + hours);
		//l = l % 3600000;
		int minutes = (int) ((l / 60000) % 60 );
		System.out.println("Minutes: " + minutes);
		//l = l % 60000;
		int seconds =  (int) ((l/1000) % 60);
		System.out.println("Seconds " + seconds);
		int millis = (int) (l%100);
    	if(hours>0 && hours <=9) rtn = "0"+hours;
    	else if (hours > 9) rtn = hours +":";
    	else if (hours == 0) rtn = "00:";
    	if(minutes>0 && minutes <=9) rtn = rtn + "0"+minutes;
    	else if(minutes > 9) rtn = rtn + ""+minutes;
    	else if (minutes == 0) rtn = rtn + "00";
    	if(seconds>0 && seconds <=9) rtn = rtn + ":0"+seconds;
    	else if(seconds > 9) rtn = rtn + ":"+seconds;
    	rtn = rtn + "."+millis;
		return rtn;
	}

	@RequestMapping(value = "/write", method = RequestMethod.GET)
    @ResponseBody
    public String writeBib(@RequestParam(value = "bib", required = true) String bib){
        try {
        	timer.writeTag(Integer.valueOf(bib));
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return "true";
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
    public static Long countRaceResultsByEvent(
    		@RequestParam(value = "event", required = true) Long event) {
        try {
        	return Event.countRaceResults(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1l;
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
        	outputwriter.write(r.getBib()+","+r.getFirstname()+","+r.getLastname()+","+","+r.getCity()+","+r.getState()+","+r.getTimeofficial()+"\r\n");  
        }     
        outputwriter.flush();   
        outputwriter.close();

    };
    
}
