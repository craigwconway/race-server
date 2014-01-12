package com.bibsmobile.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Transient;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;
import com.thingmagic.Gen2;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TagData;
import com.thingmagic.TagFilter;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;

public class ThingMagicTimer implements Timer {
	
	private int status;
	private Reader reader;
	private ReadListener readListener; 
	private TimerConfig timerConfig;

	@Override
	public void connect() {
		try {
			reader = Reader.create(timerConfig.getUrl()); 
			reader.connect();
			optimize();
			status = 1;
			System.out.println("Reader at " + timerConfig.getUrl() + "connected");
		} catch (ReaderException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		if (null == reader)
			return;
		reader.destroy();
		reader = null;
		status = 0;
	}

	@Override
	public void startReader() {
		// stop writing
		if (getStatus() == 3) {
			this.disconnect();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.connect();
		}
		if (getStatus() == 1) {
			if(timerConfig.getPosition() == 0) readListener = new StartLineListener();
			else readListener = new FinishLineListener();
			reader.addReadListener(readListener);
			reader.startReading();
			status = 2;
		}
	}

	@Override
	public void stopReader() {
		if (null == reader)
			return;
		reader.stopReading();
		reader.removeReadListener(readListener);
		status = 1;
	}
	

	@Override
	public void write(long num) {
		status = 3; 
		TagReadData[] bibSeen;
		byte[] bibdata = new byte[] { (byte) (num >>> 24), (byte) (num >>> 16),
				(byte) (num >>> 8), (byte) (num) };
		try {
			bibSeen = reader.read(250);
			if (bibSeen.length < 1) {
				System.out.println("0" + " tags seen, could not write");
			}
			if (bibSeen.length > 1) {
				System.out.println(bibSeen.length
						+ "tags seen, could not write");
			}
			TagFilter target = null;
			TagData bibinf = new TagData(bibdata);
			// r.writeTag(null, bibinf);
			Gen2.TagData epc = new Gen2.TagData(bibdata);
			Gen2.WriteTag tagop = new Gen2.WriteTag(epc);
			reader.executeTagOp(tagop, target);
		} catch (ReaderException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long getDateTime() {
		long t = 0l;
		if (getStatus() < 1)
			return 0;
		try {
			System.out.println(reader.paramGet("/reader/currentTime"));
			t = ((Date) reader.paramGet("/reader/currentTime")).getTime();
		} catch (ReaderException e) {
			e.printStackTrace();
		}
		return t;
	}

	private void optimize() {
		try {
			reader.paramSet("/reader/read/plan", new SimpleReadPlan(null,
					TagProtocol.GEN2, true));
		} catch (ReaderException e) {
			e.printStackTrace();
		}

	}

	class FinishLineListener implements ReadListener {

		private HashMap<Integer, Long> bibTimes = new HashMap<Integer, Long>();
		
		public void tagRead(Reader r, TagReadData tr) {
			TagData t = tr.getTag();
			byte[] bibdata = t.epcBytes();
			long bibtime = tr.getTime();
			int bibnum = bibdata[3] & 0xFF | (bibdata[2] & 0xFF) << 8
					| (bibdata[1] & 0xFF) << 16 | (bibdata[0] & 0xFF) << 24;
			System.out.println("New tag (bibs): " + bibnum);
			System.out.println("New tag (mylaps): " + t.toString());

			if (!bibTimes.containsKey(bibnum)) 
				bibTimes.put(bibnum, bibtime);
			
			if(bibtime < (bibTimes.get(bibnum) + (timerConfig.getReadTimeout() * 1000)) ){
				bibTimes.put(bibnum, bibtime);
				for(Event event:Event.findEventsByRunning()){
					try{
						RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult();
						result.setTimeofficial("" + bibtime);
						result.merge();
					}catch(Exception e){
						System.out.println("No bib found for "+bibnum+" in "+event.getName());
					}
				}
			}else{
				System.out.println("Finish Timeout reached for "+bibnum);
			}
		}
	}
	
	class StartLineListener implements ReadListener {

		private HashMap<Integer, Long> bibTimes = new HashMap<Integer, Long>();
		
		public void tagRead(Reader r, TagReadData tr) {
			TagData t = tr.getTag();
			byte[] bibdata = t.epcBytes();
			long bibtime = tr.getTime();
			int bibnum = bibdata[3] & 0xFF | (bibdata[2] & 0xFF) << 8
					| (bibdata[1] & 0xFF) << 16 | (bibdata[0] & 0xFF) << 24;
			System.out.println("New tag (bibs): " + bibnum);
			System.out.println("New tag (mylaps): " + t.toString());

			if (!bibTimes.containsKey(bibnum)) 
				bibTimes.put(bibnum, bibtime);
			
			if(bibtime < (bibTimes.get(bibnum) + (timerConfig.getReadTimeout() * 1000)) ){
				bibTimes.put(bibnum, bibtime);
				for(Event event:Event.findEventsByRunning()){
					try{
						RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult();
						result.setTimestart("" + bibtime);
						result.merge();
					}catch(Exception e){
						System.out.println("No bib found for "+bibnum+" in "+event.getName());
					}
				}
			}else{
				System.out.println("Start Timeout reached for "+bibnum);
			}
		}
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
