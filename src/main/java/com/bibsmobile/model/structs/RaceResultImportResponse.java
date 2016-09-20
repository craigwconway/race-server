/**
 * 
 */
package com.bibsmobile.model.structs;

/**
 * Used in response from importing a RaceResult object
 * @author galen
 *
 */
public class RaceResultImportResponse {
	/**
	 * Whether or not the eventType of this result was changed
	 */
	private boolean eventTypeChanged = false;
	/**
	 * Whether or not there was an error in import
	 */
	private boolean error = false;
	/**
	 * Whether or not a new result was created
	 */
	private boolean newResult = false;
	
	/**
	 * @return the eventTypeChanged
	 */
	public boolean isEventTypeChanged() {
		return eventTypeChanged;
	}
	/**
	 * @param eventTypeChanged the eventTypeChanged to set
	 */
	public void setEventTypeChanged(boolean eventTypeChanged) {
		this.eventTypeChanged = eventTypeChanged;
	}
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}
	/**
	 * @return the newResult
	 */
	public boolean isNewResult() {
		return newResult;
	}
	/**
	 * @param newResult the newResult to set
	 */
	public void setNewResult(boolean newResult) {
		this.newResult = newResult;
	}
}
