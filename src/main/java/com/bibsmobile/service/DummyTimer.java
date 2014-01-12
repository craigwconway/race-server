package com.bibsmobile.service;

import java.util.Date;

import com.bibsmobile.model.TimerConfig;

public class DummyTimer implements Timer {
	
	private TimerConfig timerConfig;
	private int status;
	
	@Override
	public long getDateTime() {
		return new Date().getTime();
	}

	@Override
	public void start() {
		status = 2;
		System.out.println(this.getClass().getName()+":"+Thread.currentThread().getName()+" Starting Reading...");
	}

	@Override
	public void stop() {
		status = 1;
		System.out.println(this.getClass().getName()+":"+Thread.currentThread().getName()+" Stopped Reading.");
	}

	@Override
	public void write(long bib) throws Exception {
		System.out.println(this.getClass().getName()+":"+Thread.currentThread().getName()+" Started Writing...");
		status = 3;
	}

	@Override
	public void connect() {
		status = 1;
		System.out.println(this.getClass().getName()+" Connected.");
	}

	@Override
	public void disconnect() {
		status = 0;
		System.out.println(this.getClass().getName()+" Connected.");
		
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setTimerConfig(TimerConfig timerConfig) {
		this.timerConfig = timerConfig;
	}
}