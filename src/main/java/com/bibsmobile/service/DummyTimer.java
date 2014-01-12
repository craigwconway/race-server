package com.bibsmobile.service;

import java.util.Date;
import java.util.Random;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public class DummyTimer implements Timer, Runnable {
	
	private TimerConfig timerConfig;
	private int status;
	private Thread thread;
	
	@Override
	public long getDateTime() {
		return new Date().getTime();
	}

	@Override
	public void startReader() {
			thread = new Thread(this);
			thread.start();
		status = 2;
		notify();
		System.out.println(this.getClass().getName()+":"+Thread.currentThread().getName()+" Starting Reading...");
	}

	@Override
	public void stopReader() {
		status = 1;
		notify();
		System.out.println(this.getClass().getName()+":"+Thread.currentThread().getName()+" Stopped Reading.");
	}

	@Override
	public void write(long bib) throws Exception {
		status = 3;
		notify();
		System.out.println(this.getClass().getName()+":"+Thread.currentThread().getName()+" Started Writing...");
	}

	@Override
	public void connect() {
		status = 1;
		notify();
		System.out.println(this.getClass().getName()+" Connected.");
	}

	@Override
	public void disconnect() {
		status = 0;
		notify();
		System.out.println(this.getClass().getName()+" Discconnected.");
		
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
		System.out.println("Run thread "+Thread.currentThread().getName());
		while(getStatus()==2) {
			for(Event event:Event.findEventsByRunning()){
				int bibnum = new Random().nextInt(30);
				long bibtime = new Date().getTime();
				try{
					if(null == event.getTimerStart() && null == event.getTimeStart())
						continue;
					long starttime = (Long) ((null!=event.getTimerStart()) ? event.getTimerStart() : event.getTimeStart());
					RaceResult result = new RaceResult();
					try{
						result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult();
					}catch(Exception e){}
					String timeOfficial = RaceResult.toHumanTime(starttime, bibtime);
					result.setEvent(event);
					result.setBib(bibnum+"");
					result.setTimeofficial(timeOfficial);
					if(null==result.getId())
						result.persist();
					else
						result.merge();
					System.out.println("Found "+bibnum+" @ "+timeOfficial +" in "+event.getName());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			// wait or conceed
	        try {
	            Thread.sleep(1000);
	    		System.out.println("Again thread "+Thread.currentThread().getName());
	        } catch (InterruptedException e) {}
		}
	}
}