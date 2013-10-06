package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.RaceTimer;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/events")
@Controller
@RooWebScaffold(path = "events", formBackingObject = Event.class)
@RooWebJson(jsonObject = Event.class)
public class EventController {
	RaceTimer manager = new RaceTimer();
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public String readerTimeStart(){
    	String rtn = "Timer started (Galen)"+new Date().getTime();
        try {
        	// galens api call here
        	if(manager.getStatus() < 1)
        		manager.connect();
        	//if(manager.getStatus()!=2)
        		//manager.start();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }
    
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    @ResponseBody
    public String readerTimeStop(){
    	String rtn = "Timer stopped (Galen)"+new Date().getTime();
        try {
        	// galens api call here
        	if(manager.getStatus()==2)
        		manager.stop();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }
    
    @RequestMapping(value = "/pstart", method = RequestMethod.GET)
    @ResponseBody
    public String readerProgramStart(){
    	String rtn = "Program started (Galen)"+new Date().getTime();
        try {
        	// galens api call here
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }

    
    @RequestMapping(value = "/pstop", method = RequestMethod.GET)
    @ResponseBody
    public String readerProgramStop(){
    	String rtn = "Program stopped (Galen)"+new Date().getTime();
        try {
        	// galens api call here
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn.toString();
    }
    
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    @ResponseBody
    public String readerQuery(){
    	List<RaceResult> results = new ArrayList<RaceResult>();
        String rtn = "reader results query "+new Date().getTime();
        try {
        	// galens api call here
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rtn;
    }


    @RequestMapping(value = "/featured", method = RequestMethod.GET)
    @ResponseBody
    public String featured(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
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
    public String byName() {
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
    public String byName(@PathVariable String eventName, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
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
    public String future(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size, Integer featured) {
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
    public String past(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
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
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Event event = Event.findEvent(id);
        
        // remove relationships
        List<ResultsFile> files = ResultsFile.findResultsFilesByEvent(event).getResultList();
        System.out.println("files "+files.size());
        for(ResultsFile file:files){
            System.out.println("delete file "+file.getId());
        	List<ResultsImport> imports = ResultsImport.findResultsImportsByResultsFile(file).getResultList();
            System.out.println("imports "+imports.size());
        	for(ResultsImport import_:imports){
                System.out.println("delete import "+import_.getId());
        		import_.remove(); 
        	}
        	List<ResultsFileMapping> maps = ResultsFileMapping.findResultsFileMappingsByResultsFile(file).getResultList();
            System.out.println("maps "+maps.size());
        	for(ResultsFileMapping map:maps){
                System.out.println("delete map "+map.getId());
        		map.remove();
        	}
        	file.remove();
        } 
      List<RaceImage> images = RaceImage.findRaceImagesByEvent(event).getResultList();
      for(RaceImage image:images){ 
          System.out.println("delete image "+image.getId());
      	image.remove();
      }
      List<RaceResult> results = RaceResult.findRaceResultsByEvent(event).getResultList();
      System.out.println("results "+results.size());
      for(RaceResult result:results){ 
          //System.out.println("delete result "+result.getId());
      	result.remove();
      }
        // remove event
        System.out.println("clear event (not delete) "+event.toString());
        //event.remove();

        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/events";
    }
}
