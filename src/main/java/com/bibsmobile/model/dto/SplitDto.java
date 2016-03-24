/**
 * 
 */
package com.bibsmobile.model.dto;

import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Split;

/**
 * @author galen
 *
 */
public class SplitDto {
	String timeDisplay;
	int position;
	
	public SplitDto(){};
	public SplitDto(Split split) {
		switch(split.getType()) {
		case CUMULATIVE:
			this.timeDisplay = split.getTimeManual();
			break;
		case DISCRETE:
			this.timeDisplay = split.getTimeManual();
			break;
		case TIMESTAMP:
			if(split.getRaceResult() == null) break;
			if(split.getTime() == 0) break;
			if(split.getRaceResult().getTimestart() > 0) {
				this.timeDisplay = RaceResult.toHumanTime(split.getRaceResult().getTimestart(), split.getTime());
			} else if(split.getRaceResult().getEventType() != null && split.getRaceResult().getEventType().getGunTime().getTime() > 0) {
				this.timeDisplay = RaceResult.toHumanTime(split.getRaceResult().getEventType().getGunTime().getTime(), split.getTime());
			}
			break;
		default:
			break;
		}
	};
	
	public SplitDto(Split split, int position) {
		this.position = position;
		switch(split.getType()) {
		case CUMULATIVE:
			this.timeDisplay = split.getTimeManual();
			break;
		case DISCRETE:
			this.timeDisplay = split.getTimeManual();
			break;
		case TIMESTAMP:
			if(split.getRaceResult() == null) break;
			if(split.getTime() == 0) break;
			if(split.getRaceResult().getTimestart() > 0) {
				this.timeDisplay = RaceResult.toHumanTime(split.getRaceResult().getTimestart(), split.getTime());
			} else if(split.getRaceResult().getEventType() != null && split.getRaceResult().getEventType().getGunTime().getTime() > 0) {
				this.timeDisplay = RaceResult.toHumanTime(split.getRaceResult().getEventType().getGunTime().getTime(), split.getTime());
			}
			break;
		default:
			break;
		}
	};
	
}
