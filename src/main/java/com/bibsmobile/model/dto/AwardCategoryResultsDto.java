/**
 * 
 */
package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bibsmobile.model.AwardCategoryResults;
import com.bibsmobile.model.RaceResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author galen
 *
 */
public class AwardCategoryResultsDto {
	
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
	public AwardCategoryResultsDto() {};

	public AwardCategoryResultsDto(List<RaceResult> results, String name) {
		this.name = name;
		this.results = RaceResultViewDto.fromRaceResultsToRawDtoArray(results);
	}
	
	public AwardCategoryResultsDto(AwardCategoryResults results) {
		this.name = results.getCategory().getName();
		this.results = RaceResultViewDto.fromRaceResultsToRawDtoArray(results.getResults());
	}
	
	public static List<AwardCategoryResultsDto> fromCategories (List<AwardCategoryResults> categories) {
		List<AwardCategoryResultsDto> dtos = new ArrayList<AwardCategoryResultsDto>();
		for(AwardCategoryResults category : categories) {
			AwardCategoryResultsDto dto = new AwardCategoryResultsDto(category);
			dtos.add(dto);
		}
		return dtos;
	}
	
	public static String toJsonArray(Collection<AwardCategoryResultsDto> leaderboards) {
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
