package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a data transfer object used for list view returns of many RaceResults.
 * It does not have as much information, however.
 * @author galen
 *
 */
public class RaceImageViewDto {
	
	/**
	 * @apiDefine raceImageViewDto
	 * @apiSuccess (200) {Number} id unique ID of image
	 * @apiSuccess (200) {Number} filePath URL of image
	 * @apiSuccessExample Single Image Found
	 * {
	 * 	"id":1,
	 * 	"filePath": "http://www.mtv.com/crop-images/2013/09/12/drake.jpg"
	 * }
	 */
	
	
	RaceImageViewDto(RaceImage image) {
		this.id = image.getId();
		this.filePath = image.getFilePath();
	}
	
	private Long id;
	
	private String filePath;
		
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

	public static String fromRaceImageToDto(RaceImage image ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new RaceImageViewDto(image));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromRaceImagesToDtoArray(Collection<RaceImage> images ) {
		List <RaceImageViewDto> dtos = new ArrayList <RaceImageViewDto>();
		for(RaceImage image : images) {
			dtos.add(new RaceImageViewDto(image));
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
	 * @return the firstname
	 */
	public String getFilePath() {
		return filePath;
	}

	
	
}
