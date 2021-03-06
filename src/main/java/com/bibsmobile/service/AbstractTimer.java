package com.bibsmobile.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.util.BuildTypeUtil;

public abstract class AbstractTimer implements Timer {
	
	public static final String UNASSIGNED_BIB_EVENT_NAME = "Unassigned Results";

	private Map<String, Integer> bibsByReader = new HashMap<String, Integer>(); // position, count
	private List<String> bibCache = new ArrayList<String>();			// Used for testing purposes only
	private Set<String> uniqueBibs = new TreeSet<String>();  			// Used for testing purposes only
	private Map<String, Long> bibTimes = new HashMap<String, Long>();	// Timeout hashmap of (bibnumber -> time)
	
	// auto-add an unregistered runner to an event
	public void logTime(final long bib, long time, final TimerConfig timerConfig, final Event event) {
		RaceResult r = new RaceResult();
		try {
		r = RaceResult.findRaceResultsByEventAndBibEquals(event,bib).getSingleResult();
		r.setEvent(event);
		r.setBib(bib);
		r = calculateOfficialTime(r, time, timerConfig);
		r.merge();
		} catch (Exception e) {
		r.setEvent(event);
		r.setBib(bib);
		r = calculateOfficialTime(r, time, timerConfig);
		r.persist();
		}

	}
	public void logTime(final long bibnum, long bibtime, final TimerConfig timerConfig) {
		bibtime = bibtime / 1000; //microseconds
		final String slog = Thread.currentThread().getName() +" " + getClass().getName();
		final String cacheKey = bibnum+"-"+timerConfig.getPosition();
		System.out.println(slog+" logging '"+bibnum + "' @ "+bibtime+ ", position "+timerConfig.getPosition());
		logTimerRead(String.valueOf(bibnum),timerConfig.getUrl());// test logging
		
		if(BibTimeout.getTimeout(timerConfig.getPosition(), bibnum) == null) {
			BibTimeout.setTimeout(timerConfig.getPosition(), bibnum, bibtime);
		}
		//if (!bibTimes.containsKey(cacheKey)) 
		//	bibTimes.put(cacheKey, bibtime);
		boolean newlap=false;
		// timeout by timer config
		if(timerConfig.isLaps()==true) {
			System.out.println("bibnum: "+bibnum +" bibtime: "+ bibtime + " timeout: " +BibTimeout.getTimeout(timerConfig.getPosition(), bibnum));
			Long valid = BibTimeout.getTimeout(timerConfig.getPosition(), bibnum) + (timerConfig.getMinFinish() * 1000);
			System.out.println("valid at: " + valid);
			if(bibtime >= (BibTimeout.getTimeout(timerConfig.getPosition(), bibnum) + (timerConfig.getMinFinish() * 1000))) {
				newlap = true;
				System.out.println("[TIMER] Setting new lap for bib: " + bibnum + " at time " + bibtime);
				BibTimeout.setTimeout(timerConfig.getPosition(), bibnum, bibtime);
			}
		}
		if((bibtime < (BibTimeout.getTimeout(timerConfig.getPosition(), bibnum) + (timerConfig.getReadTimeout() * 1000))) || newlap){
			// match bib to running events
			List <Event> events;
			if(BuildTypeUtil.usesRfid()) {
				events = Event.findAllEvents();
			} else {
				events = Event.findEventsByRunning();
			}
			System.out.println(slog+" running events "+events.size() );
			boolean foundEventForBib = false;
			List <EventType> eventTypes = new ArrayList <EventType>();
			for(Event event : events) {
				for(EventType eventType : event.getEventTypes()) {
					eventTypes.add(eventType);
				}
			}
			for(EventType eventType:eventTypes){
				try{
					// if event started

					System.out.println(slog+" EVENT: "+eventType.getId() +" start:"+eventType.getStartTime() +" gun:"+eventType.getGunTime());
					RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(eventType.getEvent(),bibnum).getSingleResult(); // throws exception
					foundEventForBib = true;
					result.setEventType(eventType);
					System.out.println(slog+" RUNNER: "+result.getId()+ " start:" +result.getTimestart()+" finish:"+result.getTimeofficial() );
					result.setTimed(true);
					// timeout by previous time official
					if((result.getTimeofficial() > 0 && (bibtime > result.getTimeofficial() + (timerConfig.getReadTimeout() * 1000))) && !newlap){
						return;
					}
					// calculate start, finish, split times
					result = calculateOfficialTime(result, bibtime, timerConfig);
					if(newlap) {
						result.setLaps(result.getLaps() == null ? 0 : result.getLaps() + 1);
					}
					if(result.getLaps()  == null) {
						result.setLaps(1);
					}
					System.out.println(slog+" UPDATE "+result.getId()+ " start:" +result.getTimestart()+" finish:"+result.getTimeofficial() );
					result.merge();
					
				}catch(Exception e){
					System.out.println(slog+" ERROR "+e.getMessage());
				} 
			}
			// log unassigned bibs to a special Event
			if(!foundEventForBib){
				logUnassignedBib(bibnum, bibtime, timerConfig);
			}
		}else{
			System.out.println(slog+" TIMEOUT bib " + bibnum + " @ reader "+timerConfig.getPosition());
		}
	}

	/**
	 * Calculates the split time using the reader position
	 * @param result
	 * @param bibtime
	 * @param position
	 * @return
	 */
	public RaceResult calculateOfficialTime(RaceResult result, long bibtime, final TimerConfig timerConfig) {
		// starting line
		if(timerConfig.getPosition() == 0){
			long cTimestart = 0;
			if(result.getTimestart()>0){
				cTimestart = result.getTimestart();
				if(bibtime > cTimestart + (timerConfig.getReadTimeout() * 1000)) {
					System.out.println(result.getBib()+" START EXISTS ");
				}
			}
			result.setTimestart( bibtime );
		// splits
		}else{
			if(timerConfig.getPosition() > 1) {
				String[] splits = (null==result.getTimesplit() || result.getTimesplit().isEmpty()) 
						? new String[]{} : result.getTimesplit().split(",");
				if(timerConfig.getPosition() > (splits.length + 1)) splits = Arrays.copyOf(splits, timerConfig.getPosition() - 1);
				splits[timerConfig.getPosition()-2] = bibtime+"";
				StringBuffer s = new StringBuffer();
				for(int i=0;i<splits.length;i++){
					if(i>0) s.append(",");
					s.append(splits[i]);
				}
				result.setTimesplit(s.toString());				
			}
			long starttime = (result.getTimestart()==0 && null!=result.getEventType().getGunTime()) 
					? result.getEventType().getGunTime().getTime() : result.getTimestart(); 
			result.setTimestart( starttime );
			if(timerConfig.getPosition() == 1) {
				result.setTimeofficial( bibtime );
				result.setTimeofficialdisplay( RaceResult.toHumanTime(starttime, bibtime) );
			}
		}
		return result;
	}

	public void logTimerRead(String bib, String reader){
		System.out.println("Log Read '" + bib + "' @ "+reader);
		uniqueBibs.add(bib);
		if(!bibCache.contains(bib+"-"+reader)){
			bibCache.add(bib+"-"+reader);
			if(bibsByReader.containsKey(reader)){
				bibsByReader.put(reader, bibsByReader.get(reader)+1);
			}else{
				bibsByReader.put(reader, 1);
			}
		}
	}
	
	public void logGarbage(String bibString, long time, final TimerConfig timerConfig) {
		//TODO: Add logging for non-bibs chips
	}
	
	public void logUnassignedBib(long bib, long time, final TimerConfig timerConfig){
		System.out.println("UNASSIGNED '" + bib + "' @ "+ new Date(time));
		// find unassigned bibs event
		Event event = null;
		try{
			event = Event.findEventsByNameLike(UNASSIGNED_BIB_EVENT_NAME,1,1).getSingleResult();
		}catch(Exception e){
			System.out.println("Caught exception finding matching event");
			e.printStackTrace();
			event = new Event();
			event.setName(UNASSIGNED_BIB_EVENT_NAME);
			//event.setRunning(1);
			//event.setGunFired(true);
			//event.setGunTime(new Date());
			//event.setTimeStart(new Date());
			event.persist();
		}
		this.logTime(bib, time, timerConfig, event);
		System.out.println("UNASSIGNED '" + bib + "logged");
	}

	public String createReport(){
		// create file
		StringBuilder sb = new StringBuilder();
		for(String reader:bibsByReader.keySet()){
			sb.append("Reader "+reader+": "+bibsByReader.get(reader)+"          \t<br/>\n");
		}
		sb.append("Unique bibs: "+ uniqueBibs.size()+"          \t<br/>\n");
		sb.append("Total reads: "+ bibCache.size());
		
		return sb.toString();
	}
	
	public void clearTimesByEvent(long eventId){
		Event event = Event.findEvent(eventId);
		for(RaceResult runner:event.getRaceResults()){
			runner.setTimeofficial(0);
			runner.setTimeofficialdisplay("");
			runner.setLaps(null);
			runner.persist();
			System.out.println("Contents of bibTimes:");
			System.out.println(bibTimes);
			// delete by [bib]-[timer.position]
			for(int i=0;i<3;i++){
				String o = runner.getBib() + "-" + i;
				bibTimes.remove(o);
				bibCache.remove(o);
				bibsByReader.remove(o);
			}
			uniqueBibs.remove(runner.getBib());
		}
	}		
	
	public void clearAllTimesByEventAndTimerId(long eventId, int position){
		Event event = Event.findEvent(eventId);
		System.out.println("clearing times for event: " + event.getName() + " and position: " + position);
		System.out.println("Contents of bibTimes (pre-clear):");
		System.out.println(bibTimes);
		System.out.println("Clearing " + event.getRaceResults());
		for(RaceResult runner:event.getRaceResults()){
			runner.setTimeofficial(0);
			runner.setTimestart(0);
			runner.setTimeofficialdisplay("");
			runner.setTimesplit("");
			runner.merge();
			// delete by [bib]-[timer.position]
			String o = runner.getBib() + "-" + position;
			System.out.println("removing key: " + o);
			BibTimeout.clearBib(runner.getBib());
			//bibCache.remove(o);
			//bibsByReader.remove(o);
			//uniqueBibs.remove(runner.getBib());
		}
		System.out.println("Contents of bibTime(post-clear):");
		System.out.println(bibTimes);
	}	

}
