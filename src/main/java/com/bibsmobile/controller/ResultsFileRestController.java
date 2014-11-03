package com.bibsmobile.controller;

import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.util.JSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@RequestMapping("/rest/resultsfiles")
@Controller
public class ResultsFileRestController {
    @RequestMapping(value = "/{id}", headers = "Accept=application/json", produces = "application/json")
    public ResponseEntity<String> details(@PathVariable("id") Long id) {
        ResultsFile rf = ResultsFile.findResultsFile(id);
        if (rf == null) return new ResponseEntity<>(JSONUtil.convertErrorMessage("not found"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(JSONUtil.convertObject(new ResultsFileDetails(rf)), HttpStatus.OK);
    }

    private static class ResultsFileDetails {
        private final Long id;
        private final String name;
        private final String contentType;
        private final Date created;
        private final long filesize;
        private final String sha1Checksum;
        private final Boolean automaticUpdates;
        private final ResultsFileImportDetails lastImport;

        private ResultsFileDetails(ResultsFile rf) {
            super();
            this.id = rf.getId();
            this.name = rf.getName();
            this.contentType = rf.getContentType();
            this.created = rf.getCreated();
            this.filesize = rf.getFilesize();
            this.sha1Checksum = rf.getSha1Checksum();
            this.automaticUpdates = rf.getAutomaticUpdates();
            this.lastImport = new ResultsFileImportDetails(rf);
        }
        public Long getId() { return this.id; }
        public String getName() { return this.name; }
        public String getContentType() { return this.contentType; }
        public Date getCreated() { return this.created; }
        public long getFilesize() { return this.filesize; }
        public String getSha1Checksum() { return this.sha1Checksum; }
        public Boolean getAutomaticUpdates() { return this.automaticUpdates; }
        public ResultsFileImportDetails getLastImport() { return this.lastImport; }
    }
    private static class ResultsFileImportDetails {
        private final Long id;
        private final Date runDate;
        private final int rowsProcessed;
        private final int numErrors;

        private ResultsFileImportDetails(ResultsFile rf) {
            super();
            ResultsImport ri = rf.getLatestImport();
            this.id = ri.getId();
            this.runDate = ri.getRunDate();
            this.rowsProcessed = ri.getRowsProcessed();
            this.numErrors = ri.getErrors();
        }
        public Long getId() { return this.id; }
        public int getRowsProcessed() { return this.rowsProcessed; }
        public int getNumRowsErrors() { return this.numErrors; }
        public int getNumRowsSuccessful() { return this.rowsProcessed - this.numErrors; }
        public Date getRunDate() { return this.runDate; }
    }
}
