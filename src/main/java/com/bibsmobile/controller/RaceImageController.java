package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.RaceImage;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/raceimages")
@Controller
@RooWebScaffold(path = "raceimages", formBackingObject = RaceImage.class)
@RooWebJson(jsonObject = RaceImage.class)
public class RaceImageController {
	
	@RequestMapping(value="/api", method = RequestMethod.GET)
    public ResponseEntity<String> api(
    		@RequestParam(value="filePath") String filePath, 
    		@RequestParam(value="raceId") long raceId,
    		@RequestParam(value="bib",required=false) String[] bibs) {
        RaceImage raceImage;
               
        try {
        	/* Validate <code>bibs</code> argument, throwing <code>IllegalArgumentException</code>
        	 * if the argument array is null or has null elements.
        	 */
        	Validate.noNullElements(bibs);
        	
        	if (bibs.length == 0) { // Empty Array! no parameter for bib has been passed
        		raceImage = new RaceImage(filePath,raceId);
        	} else if (bibs.length == 1) { // only one parameter for bib has been passed
        		raceImage = new RaceImage(filePath,raceId,bibs[0]);        		
			} else {
				raceImage = new RaceImage(filePath,raceId,bibs);				
			}        	
        } catch (IllegalArgumentException e) {
        	raceImage = new RaceImage(filePath,raceId);        	
        }        
        raceImage.persist();
        
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/search", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceImagesByEventId(@RequestParam Long eventId, @RequestParam(required = false) String bib, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        if (event != null) {
            List<RaceImage> raceImages = null;
            if (bib != null) {
                List<RaceResult> raceResults = RaceResult.findRaceResultsByEvent(event).getResultList();
                if (CollectionUtils.isNotEmpty(raceResults)) {
                    raceImages = RaceImage.findRaceImagesByRaceResults(raceResults).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
                }
            } else {
                raceImages = RaceImage.findRaceImagesByEvent(event).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
            }
            if (CollectionUtils.isNotEmpty(raceImages)) {
                return new ResponseEntity<>(RaceImage.toJsonArray(raceImages),headers, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
	
}
