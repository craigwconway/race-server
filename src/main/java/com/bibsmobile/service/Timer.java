package com.bibsmobile.service;

import java.util.Map;

public interface Timer {
		
		void init();
		void cleanup();
		int getStatus();
		void connect();
		String getReaderURI();
		void setReaderURI(String uri);
		void disconnect();
		void start();
		void stop();
		Map <Integer,Long> getTimes();
		void writeTag(int num) throws Exception;
		long getTime() throws Exception;
}