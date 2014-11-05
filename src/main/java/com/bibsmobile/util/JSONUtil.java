package com.bibsmobile.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JSONUtil {
    private static final Logger log = LoggerFactory.getLogger(JSONUtil.class);
    private static final ObjectMapper objMapper = new ObjectMapper();

    private JSONUtil() {
        super();
    }

    /**
     * converts an object to a json object
     */
    public static String convertObject(Object o) {
        try {
            return objMapper.writeValueAsString(o);
        } catch (Exception e) {
            return JSONUtil.convertException(e);
        }
    }

    /**
     * converts a string to a json status message
     */
    public static String convertStatusMessage(String s) {
        StatusMessage m = new StatusMessage(s);
        try {
            return objMapper.writeValueAsString(m);
        } catch (Exception e1) {
            log.error("error converting exception to error message object", e1);
            return "{\"error\": \"error converting exception to error message object\"}";
        }
    }

    /**
     * converts a string to a json error message
     */
    public static String convertErrorMessage(String e) {
        ErrorMessage m = new ErrorMessage(e);
        try {
            return objMapper.writeValueAsString(m);
        } catch (Exception e1) {
            log.error("error converting exception to error message object", e1);
            return "{\"error\": \"error converting exception to error message object\"}";
        }
    }

    /**
     * converts an exception to a json error message
     */
    public static String convertException(Exception e) {
        log.error("json error message", e);
        return JSONUtil.convertErrorMessage(e.getMessage());
    }

    /**
     * converts a list of results to a paginated json object
     */
    public static <T> String convertPaginated(Integer start, Integer count, List<T> results) {
        try {
            Pagination<T> p = new Pagination<>(start, count, results);
            return JSONUtil.convertObject(p);
        } catch (Exception e) {
            return JSONUtil.convertException(e);
        }
    }

    private static class StatusMessage {
        private final String status;

        protected StatusMessage(String status) {
            super();
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }
    }

    private static class ErrorMessage {
        private final String error;

        protected ErrorMessage(String error) {
            super();
            this.error = error;
        }

        public String getError() {
            return this.error;
        }
    }

    private static class Pagination<T> {
        private final int start; // how many results skipped from start
        private final int count; // how many results to return
        private final List<T> results; // results

        protected Pagination(Integer start, Integer count, List<T> results) {
            super();
            if (start == null)
                this.start = 0;
            else
                this.start = start;
            if (count == null || count > results.size())
                this.count = results.size();
            else
                this.count = count;
            this.results = results;
        }

        public int getTotal() {
            return this.results.size();
        }

        public int getStart() {
            return this.start;
        }

        public int getCount() {
            return this.count;
        }

        public List<T> getResults() {
            return this.results.subList(this.getStart(), this.getStart() + this.getCount());
        }
    }
}
