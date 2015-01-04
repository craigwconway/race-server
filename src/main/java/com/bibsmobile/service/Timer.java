package com.bibsmobile.service;

import com.bibsmobile.model.TimerConfig;

public interface Timer {
	
	void setTimerConfig(TimerConfig timerConfig);
	
	int getStatus();
	
	long getDateTime();

	void connect() throws Exception;

	void disconnect();

	void startReader();

	void stopReader();
	
	void emptyBuffer();

	void write(long bib) throws Exception;

	void logTime(long bibnum, long bibtime, TimerConfig timerConfig);

	void clearTimesByEvent(long eventId);
	
	void clearAllTimesByEventAndTimerId(long eventId, int position);
	
	String createReport();
}