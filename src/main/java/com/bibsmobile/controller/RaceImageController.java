package com.bibsmobile.controller;

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
	
}
