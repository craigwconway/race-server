package com.bibsmobile.model.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.UserProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a data transfer object used for list view a user's details.
 * It is a representation of a {@link com.bibsmobile.model.UserProfile UserProfile}
 * @author galen
 *
 */
public class UserDetailsShortDto {
	
	/**
	 * @apiDefine userDetailsShortDto
	 * @apiSuccess (200) {Number} id unique ID of user
	 * @apiSuccess (200) {String} firstname first name of user
	 * @apiSuccess (200) {String} lastname last name of user
	 * @apiSuccess (200) {String} email email of user
	 * @apiSuccess (200) {String} gender of user
	 * @apiSuccess (200) {String} birthdate String of user's birthdate in format yyyy-MM-dd
	 * @apiSuccess (200) {Object[]} results results claimed by user
	 * @apiSuccess (200) {Number} results.id id of race result
	 * @apiSuccess (200) {Number} results.bib bib number of race result
	 * @apiSuccess (200) {Object[]} events events followed by user
	 * @apiSuccess (200) {Number} events.id id of event followed
	 * @apiSuccess (200) {Number} events.name name of event followed
	 * @apiSuccess (200) {Number} events.timeStart timestamp of event followed
	 * @apiSuccess (200) {String} events.timeStartLocal local event time
	 * @apiSuccessExample Single Image Found
	 * {
	 * 	"id":1,
	 * 	"firstname": "galen",
	 * 	"lastname":"danziger",
	 * 	"email":"gedanziger@gmail.com",
	 * 	"gender":"M",
	 * 	"birthdate":"1982-11-15",
	 * 	"events": [
	 * 		{
	 * 			"id":1,
	 * 			"name":"King's Canyon Critical Mass",
	 * 			"timeStart": 1448959460000,
	 * 			"timeStartLocal": "12/01/2015 12:44:20 AM".
	 * 			"city":"San Francisco",
	 * 			"state":"California"
	 * 		}
	 * 	],
	 * 	"results": [
	 * 		{
	 * 			"id":1,
	 * 			"bib":5,
	 * 			"timeofficialdisplay":"11:11:11",
	 * 			"team":"facepunch runclub"
	 * 		}
	 * 	]
	 * }
	 */
	
	
	UserDetailsShortDto(UserProfile user) {
		this.id = user.getId();
		this.firstname = user.getFirstname();
		this.gender = user.getGender();
		this.email = user.getEmail();
		this.gender = user.getGender();
		if(user.getBirthdate() != null) {
			try{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				this.birthdate = fmt.format(user.getBirthdate());
			} catch(Exception e) {
				
			}
		}
		events = new HashSet<EventViewResultsDto>();
		for (Event event : user.getEvents()) {
			events.add(new EventViewResultsDto(event));
		}
		results = new HashSet<RaceResultViewDto>();
		for(RaceResult result : user.getRaceResults()) {
			results.add(new RaceResultViewDto(result));
		}


	}
	
	private Long id;
	
	private String firstname;
	
	private String lastname;
	
	private String email;
	
	private String gender;
	
	private String birthdate;
	
	private Set<EventViewResultsDto> events;
	
	private Set<RaceResultViewDto> results;
		
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String fromUserProfileToDto(UserProfile user ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new UserDetailsShortDto(user));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Caused by: " +e.getCause());
			return null;
		}
	}	

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

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
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the birthdate
	 */
	public String getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the events
	 */
	public Set<EventViewResultsDto> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(Set<EventViewResultsDto> events) {
		this.events = events;
	}

	/**
	 * @return the results
	 */
	public Set<RaceResultViewDto> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Set<RaceResultViewDto> results) {
		this.results = results;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	
}
