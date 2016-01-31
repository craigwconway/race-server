/**
 * 
 */
package com.bibsmobile.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.AwardCategoryResults;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.RaceResult;

/**
 * This class holds immortal awards configurations. It is a rework of the standards awards cache and should be redone.
 * @author galen
 *
 */
public class AwardsImmortalCache {
    private final static Map cache = new HashMap();

    
    public static void clearAwardsCache(long eventTypeId) {
    	cache.remove(StringUtils.join("awards",eventTypeId));
    	cache.remove(StringUtils.join("overall",eventTypeId));
    	cache.remove(StringUtils.join("class", eventTypeId));
    	cache.remove(StringUtils.join("age_gender", eventTypeId, "M"));
    	cache.remove(StringUtils.join("age_gender", eventTypeId, "F"));
    	cache.remove(StringUtils.join("gender", eventTypeId, "M"));
    	cache.remove(StringUtils.join("gender", eventTypeId, "M"));
    }
    
    public static List<RaceResult> getResultsOverall(long eventTypeId){
    	EventType eventType = EventType.findEventType(eventTypeId);
    	List<RaceResult> results = new ArrayList<RaceResult>();
    	String key = StringUtils.join("overall",eventTypeId);
    	// check cached value
    	if(cache.containsKey(key)){
    		System.out.println("cache hit");
    		results = (ArrayList<RaceResult>) cache.get(key);   
    	}else{
    		System.out.println("cache miss");
        	results = eventType.getAwards(StringUtils.EMPTY, AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 9999);
        	cache.put(key, results);
        	System.out.println("generating overall results for event Type ID: " + eventTypeId);
        	System.out.println(results);
    	}
    	return results;
    }
    
    public static List<RaceResult> getResultsGender(long eventTypeId, String gender){
    	EventType eventType = EventType.findEventType(eventTypeId);
    	List<RaceResult> results = new ArrayList<RaceResult>();
    	String key = StringUtils.join("gender", eventTypeId, gender);
    	// check cached value
    	if(cache.containsKey(key)){
    		System.out.println("cache hit");
    		results = (ArrayList<RaceResult>) cache.get(key);   
    	}else{
    		System.out.println("cache miss");
        	results = eventType.getAwards(gender, AwardCategory.MIN_AGE, AwardCategory.MAX_AGE, 9999);
        	cache.put(key, results);
        	System.out.println("generating overall results for event Type ID: " + eventTypeId);
        	System.out.println(results);
    	}
    	return results;
    }    
    
    public static int getResultOverall(long eventTypeId, long bib){
    	try{
	    	for(int i=0;i<getResultsOverall(eventTypeId).size();i++){
	    		if(getResultsOverall(eventTypeId).get(i).getBib()==bib) return i+1;
	    	}
    	} catch(Exception e) {
    		return 0;
    	}
    	return 0;
    }

    public static int getResultGender(long eventTypeId, long bib, String gender){
    	try{
	    	for(int i=0;i<getResultsGender(eventTypeId, gender).size();i++){
	    		if(getResultsGender(eventTypeId, gender).get(i).getBib()==bib) return i+1;
	    	}
    	} catch(Exception e) {
    		return 0;
    	}
    	return 0;
    }
    
    public static List<AwardCategoryResults> getClassRankings(long eventTypeId){
    	List<AwardCategoryResults> list = new ArrayList<AwardCategoryResults>();
    	EventType eventType = EventType.findEventType(eventTypeId);
    	String key = StringUtils.join("class",eventTypeId);
    	// check cached value
    	if(cache.containsKey(key)){
    		System.out.println("cahce hit");
    		list = (List<AwardCategoryResults>) cache.get(key);
    	}else{
    		System.out.println("cache miss");
    		list = eventType.calculateRank(eventType);
	    	for(AwardCategoryResults c:list){
	    		c.getCategory().setMedal(false); // hack
	    	}
	    	cache.put(key, list);
    	}
    	return list;
    }    
    
    public static List<AwardCategoryResults> getAwards(long eventTypeId){
    	List<AwardCategoryResults> list = new ArrayList<AwardCategoryResults>();
    	EventType eventType = EventType.findEventType(eventTypeId);
    	String key = StringUtils.join("awards",eventTypeId,eventType.getAwardsConfig());
    	// check cached value
    	if(cache.containsKey(key)){
    		System.out.println("cahce hit");
    		list = (List<AwardCategoryResults>) cache.get(key);
    	}else{
    		System.out.println("cache miss");
    		list = eventType.calculateMedals(eventType);
	    	for(AwardCategoryResults c:list){
	    		c.getCategory().setMedal(false); // hack
	    	}
	    	cache.put(key, list);
    	}
    	return list;
    }
    
    public static int getAward(long eventId, long bib){
    	for(AwardCategoryResults a:getAwards(eventId)){
    		for(int i=0;i<a.getResults().size();i++){
    			if(a.getResults().get(i).getBib()==bib) return i+1;
    		}
    	}
    	System.out.println("No award found for bib "+bib);
    	return 0;
    }

    public static int getClassRank(long eventId, long bib){
    	for(AwardCategoryResults a:getClassRankings(eventId)){
    		for(int i=0;i<a.getResults().size();i++){
    			if(a.getResults().get(i).getBib()==bib) return i+1;
    		}
    	}
    	System.out.println("No classrank found for bib "+bib);
    	return 0;
    }
    public static List<AwardCategoryResults> getAgeGenderRankings(long eventTypeId, String gender){
    	List<AwardCategoryResults> results = new ArrayList<AwardCategoryResults>();
    	EventType eventType = EventType.findEventType(eventTypeId);
    	String key = StringUtils.join("age_gender",eventTypeId,gender);
    	// check cached value
    	if(cache.containsKey(key)){
    		System.out.println("cache hit");
    		results = (List<AwardCategoryResults>) cache.get(key); 
    	}else{
    		System.out.println("cache miss");
    		
	    	List<AwardCategory> list = eventType.getAwardCategorys();
	    	List<Long> medalsBibs = new ArrayList<Long>();
	
	    	// if not allow medals in age/gender, collect medals bibs, pass into non-medals
	    	if(!eventType.getAwardsConfig().isAllowMedalsInAgeGenderRankings()){
	        	for(AwardCategoryResults c:eventType.calculateMedals(eventType)){
	        		for(RaceResult r:c.getResults()){
	        			medalsBibs.add(r.getBib());
	        		}
	        	}
	    	}
	    	
			// filter age/gender
	    	for(AwardCategory c:eventType.getAwardCategorys()){
	    		if(!c.isMedal() && c.getGender().toUpperCase().equals(gender.toUpperCase())){
	    			results.add(new AwardCategoryResults(c,
	    					eventType.getAwards(c.getGender(), c.getAgeMin(), c.getAgeMax(), c.getListSize(), medalsBibs)));
	    		}
	    	}
	    	cache.put(key, results);
    	}
    	
    	Collections.sort(results);
    	return results;
    }
}
