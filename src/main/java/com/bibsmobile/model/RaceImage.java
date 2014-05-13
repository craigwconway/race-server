package com.bibsmobile.model;

import java.util.List;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findRaceImagesByEvent", "findRaceImagesByRaceResults"})
public class RaceImage {
	
	@NotNull
	String filePath;

    @ManyToOne
	RaceResult raceResult;

    @ManyToOne 
	Event event;

    @ManyToOne
	UserProfile userProfile;
	
	boolean nonPublic;
	
	public RaceImage(){}
	
	public RaceImage(String filePath, long eventId){
		this.filePath = filePath;
		this.event = Event.findEvent(eventId);
	}
	
	public RaceImage(String filePath, long eventId, String bib){
		this.filePath = filePath;
		this.event = Event.findEvent(eventId);
		this.raceResult = RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getSingleResult();
	}
	
	public RaceImage(String filePath, long eventId, String[] bibs){
		this.filePath = filePath;
		this.event = Event.findEvent(eventId);
		if (this.event != null) {
			this.raceResult = (RaceResult) RaceResult.findRaceResultsByEventAndMultipleBibs(event, bibs);
		} else {
			throw new ObjectRetrievalFailureException(Event.class, eventId);
		}		
	}
}
