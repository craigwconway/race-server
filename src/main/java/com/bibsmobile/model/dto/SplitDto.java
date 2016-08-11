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
	private String timeDisplay;
	private int position;
	
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
	}
	/**
	 * @return the timeDisplay
	 */
	public String getTimeDisplay() {
		return timeDisplay;
	}
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}
	/**
	 * @param timeDisplay the timeDisplay to set
	 */
	public void setTimeDisplay(String timeDisplay) {
		this.timeDisplay = timeDisplay;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	};
	
}
