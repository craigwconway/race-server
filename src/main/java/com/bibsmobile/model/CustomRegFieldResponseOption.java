/**
 * 
 */
package com.bibsmobile.model;

import java.util.Collection;

import flexjson.JSONSerializer;

/**
 * Used to hold options to respond to questions
 * @author galen
 *
 */
public class CustomRegFieldResponseOption {
	/**
	 * String holding response to question
	 */
	private String response;
	/**
	 * Long containing discount to price
	 */
	private Long price;
	
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}
	/**
	 * @return the price
	 */
	public Long getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Long price) {
		this.price = price;
	}
	
    public static String toJsonArray(Collection<CustomRegFieldResponseOption> collection) {
        return new JSONSerializer().include("*.children").exclude("*.class").serialize(collection);
    }
    
    public String toJson() {
    	return new JSONSerializer().serialize(this);
    }


}
