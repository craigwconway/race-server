/**
 * 
 */
package com.bibsmobile.model.dto;

/**
 * Wrapper for request method for adding a bank account
 * @author galen
 *
 */
public class BankAddRequest {
	public Long userGroupId;
	public String stripeToken;
	public String bankToken;
	public String holderName;
	public String customName;
	public String type;
	/**
	 * @return the userGroupId
	 */
	public Long getUserGroupId() {
		return userGroupId;
	}
	/**
	 * @param userGroupId the userGroupId to set
	 */
	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}
	/**
	 * @return the stripeToken
	 */
	public String getStripeToken() {
		return stripeToken;
	}
	/**
	 * @param stripeToken the stripeToken to set
	 */
	public void setStripeToken(String stripeToken) {
		this.stripeToken = stripeToken;
	}
	/**
	 * @return the stripeToken
	 */
	public String getBankToken() {
		return bankToken;
	}
	/**
	 * @param stripeToken the stripeToken to set
	 */
	public void setBankToken(String bankToken) {
		this.bankToken = bankToken;
	}
	/**
	 * @return the holderName
	 */
	public String getHolderName() {
		return holderName;
	}
	/**
	 * @param holderName the holderName to set
	 */
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	/**
	 * @return the customName
	 */
	public String getCustomName() {
		return customName;
	}
	/**
	 * @param customName the customName to set
	 */
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
