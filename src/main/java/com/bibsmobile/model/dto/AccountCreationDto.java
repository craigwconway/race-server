package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Enumerated;

import java.util.Date;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.ReaderStatus;
import com.bibsmobile.model.TimeSyncEnum;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This object is used for event directors signing up for new accounts.
 * @author galen
 *
 */
public class AccountCreationDto {

	/**
	 * @apiDefine accountCreationDto
	 * @apiParam {String} username username of registering event director
	 * @apiParam {String} password password of registering event director
	 * @apiParam {String} phone phone number of registering event director
	 * @apiParam {String} email email of registering event director
	 * @apiParam {String} firstname firstname of registering event director
	 * @apiParam {String} lastname lastname of registering event director
	 * @apiParam {String} orgName Name of director's organization
	 * @apiParam {String} description of director's organization
	 */
	
	/**
	 * Default Constructor.
	 */
	AccountCreationDto() {
		
	}
	
	/**
	 * First name of registering user. Maps to {@link com.bibsmobile.model.UserProfile#getFirstname() firstname}
	 */
	private String firstname;
	
	/**
	 * Last name of registering user.  Maps to {@link com.bibsmobile.model.UserProfile#getLastname() lastname}
	 */
	private String lastname;
	
	/**
	 * Username of registering user. Maps to {@link com.bibsmobile.model.UserProfile#getUsername() username}
	 */
	private String username;
	
	/**
	 * Password of registering user. Maps to {@link com.bibsmobile.model.UserProfile#getPassword() password}
	 */
	private String password;
	
	/**
	 * User's phone number. Maps to {@link com.bibsmobile.model.UserProfile#getPhone() phone}
	 */
	private String phone;
	
	/**
	 * Email of registering user. Maps to {@link com.bibsmobile.model.UserProfile#getEmail() email}
	 */
	private String email;
	
	/**
	 * Name of user's organization. Maps to {@link com.bibsmobile.model.UserGroup#getName() org name}
	 */
	private String orgName;
	
	/**
	 * Description of user's organization. Maps to {@link com.bibsmobile.model.UserGroup#getDescription() org description}
	 */
	private String orgDescription;

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * @return the orgDescription
	 */
	public String getOrgDescription() {
		return orgDescription;
	}

	/**
	 * @param orgDescription the orgDescription to set
	 */
	public void setOrgDescription(String orgDescription) {
		this.orgDescription = orgDescription;
	}
	
	
	
}
