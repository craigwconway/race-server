/**
 * 
 */
package com.bibsmobile.service;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Series;
import com.bibsmobile.service.EventSearchCriteria.GeospatialCriteria;
import com.bibsmobile.service.EventSearchCriteria.NameCriteria;

import com.bibsmobile.model.dto.EventViewDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Service for event business stuff
 * @author galen
 * @since 2015-12-31
 */
@Service
public class EventService {
	
	@Autowired
	private EntityManagerFactory emf;

    @Transactional
    public List<Event> compoundSearch(EventSearchCriteria criteria) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();

    	BooleanJunction junction = builder.bool();
    	// Add location search
    	if(criteria.getGeospatialCriteria() != null) {
    		junction.must(addGeospatialCriteria(builder, criteria.getGeospatialCriteria()));
    	}
    	// Add name search
    	if(criteria.getNameCriteria() != null) {
    		junction.must(addNameCriteria(builder, criteria.getNameCriteria()));
    	}
    	// Only search publicly listed events
    	junction.must(restrictVisiblity(builder));
    	org.apache.lucene.search.Query luceneQuery = junction.createQuery();
    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }

    public org.apache.lucene.search.Query restrictVisiblity(QueryBuilder builder) {
    	org.apache.lucene.search.Query luceneQuery = builder
    			.keyword().onField("live")
    			.matching("true")
    			.createQuery();
    	return luceneQuery;
    }
    
    public org.apache.lucene.search.Query addGeospatialCriteria(QueryBuilder builder, GeospatialCriteria criteria) {
    	org.apache.lucene.search.Query luceneQuery = builder
    			.spatial().within(criteria.getRadius(), Unit.KM)
    			.ofLatitude(criteria.getLongitude())
    			.andLongitude(criteria.getLatitude())
    			.createQuery();
    	return luceneQuery;
    }

    public org.apache.lucene.search.Query addNameCriteria(QueryBuilder builder, NameCriteria criteria) {
    	org.apache.lucene.search.Query luceneQuery = builder.keyword()
    			.fuzzy()
    			.onField("name")
    			.matching(criteria.getName())
    			.createQuery();
    	return luceneQuery;
    }    
    
    @Transactional
    public List<Event> geospatialSearch(double longitude, double latitude, double radius) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery = builder
    			.spatial().within(radius, Unit.KM)
    			.ofLatitude(latitude)
    			.andLongitude(longitude)
    			.createQuery();
    	List<Event> events =  fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
		em.getTransaction().commit();
		em.close();
		return events;
    }

	@Transactional
    public ResponseEntity<String> geospatialSearchDto(double longitude, double latitude, double radius) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery = builder
    			.spatial().within(radius, Unit.KM)
    			.ofLatitude(latitude)
    			.andLongitude(longitude)
    			.createQuery();
    	List<Event> events =  fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
		ResponseEntity<String> dto = new ResponseEntity(EventViewDto.fromEventsToDtoArray(events), HttpStatus.OK);
		em.getTransaction().commit();
		em.close();
		return dto;
    }
    
    
    @Transactional
    public List<Event> eventTypeDistanceSearch(String distance) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery = builder.keyword()
    			.onField("types.distance")
    			.matching(distance)
    			.createQuery();
    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }
    
    @Transactional
    public List<Event> eventTypeRacetypeSearch(String racetype) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery = builder.keyword()
    			.onField("types.racetype")
    			.matching(racetype)
    			.createQuery();
    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }
    
    @Transactional
    public List<Event> nameSearch(String name) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery = builder.keyword()
    			.fuzzy()
    			.onField("name")
    			.matching(name)
    			.createQuery();
    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }

	@Transactional
    public ResponseEntity<String> nameSearchInSeriesDto(String name, Series series) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery = builder.keyword()
    			.fuzzy()
    			.onField("name")
    			.matching(name)
    			.createQuery();
    	List<Event> events =  fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
		List<Event> responses = new ArrayList<Event>();
		for(Event event : events) {
    		if(event.getSeries() != null && event.getSeries().getId() == series.getId()) {
    			responses.add(event);
    		}
		}
		ResponseEntity<String> dto = new ResponseEntity(EventViewDto.fromEventsToDtoArray(responses), HttpStatus.OK);
		em.getTransaction().commit();
		em.close();
		return dto;
    }

    
    @Transactional
    public List<Event> distanceRangeQuery(Long low, Long high) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	
    	org.apache.lucene.search.Query luceneQuery;
    	if(low == null) {
    		luceneQuery = builder.range()
    				.onField("types.meters")
    				.below(high)
    				.createQuery();
    	} else if (high == null) {
    		luceneQuery = builder.range()
    				.onField("types.meters")
    				.above(low)
    				.createQuery();
    	} else {
    		luceneQuery = builder.range()
    				.onField("types.meters")
    				.from(low)
    				.to(high)
    				.createQuery();
    	}

    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }

    @Transactional
    public List<Event> seriesQuery(Series series) {
    	EntityManager em = emf.createEntityManager();
    	FullTextEntityManager fullTextEntityManager = 
    		    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    	em.getTransaction().begin();
    	QueryBuilder builder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Event.class).get();
    	org.apache.lucene.search.Query luceneQuery = builder.range()
    			.onField("series.id")
    			.from(series.getId())
    			.to(series.getId())
    			.createQuery();
    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }    
    
}
