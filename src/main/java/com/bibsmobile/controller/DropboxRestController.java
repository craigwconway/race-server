package com.bibsmobile.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.JSONUtil;
import com.bibsmobile.util.ResultsFileUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("/rest/dropbox")
@Controller
public class DropboxRestController {
    @RequestMapping(value = "/import", method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
    public @ResponseBody
    ResponseEntity<String> importFile(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        ResultsImport rimport = null;
        try {
            UserProfile user = UserProfileUtil.getLoggedInUserProfile();
            if (user == null)
                return new ResponseEntity<String>("{\"error\": \"not logged in\"}", HttpStatus.UNAUTHORIZED);
            DropboxImportJSON parsedJson = mapper.readValue(body, DropboxImportJSON.class);
            rimport = ResultsFileUtil.importDropbox(user, Event.findEvent(parsedJson.getEvent()), parsedJson.getDropboxPath(), parsedJson.getMap(), parsedJson.isSkipHeaders());
            if (rimport.getErrors() > 0)
                return new ResponseEntity<String>("{\"error\": \"" + rimport.getErrors() + " errors while importing\", \"errorRows\": \"" + rimport.getErrorRows() + "\"}",
                        HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>(JSONUtil.convertException(e), HttpStatus.OK);
        }
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    private static class DropboxImportJSON {
        private Long event;
        private String dropboxPath;
        private boolean skipHeaders;
        private List<String> map;

        public Long getEvent() {
            return this.event;
        }

        public String getDropboxPath() {
            return this.dropboxPath;
        }

        public boolean isSkipHeaders() {
            return this.skipHeaders;
        }

        public List<String> getMap() {
            return this.map;
        }
    }
}
