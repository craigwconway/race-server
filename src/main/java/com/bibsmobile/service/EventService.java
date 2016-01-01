/**
 * 
 */
package com.bibsmobile.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.Event;

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
    	return fullTextEntityManager.createFullTextQuery(luceneQuery, Event.class).getResultList();
    }
}
