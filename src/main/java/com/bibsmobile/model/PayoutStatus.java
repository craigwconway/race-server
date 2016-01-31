/**
 * 
 */
package com.bibsmobile.model;

/**
 * Enum used to track payout status for directors.
 * @author galen
 *
 */
public enum PayoutStatus {
	/**
	 * Payout created and pending approval.
	 */
	PENDING,
	/**
	 * Payout currently processing.
	 */
	PROCESSING,
	/**
	 * Payout completed.
	 */
	COMPLETED,
	/**
	 * Payout has an error.
	 */
	ERROR,
	/**
	 * Payout discarded.
	 */
	DISCARDED;
}
