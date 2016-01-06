/**
 * 
 */
package com.bibsmobile.model;

/**
 * Statuscodes for invalid time syncronization. These are used in {@link SyncReport} objects.
 * @author galen
 *
 */
public enum TimeSyncStatusEnum {
	OK, BAD_TIMESTAMP, INVALID_SYNC_CODE;
}
