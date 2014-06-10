package com.bibsmobile.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.TimerConfig;

public abstract class AbstractTimer implements Timer {

	
	@Override
	public void logTime(final int bibnum, long bibtime, final TimerConfig timerConfig) {
		Map<String, Long> bibTimes = new HashMap<String, Long>();
		final String slog = Thread.currentThread().getName() +" " + getClass().getName();
		final String cacheKey = bibnum+"-"+timerConfig.getPosition();
		//bibtime = bibtime / 1000;
		System.out.println(slog+" logging '"+bibnum + "' @ "+bibtime+ ", position "+timerConfig.getPosition());
		if (!bibTimes.containsKey(cacheKey)) 
			bibTimes.put(cacheKey, bibtime);

		System.out.println(slog+" ok '");
		if(bibtime < (bibTimes.get(cacheKey) + (timerConfig.getReadTimeout() * 1000)) ){
			System.out.println(slog+" ok 2");
			List<Event> events = Event.findEventsByRunning();
			System.out.println(slog+" ok 3");
			System.out.println(slog+" events '"+events.size() );
			for(Event event:events){
				System.out.println(slog+" event '"+event.getName() +" start:"+event.getTimeStart() +" gun:"+event.getGunTimeStart());
				try{
					// if event started
					if(null==event.getGunTime() )
						continue;
					
					RaceResult result = RaceResult.findRaceResultsByEventAndBibEquals(event,bibnum+"").getSingleResult();
					System.out.println(slog+" origional '"+result.getFirstname()+ "start:" +result.getTimestart()+" finish:"+result.getTimeofficial() );

					// starting line
					if(timerConfig.getPosition() == 0){
						long cTimestart = 0;
						if(result.getTimestart()>0){
							cTimestart = result.getTimestart();
							if(bibtime > cTimestart + (timerConfig.getReadTimeout() * 1000)) {
								System.out.println(slog+" existing starttime "+bibnum);
								continue; // don't update	
							}
						}
						result.setTimestart( bibtime );
					
					// finish line	
					}else{
						long cTimeofficial = 0;
						if(result.getTimeofficial() > 0){
							cTimeofficial = result.getTimeofficial();
							if(bibtime > cTimeofficial + (timerConfig.getReadTimeout() * 1000)) {
								System.out.println(slog+" existing timeout "+bibnum);
								continue; // don't update	
							}
						}
						// bib vs chip start
						long starttime = 0l;
						if(result.getTimestart()>0){
							starttime = Long.valueOf(result.getTimestart()) ;
							System.out.println(slog+" starttime runner "+starttime);
						}else{
							starttime = event.getGunTime().getTime(); 
							result.setTimestart( starttime );
							System.out.println(slog+" starttime event "+starttime);
						}
						final String strTime = RaceResult.toHumanTime(starttime, bibtime);
						result.setTimeofficial( bibtime );
						result.setTimeofficialdisplay( strTime );
					}
					
					System.out.println(slog+" update '"+result.getFirstname()+ "start:" +result.getTimestart()+" finish:"+result.getTimeofficial() );

					
					result.merge();
					
					System.out.println(slog+" update for bib "+bibnum+" in "+event.getName()+
							" line position "+timerConfig.getPosition());
					
				}catch(Exception e){
					System.out.println(slog+" ERROR2 "+e.getMessage());
				} 
			}
		}else{
			System.out.println(slog+" timeout for "+bibnum + "at position "+timerConfig.getPosition());
		}
	}

}
