package com.bibsmobile.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public class DummyTimer implements Timer, Runnable {

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
		notify();
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
		notify();
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
			for (Event event : Event.findEventsByRunning()) {
				// if event started
				if (null == event.getGunTime())
					continue;

				// put cache
				if (!bibTimes.containsKey(bibnum))
					bibTimes.put(bibnum, bibtime);

				// check cache time
				if (bibtime > (bibTimes.get(bibnum) + (timerConfig
						.getReadTimeout() * 1000))) {
					System.out.println(log + " timeout for " + bibnum);
					continue;
				}

				// lookup result
				RaceResult result = new RaceResult();
				try {
					result = RaceResult.findRaceResultsByEventAndBibEquals(
							event, bibnum + "").getSingleResult();
				} catch (org.springframework.dao.EmptyResultDataAccessException e) {
					System.out.println(log + " unregistered bib " + bibnum
							+ " " + event.getName());
					continue;
				}

				// starting line
				if (timerConfig.getPosition() == 0) {
					// check existing starttime
					long cTimestart = 0;
					if (result.getTimestart()>0) {
						cTimestart = result.getTimestart();
						if (bibtime > cTimestart
								+ (timerConfig.getReadTimeout() * 1000)) {
							System.out.println(log + " existing starttime "
									+ bibnum);
							continue; // don't update
						}
					}
					result.setTimestart(bibtime);
					result.merge();
					System.out.println(log + " update start for bib " + bibnum
							+ " in " + event.getName());

				}

				// finish line
				if (timerConfig.getPosition() > 0) {
					// check existing timeofficial
					long cTimeofficial = 0;
					if (result.getTimeofficial()>0) {
						cTimeofficial = result.getTimeofficial();
						if (bibtime > cTimeofficial
								+ (timerConfig.getReadTimeout() * 1000)) {
							System.out.println(log + " existing timeout "
									+ bibnum);
							continue; // don't update
						}
					}
					// bib vs chip start
					long starttime = 0l;
					if (result.getTimestart()>0) {
						starttime = Long.valueOf(result.getTimestart());
						System.out.println(log + " starttime runner "
								+ starttime);
					} else {
						starttime = event.getGunTime().getTime();
						System.out.println(log + " starttime event "
								+ starttime);
					}
					final String strTime = RaceResult.toHumanTime(starttime,
							bibtime);
					result.setTimeofficial(bibtime);
					result.setTimeofficialdisplay(strTime);
					result.merge();
					System.out.println(log + " update bib " + bibnum + " "
							+ strTime + " " + event.getName());
				}

			}
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