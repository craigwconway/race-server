/**
 * 
 */
package com.bibsmobile.model;

import javax.persistence.Embeddable;

/**
 * Set custom fields to be used in import and displaying results in the webapp. Added to handle the
 * multitude of special or unique situations a few event directors land themselves in. An example of
 * a custom mapping might be for a beer run with the resulting object (in json form): {"field":"beers","value":"3"}
 * @author galen [gedanziger]
 *
 */
@Embeddable
public class CustomResultField {
	private String field;
	private String value;
	
	/**
	 * @return The custom type mapped. An example might be "beers" for a beer run.
	 */
	public String getField() {
		return field;
	}
	/**
	 * @param field the custom type mapped. An example might be "beers" for a beer run.
	 */
	public void setField(String field) {
		this.field = field;
	}
	/**
	 * @return The value set by the mapping. An example of a value might be "3" for number of
	 * beers consumed at a beer run.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value set by the mapping. An example of a value might be "3" for number of
	 * beers consumed at a beer run.
	 */
	public void setResult(String value) {
		this.value = value;
	}
}
