package com.bibsmobile.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
	private static final Logger log = LoggerFactory.getLogger(JSONUtil.class);

	public static String convertException(Exception e) {
		log.error("json error message", e);
		ErrorMessage m = new ErrorMessage(e.getMessage());
		ObjectMapper objMapper = new ObjectMapper();
		try {
			return objMapper.writeValueAsString(m);
		} catch (JsonProcessingException e1) {
			log.error("error converting exception to error message object", e1);
			return "{\"error\": \"error converting exception to error message object\"}";
		}
	}

	private static class ErrorMessage {
		private String error;

		public ErrorMessage(String error) {
			this.error = error;
		}

		@SuppressWarnings("unused")
		public String getError() {
			return this.error;
		}
	}
}
