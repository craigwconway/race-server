package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.PictureHashtag;
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
public class RaceImageDetailsDto {
	
	/**
	 * @apiDefine raceImageDetailsDto
	 * @apiSuccess (200) {Number} id unique ID of image
	 * @apiSuccess (200) {Number} filePath URL of image
	 * @apiSuccess (200) {String[]} hashtags Hashtags the image is tagged with
	 * @apiSuccess (200) {Object[]} results RaceResultViewDto Object returned
	 * @apiSuccessExample Single Image Found
	 * {
	 * 	"id":1,
	 * 	"filePath": "http://www.mtv.com/crop-images/2013/09/12/drake.jpg"
	 * 	"hashtags": ["drake", "willyoungplaylist", "zapposlabs"]
	 * 	"results": 
	 * 	[
	 * 		{
	 * 			"id":1,
	 * 			"bib": 22,
	 * 			"firstname":"Galen",
	 * 			"lastname":"Danziger",
	 * 			"city":"San Francisco",
	 * 			"age":24,
	 * 			"gender":"M",
	 * 			"team": "Shrugrunners Run Club",
	 * 			"timeofficialdisplay":"00:23:57"
	 * 		}
	 * 	]
	 * }
	 */
	
	
	RaceImageDetailsDto(RaceImage image) {
		this.id = image.getId();
		this.filePath = image.getFilePath();
		for(PictureHashtag hashtag : image.getPictureTypes()) {
			this.hashtags.add(hashtag.getPictureHashtag());
		}
		for(RaceResult result : image.getRaceResults()) {
			this.results.add(new RaceResultViewDto(result));
		}
	}
	
	private Long id;
	
	private String filePath;
	
	private List<String> hashtags = new ArrayList<String>();
	
	private List<RaceResultViewDto> results = new ArrayList<RaceResultViewDto>();
		
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
			return mapper.writeValueAsString(new RaceImageDetailsDto(image));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromRaceImagesToDtoArray(Collection<RaceImage> images ) {
		List <RaceImageDetailsDto> dtos = new ArrayList <RaceImageDetailsDto>();
		for(RaceImage image : images) {
			dtos.add(new RaceImageDetailsDto(image));
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

	/**
	 * @return the hashtags
	 */
	public List<String> getHashtags() {
		return hashtags;
	}

	/**
	 * @return the results
	 */
	public List<RaceResultViewDto> getResults() {
		return results;
	}
	
	
}
