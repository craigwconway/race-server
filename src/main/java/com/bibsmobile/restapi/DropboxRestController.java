package com.bibsmobile.restapi;

import java.util.List;

import com.bibsmobile.util.SpringJSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.util.ResultsFileUtil;
import com.bibsmobile.util.UserProfileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("/rest/dropbox")
@Controller
public class DropboxRestController {
    @ResponseBody
    @RequestMapping(value = "/import", method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
    public ResponseEntity<String> importFile(@RequestBody String body) {
        ObjectMapper mapper = new ObjectMapper();
        ResultsImport rimport = null;
        try {
            UserProfile user = UserProfileUtil.getLoggedInUserProfile();
            if (user == null)
                return SpringJSONUtil.returnErrorMessage("not logged in", HttpStatus.UNAUTHORIZED);
            DropboxImportJSON parsedJson = mapper.readValue(body, DropboxImportJSON.class);
            EventType eventType = EventType.findEventType(parsedJson.getEventType());
            Event event = eventType.getEvent();
            rimport = ResultsFileUtil.importDropbox(user, event, eventType, parsedJson.getDropboxPath(), parsedJson.getMap(), parsedJson.isSkipHeaders());
            if (rimport.getErrors() > 0)
                return SpringJSONUtil.returnErrorMessage(rimport.getErrors() + " errors while importing", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return SpringJSONUtil.returnException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return SpringJSONUtil.returnStatusMessage("success", HttpStatus.OK);
    }

    private static class DropboxImportJSON {
        private final Long eventType;
        private final String dropboxPath;
        private final boolean skipHeaders;
        private final List<String> map;

        private DropboxImportJSON(Long eventType, String dropboxPath, boolean skipHeaders, List<String> map) {
            super();
            this.eventType = eventType;
            this.dropboxPath = dropboxPath;
            this.skipHeaders = skipHeaders;
            this.map = map;
        }

        public Long getEventType() {
            return this.eventType;
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
