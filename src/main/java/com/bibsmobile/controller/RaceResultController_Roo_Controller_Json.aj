// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.RaceResultController;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

privileged aspect RaceResultController_Roo_Controller_Json {
    
    @RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.showJson(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (raceResult == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(raceResult.toJson(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<RaceResult> result = RaceResult.findAllRaceResults();
        return new ResponseEntity<String>(RaceResult.toJsonArray(result), headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> RaceResultController.createFromJsonArray(@RequestBody String json) {
        for (RaceResult raceResult: RaceResult.fromJsonArrayToRaceResults(json)) {
            raceResult.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> RaceResultController.updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RaceResult raceResult = RaceResult.fromJsonToRaceResult(json);
        if (raceResult.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> RaceResultController.updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (RaceResult raceResult: RaceResult.fromJsonArrayToRaceResults(json)) {
            if (raceResult.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> RaceResultController.deleteFromJson(@PathVariable("id") Long id) {
        RaceResult raceResult = RaceResult.findRaceResult(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (raceResult == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        raceResult.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.jsonFindRaceResultsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEventAndBibEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.jsonFindRaceResultsByEventAndBibEquals(@RequestParam("event") Event event, @RequestParam("bib") String bib) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEventAndFirstnameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.jsonFindRaceResultsByEventAndFirstnameLike(@RequestParam("event") Event event, @RequestParam("firstname") String firstname) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLike(event, firstname).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEventAndFirstnameLikeAndLastnameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.jsonFindRaceResultsByEventAndFirstnameLikeAndLastnameLike(@RequestParam("event") Event event, @RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndFirstnameLikeAndLastnameLike(event, firstname, lastname).getResultList()), headers, HttpStatus.OK);
    }
    
    @RequestMapping(params = "find=ByEventAndLastnameLike", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> RaceResultController.jsonFindRaceResultsByEventAndLastnameLike(@RequestParam("event") Event event, @RequestParam("lastname") String lastname) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(RaceResult.toJsonArray(RaceResult.findRaceResultsByEventAndLastnameLike(event, lastname).getResultList()), headers, HttpStatus.OK);
    }
    
}
