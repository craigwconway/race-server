package com.bibsmobile.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public class DummyTimer extends AbstractTimer implements Timer, Runnable {

	private TimerConfig timerConfig;
	private int status;
	private Thread thread;
	private final String log = getClass().getName() + " "
			+ Thread.currentThread().getName();
	private HashMap<Integer, Long> bibTimes = new HashMap<Integer, Long>();

	@Override
	public long getDateTime() {
		return new Date().getTime();
	}

	@Override
	public void startReader() {
		System.out.println(log + " Start Reading...");
		thread = new Thread(this);
		thread.start();
		status = 2;
		System.out.println(log + " Started.");
	}

	@Override
	public void stopReader() {
		System.out.println(log + " Stop Reading...");
		status = 1;
		notify();
		System.out.println(log + " Stopped.");
	}

	@Override
	public void write(long bib) throws Exception {
		status = 3;
		System.out.println(log + " Started Writing...");
	}

	@Override
	public void connect() {
		System.out.println(log + " Connecting...");
		status = 1;
		System.out.println(log + " Connected.");
	}

	@Override
	public void disconnect() {
		System.out.println(log + " Disconnecting...");
		status = 0;
		try {
			notify();
			thread = null;
		} catch (Exception e) {
			System.out.println(log + " Error Discconnecting.");
		}
		System.out.println(log + " Discconnected.");

	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setTimerConfig(TimerConfig timerConfig) {
		this.timerConfig = timerConfig;
	}

	@Override
	public void run() {
		System.out.println(log + " run...");
		bibTimes.clear();
		while (status == 2) {
			int bibnum = new Random().nextInt(30);
			long bibtime = new Date().getTime();
			
			// yay
            logTime(bibnum, bibtime, timerConfig);
			
			// wait or conceed
			try {
				Thread.sleep(1000);
				System.out.println(log + " Again thread "
						+ Thread.currentThread().getName());
			} catch (InterruptedException e) {
			}
		}
	}
}