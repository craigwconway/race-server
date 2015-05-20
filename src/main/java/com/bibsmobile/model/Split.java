/**
 * 
 */
package com.bibsmobile.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.annotations.Parent;

/**
 * Object containing splits. This object cannot be accessed directly, only through the raceresult containing it
 * as an embedded entity. There can only be one split per position.
 * @author galen
 *
 */
@Embeddable
public class Split {
	private long time;
	private String timedisplay;
	private String name;
	private Integer distance;
	@Parent
	private RaceResult raceResult;
	/**
	 * @return long containing the unix timestamp of the runner's time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * Set a human-readable time at the split point.
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * Get a human readable time at the split point.
	 * @return the timedisplay
	 */
	public String getTimedisplay() {
		return timedisplay;
	}
	/**
	 * @param timedisplay the timedisplay to set
	 */
	public void setTimedisplay(String timedisplay) {
		this.timedisplay = timedisplay;
	}
	/**
	 * Custom name for the split point.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Custom name for the split point. 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the distance at the split point
	 */
	public Integer getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	/**
	 * @return the raceResult
	 */
	public RaceResult getRaceResult() {
		return raceResult;
	}
	/**
	 * @param raceResult the raceResult to set
	 */
	public void setRaceResult(RaceResult raceResult) {
		this.raceResult = raceResult;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Split [time=" + time + ", timedisplay=" + timedisplay + "]";
	}
}
