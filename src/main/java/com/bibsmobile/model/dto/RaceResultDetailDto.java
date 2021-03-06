package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Split;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a data transfer object is used in detailed RaceResult returns. 
 * This should only be used for single results or small collections of results,
 * as it is computationally very expensive.
 * @author galen
 *
 */
public class RaceResultDetailDto {
	
	/**
	 * @apiDefine raceResultDetailDto
	 * @apiSuccess (200) {Number} id unique ID of result
	 * @apiSuccess (200) {Number} bib Bib number of result
	 * @apiSuccess (200) {String} firstname First name of athlete
	 * @apiSuccess (200) {String} lastname Last name of athlete
	 * @apiSuccess (200) {Number} age Age of athlete
	 * @apiSuccess (200) {String} gender Gender of athlete. Either M or F
	 * @apiSuccess (200) {String="a of b","N/A"} rankGender Gender rank of race result. Either "a of b" or "N/A"
	 * @apiSuccess (200) {String="a of b","N/A"} rankOverall Overall rank of race result. Either "a of b" or "N/A"
	 * @apiSuccess (200) {String="a of b","N/A"} rankClass Class rank of race result. Either "a of b" or "N/A"
	 * @apiSuccess (200) {String} city City of origin of athlete
	 * @apiSuccess (200) {String} timeofficialdisplay Official Time of athlete
	 * @apiSuccess (200) {String} team Name of athlete's team
	 * @apiSuccess (200) {Boolean} disqualified Whether or not the runner has been disqualified
	 * @apiSuccess (200) {Boolean} claimed Whether or not this result has been claimed
	 * @apiSuccess (200) {Object[]} images Objects containing each of the images of the athlete
	 * @apiSuccess (200) {Number} images.id id of race image
	 * @apiSuccess (200) {String} images.filePath url of image to display
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
	 * 		"rankGender":"15 of 200",
	 * 		"rankOverall":"33 of 299",
	 * 		"team": "Shrugrunners Run Club",
	 * 		"timeofficialdisplay":"00:23:57",
	 * 		"images":
	 * 		[
	 * 			{
	 * 				"id":1,
	 * 				"filePath": "http://www.mtv.com/crop-images/2013/09/12/drake.jpg"
	 * 			}
	 * 		]
	 * 	}
	 * ]
	 */
	
	
	public RaceResultDetailDto(RaceResult result) {
		this.id = result.getId();
		this.bib = result.getBib();
		this.firstname = result.getFirstname();
		this.lastname = result.getLastname();
		this.gender = result.getGender();
		this.age = result.getAge();
		this.city = result.getCity();
		this.timeofficialdisplay = result.getTimeofficialdisplay();
		this.timeofficialdisplayGun = result.getTimeofficialdisplayGun();
		this.team = result.getTeam();
		this.disqualified = result.isDisqualified();
		this.rankOverall = result.computeOverallRanking();
		this.rankGender = result.computeGenderRanking();
		this.rankClass = result.computeClassRanking();
		this.claimed = (result.getUserProfile() != null);
		if(result.getEventType() != null) {
			this.eventType = new EventTypeDto(result.getEventType());
			//Calculate pace time.
			if(result.getEventType().getMeters() != null && result.getEventType().getMeters() > 0) {
				this.timePace = RaceResult.paceToHumanTime(0, result.getTimediff(), result.getEventType().getMeters());
			}
		}
		if(result.getEvent() != null) {
			this.event = new EventViewResultsDto(result.getEvent());
		}
		splits = new ArrayList<SplitDto>();
		for(Entry<Integer, Split> split : result.getSplits().entrySet()) {
			splits.add(new SplitDto(split.getValue(), split.getKey()));
		}
		images = new ArrayList<RaceImageViewDto>();
		for(RaceImage rawImage : result.getRaceImages()) {
			images.add(new RaceImageViewDto(rawImage));
		}
	}
	
	private Long id;
	private long bib;
	private String firstname;
	private String lastname;
	private Integer age;
	private String city;
	private String gender;
	private String rankGender;
	private String rankOverall;
	private String rankClass;
	private String timeofficialdisplay;
	private String timeofficialdisplayGun;
	private String timePace;
	private String team;
	private boolean claimed;
	private boolean disqualified;
	private EventTypeDto eventType;
	private EventViewResultsDto event;
	private List<RaceImageViewDto> images;
	private List<SplitDto> splits;
		
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
			return mapper.writeValueAsString(new RaceResultDetailDto(result));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromRaceResultsToDtoArray(Collection<RaceResult> results ) {
		List <RaceResultDetailDto> dtos = new ArrayList <RaceResultDetailDto>();
		for(RaceResult result : results) {
			dtos.add(new RaceResultDetailDto(result));
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
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	
	/**
	 * @return the gender ranking
	 */
	public String getRankGender() {
		return rankGender;
	}
	
	/**
	 * @return the age ranking
	 */
	public String getRankOverall() {
		return rankOverall;
	}
	
	/**
	 * @return the class ranking
	 */
	public String getRankClass() {
		return rankClass;
	}

	/**
	 * @return the timeofficialdisplay
	 */
	public String getTimeofficialdisplay() {
		return timeofficialdisplay;
	}
	
	/**
	 * @return the timePace
	 */
	public String getTimePace() {
		return timePace;
	}

	/**
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}
	
	/**
	 * @return the claimed
	 */
	public boolean isClaimed() {
		return claimed;
	}

	/**
	 * @return the disqualified
	 */
	public boolean isDisqualified() {
		return disqualified;
	}

	/**
	 * @return the eventType
	 */
	public EventTypeDto getEventType() {
		return eventType;
	}
	
	/**
	 * @return the event
	 */
	public EventViewResultsDto getEvent() {
		return event;
	}

	/**
	 * @return Race Image DTO versions of views containing this athlete.
	 */
	public List<RaceImageViewDto> getImages() {
		return images;
	}
	/**
	 * @return the timeofficialdisplayGun
	 */
	public String getTimeofficialdisplayGun() {
		return timeofficialdisplayGun;
	}

	/**
	 * @return the splits
	 */
	public List<SplitDto> getSplits() {
		return splits;
	}

}
