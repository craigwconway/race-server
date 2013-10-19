package com.bibsmobile.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
	
	@Autowired // see applicationContext.xml
	private Timer timer;
	
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public String readerTimeStart(){
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
    public String readerTimeStop(){
    	String rtn = "false";
        try {
        	timer.stop();
        	rtn = "true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    private List<Integer> bibs = new ArrayList<Integer>();
    List<RaceResult> runners = new ArrayList<RaceResult>();
    
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    @ResponseBody
    public String readerQuery(@RequestParam(value = "event", required = true) int event){
        try {
        	Map <Integer,Long> bibtime = timer.getTimes();
        	boolean newBibs = false;
        	for(Integer bib:bibtime.keySet()){
    			if(bibs.contains(bib)) continue;
        		System.out.println("found "+bib+" "+bibtime.get(bib).toString());
    			newBibs = true;
    			bibs.add(bib);
        		RaceResult result = new RaceResult();
        		boolean found = false;
    			try{
    				result = RaceResult.findRaceResultsByEventAndBibEquals(
    							Event.findEvent(Long.valueOf(event).longValue()), bib.toString())
    							.getSingleResult();
    				found = true;
    			}catch(Exception e){
    				// no runner assigned to bib
    				// e.printStackTrace();
    			}
        		result.setBib(bib.toString());
        		result.setTimeoverall(	bibtime.get(bib).toString());
        		if (found) result.merge();
        		else result.persist();
        		runners.add(result);
        	}
        	if(newBibs) return RaceResult.toJsonArray(runners);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
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
      List<RaceResult> results = RaceResult.findRaceResultsByEvent(event,1,10000).getResultList();
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
