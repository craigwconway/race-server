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

	void write(long bib) throws Exception;

	void logTime(int bibnum, long bibtime, TimerConfig timerConfig);
}