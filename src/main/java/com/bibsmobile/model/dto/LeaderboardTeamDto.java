/**
 * 
 */
package com.bibsmobile.model.dto;

import java.util.Collection;
import java.util.List;

import com.bibsmobile.model.RaceResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author galen
 *
 */
public class LeaderboardTeamDto {
	
	private String name;
	private List<RaceResultViewDto> results;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the results
	 */
	public List<RaceResultViewDto> getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(List<RaceResultViewDto> results) {
		this.results = results;
	}
	
	/**
	 * Default constructor
	 */
	public LeaderboardTeamDto() {};

	public LeaderboardTeamDto(List<RaceResult> results, String name) {
		this.name = name;
		this.results = RaceResultViewDto.fromRaceResultsToRawDtoArray(results);
	}
	
	public static String toJsonArray(Collection<LeaderboardTeamDto> leaderboards) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(leaderboards);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
}
