package com.bibsmobile.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public abstract class AbstractTimer implements Timer {
	
	public static final String UNASSIGNED_BIB_EVENT_NAME = "Unassigned Results";

	private Map<String, Integer> bibsByReader = new HashMap<String, Integer>(); // position, count
	private List<String> bibCache = new ArrayList<String>();
	private Set<String> uniqueBibs = new TreeSet<String>();
	private Map<String, Long> bibTimes = new HashMap<String, Long>();
	
	// auto-add an unregistered runner to an event
	public void logTime(final String bib, long time, final TimerConfig timerConfig, final Event event) {
		RaceResult r = new RaceResult();
		r.setEvent(event);
		r.setBib(bib);
		r = calculateOfficialTime(r, time, timerConfig);
		r.persist();
	}
	
	public void logTime(final int bibnum, long bibtime, final TimerConfig timerConfig) {
		bibtime = bibtime / 1000; //microseconds
		final String slog = Thread.currentThread().getName() +" " + getClass().getName();
		final String cacheKey = bibnum+"-"+timerConfig.getPosition();
		System.out.println(slog+" logging '"+bibnum + "' @ "+bibtime+ ", position "+timerConfig.getPosition());
		logTimerRead(String.valueOf(bibnum),timerConfig.getUrl());// test logging
		if (!bibTimes.containsKey(cacheKey)) 
			bibTimes.put(cacheKey, bibtime);

		// timeout by timer config
		if(bibtime < (bibTimes.get(cacheKey) + (timerConfig.getReadTimeout() * 1000)) ){
			// match bib to running events
			List<Event> events = Event.findEventsByRunning();
			System.out.println(slog+" running events "+events.size() );
			boolean foundEventForBib = false;
			for(Event event:events){
				try{
					// if event started
					if(null==event.getGunTime() )
						continue;

					System.out.println(slog+" EVENT: "+event.getId() +" start:"+event.getTimeStart() +" gun:"+event.getGunTimeStart());
					RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult(); // throws exception
					foundEventForBib = true;
					result.setEvent(event);
					System.out.println(slog+" RUNNER: "+result.getId()+ " start:" +result.getTimestart()+" finish:"+result.getTimeofficial() );
					
					// calculate start, finish, split times
					result = calculateOfficialTime(result, bibtime, timerConfig);
					
					System.out.println(slog+" UPDATE "+result.getId()+ " start:" +result.getTimestart()+" finish:"+result.getTimeofficial() );
					result.merge();
					
				}catch(Exception e){
					System.out.println(slog+" ERROR "+e.getMessage());
				} 
			}
			// log unassigned bibs to a special Event
			if(!foundEventForBib){
				// there's only 1 running event, put the runner in
				if(events.size() == 1){
					logTime(String.valueOf(bibnum), bibtime, timerConfig, events.get(0));
				// otherwise save it to special event
				}else{
					logUnassignedBib(String.valueOf(bibnum), bibtime, timerConfig);
				}
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
			String[] splits = (null==result.getTimesplit() || result.getTimesplit().isEmpty()) 
					? new String[]{} : result.getTimesplit().split(",");
			if(timerConfig.getPosition() > splits.length) splits = Arrays.copyOf(splits, timerConfig.getPosition());
			splits[timerConfig.getPosition()-1] = bibtime+"";
			StringBuffer s = new StringBuffer();
			for(int i=0;i<splits.length;i++){
				if(i>0) s.append(",");
				s.append(splits[i]);
			}
			result.setTimesplit(s.toString());
			long starttime = (result.getTimestart()==0 && null!=result.getEvent().getGunTime()) 
					? result.getEvent().getGunTime().getTime() : result.getTimestart(); 
			result.setTimestart( starttime );
			result.setTimeofficial( bibtime );
			result.setTimeofficialdisplay( RaceResult.toHumanTime(starttime, bibtime) );
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
	
	public void logUnassignedBib(String bib, long time, final TimerConfig timerConfig){
		System.out.println("UNASSIGNED '" + bib + "' @ "+ new Date(time));
		// find unassigned bibs event
		Event event = null;
		try{
			event = Event.findEventByNameEquals(UNASSIGNED_BIB_EVENT_NAME);
		}catch(Exception e){
			event = new Event();
			event.setName(UNASSIGNED_BIB_EVENT_NAME);
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
	
	public void clearReport(){
		// clear out cache
		bibTimes.clear();
		bibCache.clear();
		bibsByReader.clear();
		uniqueBibs.clear();
	}

}
