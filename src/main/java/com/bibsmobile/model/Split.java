/**
 * 
 */
package com.bibsmobile.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Parent;

/**
 * Object containing splits. This object cannot be accessed directly, only through the raceresult containing it
 * as an embedded entity. There can only be one split per position.
 * @author galen
 *
 */
@Embeddable
public class Split {
	/**
	 * Used for a manually specified time string.
	 */
	private String timeManual;
	/**
	 * Used to store discrete split time.
	 */
	private long time;
	/**
	 * Used to store a custom name for the split.
	 */
	private String name;
	
	/**
	 * Type of split time.
	 */
	@Enumerated
	private SplitTimeType type = SplitTimeType.DISCRETE;
	
	/**
	 * Parent result object.
	 */
	@Parent
	private RaceResult raceResult;
	
	/**
	 * @return the timeManual
	 */
	public String getTimeManual() {
		return timeManual;
	}
	/**
	 * @param timeManual the timeManual to set
	 */
	public void setTimeManual(String timeManual) {
		this.timeManual = timeManual;
	}
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public SplitTimeType getType() {
		return type;
	}
	public void setType(SplitTimeType type) {
		this.type = type;
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
	
}
