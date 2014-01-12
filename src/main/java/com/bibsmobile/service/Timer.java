package com.bibsmobile.service;

import com.bibsmobile.model.TimerConfig;

public interface Timer {
	
	void setTimerConfig(TimerConfig timerConfig);
	
	int getStatus();
	
	long getDateTime();

	void connect();

	void disconnect();

	void start();

	void stop();

	void write(long bib) throws Exception;
}