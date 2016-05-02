/**
 * 
 */
package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bibsmobile.model.AwardCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author galen
 *
 */
public class AwardCategoryViewDto {
	
	private Long id;
	private String name;
	private String gender;
	private int ageMin;
	private int ageMax;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @return the ageMin
	 */
	public int getAgeMin() {
		return ageMin;
	}
	/**
	 * @return the ageMax
	 */
	public int getAgeMax() {
		return ageMax;
	}
	
	public AwardCategoryViewDto(AwardCategory awardCategory) {
		this.id = awardCategory.getId();
		this.name = awardCategory.getName();
		this.gender = awardCategory.getGender();
		this.ageMin = awardCategory.getAgeMin();
		this.ageMax = awardCategory.getAgeMax();
	}
	
	public static String fromAwardCategoriesToJson(Collection <AwardCategory> categories) {
		List<AwardCategoryViewDto> dtos = new ArrayList<AwardCategoryViewDto>();
		for(AwardCategory category : categories) {
			dtos.add(new AwardCategoryViewDto(category));
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(dtos);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
