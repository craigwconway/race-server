/**
 * 
 */
package com.bibsmobile.model;

import javax.persistence.Embeddable;

/**
 * Embeddable class inside of event. This contains pricing details related to events.
 * @author galen
 *
 */
@Embeddable
public class EventPricing {
	/**
	 * Absolute fee in currency units. This is added onto every order.
	 */
	private long absoluteFee = 100;
	
	/**
	 * Relative fee in percentage. This is added onto every order.
	 */
	private double relativeFee = .06;
	
	/**
	 * String containing the currency type
	 */
	private CurrencyEnum currency = CurrencyEnum.USD;

	/**
	 * Absolute pricing, default 100 US cents.
	 * @return the absolute pricing
	 */
	public long getAbsoluteFee() {
		return absoluteFee;
	}

	/**
	 * Absolute pricing, default 100 US cents.
	 * @param absolute the absolute to set
	 */
	public void setAbsoluteFee(long absoluteFee) {
		this.absoluteFee = absoluteFee;
	}

	/**
	 * Relative fee, this is a percentage added onto the fee
	 * @return the relative
	 */
	public double getRelative() {
		return relativeFee;
	}

	/**
	 * @param Relative fee to set, this is a percentage added to the price
	 */
	public void setRelative(long relativeFee) {
		this.relativeFee = relativeFee;
	}

	/**
	 * @return the currency
	 */
	public CurrencyEnum getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(CurrencyEnum currency) {
		this.currency = currency;
	}

}
