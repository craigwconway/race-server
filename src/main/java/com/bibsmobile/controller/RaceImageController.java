package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import org.apache.commons.collections.CollectionUtils;
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
    		@RequestParam(value="bib",required=false) String bib) {
        RaceImage raceImage;
        if(null!=bib)
        	raceImage = new RaceImage(filePath,raceId,bib);
        else 
        	raceImage = new RaceImage(filePath,raceId);
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
