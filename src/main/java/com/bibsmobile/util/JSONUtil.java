package com.bibsmobile.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
    private static final Logger log = LoggerFactory.getLogger(JSONUtil.class);
    private static final ObjectMapper objMapper = new ObjectMapper();

    /**
     * converts an object to a json object
     */
    public static String convertObject(Object o) {
        try {
            return objMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return JSONUtil.convertException(e);
        }
    }

    /**
     * converts a string to a json error message
     */
    public static String convertErrorMessage(String e) {
        ErrorMessage m = new ErrorMessage(e);
        try {
            return objMapper.writeValueAsString(m);
        } catch (JsonProcessingException e1) {
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
            Pagination<T> p = new Pagination<T>(start, count, results);
            return JSONUtil.convertObject(p);
        } catch (Exception e) {
            return JSONUtil.convertException(e);
        }
    }

    private static class ErrorMessage {
        private final String error;

        public ErrorMessage(String error) {
            this.error = error;
        }

        @SuppressWarnings("unused")
        public String getError() {
            return this.error;
        }
    }

    private static class Pagination<T> {
        private int start; // how many results skipped from start
        private int count; // how many results to return
        private final List<T> results; // results

        public Pagination(Integer start, Integer count, List<T> results) {
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

        @SuppressWarnings("unused")
        public int getTotal() {
            return this.results.size();
        }

        public int getStart() {
            return this.start;
        }

        public int getCount() {
            return this.count;
        }

        @SuppressWarnings("unused")
        public List<T> getResults() {
            return this.results.subList(this.getStart(), this.getStart() + this.getCount());
        }
    }
}
