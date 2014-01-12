package com.bibsmobile.service;

import com.bibsmobile.model.TimerConfig;

public interface Timer {
	
	void setTimerConfig(TimerConfig timerConfig);
	
	int getStatus();
	
	long getDateTime();

	void connect();

	void disconnect();

	void startReader();

	void stopReader();

	void write(long bib) throws Exception;
}