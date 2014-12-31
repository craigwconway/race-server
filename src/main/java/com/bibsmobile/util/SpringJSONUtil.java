package com.bibsmobile.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public final class SpringJSONUtil {
    private static final HttpHeaders responseHeaders = new HttpHeaders();

    static {
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    private SpringJSONUtil() {
        super();
    }

    public static ResponseEntity<String> returnObject(Object o, HttpStatus httpStatus) {
        return new ResponseEntity<>(JSONUtil.convertObject(o), responseHeaders, httpStatus);
    }

    public static <T> ResponseEntity<String> returnPaginated(Integer start, Integer count, List<T> results, HttpStatus httpStatus) {
        return new ResponseEntity<>(JSONUtil.convertPaginated(start, count, results), responseHeaders, httpStatus);
    }

    public static ResponseEntity<String> returnStatusMessage(String status, HttpStatus httpStatus) {
        return new ResponseEntity<>(JSONUtil.convertStatusMessage(status), responseHeaders, httpStatus);
    }

    public static ResponseEntity<String> returnErrorMessage(String status, HttpStatus httpStatus) {
        return new ResponseEntity<>(JSONUtil.convertErrorMessage(status), responseHeaders, httpStatus);
    }

    public static ResponseEntity<String> returnException(Exception e, HttpStatus httpStatus) {
        return new ResponseEntity<>(JSONUtil.convertException(e), responseHeaders, httpStatus);
    }
}
