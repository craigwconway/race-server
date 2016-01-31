/**
 * 
 */
package com.bibsmobile.model;

/**
 * Statuscodes for invalid time syncronization. These are used in {@link SyncReport} objects.
 * @author galen
 *
 */
public enum SplitTimeType {
	/**
	 * This is a manually imported discrete time.
	 */
	DISCRETE, 
	/**
	 * This is a manually imported cumulative time.
	 */
	CUMULATIVE,
	/**
	 * This is a timestamp created from a bibs system.
	 */
	TIMESTAMP;
}
