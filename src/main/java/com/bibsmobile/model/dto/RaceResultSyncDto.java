package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Split;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a data transfer object used for list view returns of many RaceResults.
 * It does not have as much information, however.
 * @author galen
 *
 */
public class RaceResultSyncDto {
	
	/**
	 * @apiDefine raceResultSyncDto
	 * @apiSuccess (200) {Number} id unique ID of result
	 * @apiSuccess (200) {Number} bib Bib number of result
	 * @apiSuccess (200) {String} firstname First name of athlete
	 * @apiSuccess (200) {String} lastname Last name of athlete
	 * @apiSuccess (200) {Number} age Age of athlete
	 * @apiSuccess (200) {String} gender Gender of athlete. Either M or F
	 * @apiSuccess (200) {String} city City of origin of athlete
	 * @apiSuccess (200) {String} timeofficialdisplay Official Time of athlete
	 * @apiSuccess (200) {String} team Name of athlete's team
	 * @apiSuccessExample Single Result Found
	 * [
	 * 	{
	 * 		"id":1,
	 * 		"bib": 22,
	 * 		"firstname":"Galen",
	 * 		"lastname":"Danziger",
	 * 		"city":"San Francisco",
	 * 		"age":24,
	 * 		"gender":"M",
	 * 		"team": "Shrugrunners Run Club",
	 * 		"timeofficialdisplay":"00:23:57"
	 * 	}
	 * ]
	 */
	
	
	RaceResultSyncDto(RaceResult result) {
		this.id = result.getId();
		this.bib = result.getBib();
		this.firstname = result.getFirstname();
		this.lastname = result.getLastname();
		this.age = result.getAge();
		this.gender = result.getGender();
		this.city = result.getCity();
		this.timeofficialdisplay = result.getTimeofficialdisplay();
		this.timeofficial = result.getTimeofficial();
		this.timestart = result.getTimestart();
		this.laps = result.getLaps();
		this.team = result.getTeam();
		this.splits = new HashMap<Integer, Long>();
		for(Entry<Integer, Split> entry : result.getSplits().entrySet()) {
				if(entry.getValue() != null && entry.getValue().getTime() > 0) {
					splits.put(entry.getKey(), entry.getValue().getTime());
				}
				
		}
	}
	
	private Long id;
	
	private long bib;
	
	private String firstname;
	
	private String lastname;
	
	private Integer age;
	
	private String gender;
		
	private String city;
	
	private String timeofficialdisplay;
	
	private long timeofficial;
	
	private long timestart;
	
	private Integer laps;
	
	private Map<Integer, Long> splits;
	
	private String team;
		
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

	public static String fromRaceResultToDto(RaceResult result ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new RaceResultSyncDto(result));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromRaceResultsToDtoArray(Collection<RaceResult> results ) {
		List <RaceResultSyncDto> dtos = new ArrayList <RaceResultSyncDto>();
		for(RaceResult result : results) {
			dtos.add(new RaceResultSyncDto(result));
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(dtos);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * @return the bib
	 */
	public long getBib() {
		return bib;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}
	
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return the timeofficialdisplay
	 */
	public String getTimeofficialdisplay() {
		return timeofficialdisplay;
	}

	/**
	 * @return the timeofficial
	 */
	public long getTimeofficial() {
		return timeofficial;
	}

	/**
	 * @return the timestart
	 */
	public long getTimestart() {
		return timestart;
	}

	/**
	 * @return the laps
	 */
	public Integer getLaps() {
		return laps;
	}

	/**
	 * @return the splits
	 */
	public Map<Integer, Long> getSplits() {
		return splits;
	}

	/**
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}
	
	
}
