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
		System.out.println("Connecting to " + timerConfig.getUrl() + " type:" + timerConfig.getType() + " "+getClass().getName());
		try {
			reader = Reader.create(timerConfig.getUrl()); 
			reader.connect();
			optimize();
			status = 1;
			System.out.println("Reader at " + timerConfig.getUrl() + " connected. (status="+status+")");
		} catch (ReaderException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		if (null == reader)
			return;
		reader.stopReading();
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
		if (status == 1) {
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
		if (status < 1)
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
		final String l = Thread.currentThread().getName() +" " + getClass().getName();
		
		public void tagRead(Reader r, TagReadData tr) {
			TagData t = tr.getTag();
			byte[] bibdata = t.epcBytes();
			long bibtime = tr.getTime();
			int bibnum = bibdata[3] & 0xFF | (bibdata[2] & 0xFF) << 8
					| (bibdata[1] & 0xFF) << 16 | (bibdata[0] & 0xFF) << 24;

			System.out.println(l+" bib "+bibnum + "("+t.toString()+")");

			if (!bibTimes.containsKey(bibnum)) 
				bibTimes.put(bibnum, bibtime);
			
			if(bibtime < (bibTimes.get(bibnum) + (timerConfig.getReadTimeout() * 1000)) ){
				for(Event event:Event.findEventsByRunning()){
					// if event started
					if(null==event.getGunTime())
						continue;
					
					try{
						RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult();
						// check timeout again
						long cTimeofficial = 0;
						if(result.getTimeofficial() > 0){
							cTimeofficial = result.getTimeofficial();
							if(bibtime > cTimeofficial + (timerConfig.getReadTimeout() * 1000)) {
								System.out.println(l+" existing timeout "+bibnum);
								continue; // don't update	
							}
						}
						// bib vs chip start
						long starttime = 0l;
						if(result.getTimestart()>0){
							starttime = Long.valueOf(result.getTimestart()) ;
							System.out.println(l+" starttime runner "+starttime);
						}else{
							starttime = event.getGunTime().getTime(); 
							System.out.println(l+" starttime event "+starttime);
						}
						final String strTime = RaceResult.toHumanTime(starttime, bibtime);
						result.setTimeofficial( bibtime );
						result.setTimeofficialdisplay( strTime );
						result.merge();
						System.out.println(l+" update bib "+bibnum+" "+strTime+" "+event.getName());
					}catch(org.springframework.dao.EmptyResultDataAccessException e){
						System.out.println(l+" unregistered bib "+bibnum+" "+event.getName());
					}catch(Exception e){
						e.printStackTrace();
					} 
				}
			}else{
				System.out.println(l+" finish timeout for "+bibnum);
			}
		}
	}
	
	class StartLineListener implements ReadListener {

		private HashMap<Integer, Long> bibTimes = new HashMap<Integer, Long>();
		final String l = Thread.currentThread().getName() +" " + getClass().getName();
		
		public void tagRead(Reader r, TagReadData tr) {
			TagData t = tr.getTag();
			byte[] bibdata = t.epcBytes();
			long bibtime = tr.getTime();
			int bibnum = bibdata[3] & 0xFF | (bibdata[2] & 0xFF) << 8
					| (bibdata[1] & 0xFF) << 16 | (bibdata[0] & 0xFF) << 24;

			System.out.println(l+" bib "+bibnum + "("+t.toString()+")");

			if (!bibTimes.containsKey(bibnum)) 
				bibTimes.put(bibnum, bibtime);
			
			if(bibtime < (bibTimes.get(bibnum) + (timerConfig.getReadTimeout() * 1000)) ){
				for(Event event:Event.findEventsByRunning()){
					try{
						// if event started
						if(null==event.getGunTime() )
							continue;
						
						RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult();
						// check timeout again
						long cTimestart = 0;
						if(result.getTimestart()>0){
							cTimestart = result.getTimestart();
							if(bibtime > cTimestart + (timerConfig.getReadTimeout() * 1000)) {
								System.out.println(l+" existing starttime "+bibnum);
								continue; // don't update	
							}
						}
						result.setTimestart( bibtime );
						result.merge();
						System.out.println(l+" update start for bib "+bibnum+" in "+event.getName());
					}catch(Exception e){
						System.out.println(l+" ERROR2 "+e.getMessage());
					} 
				}
			}else{
				System.out.println(l+" start timeout for "+bibnum);
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
