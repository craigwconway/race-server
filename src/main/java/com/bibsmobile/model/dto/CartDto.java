package com.bibsmobile.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a data transfer object used in conjunction with {@link Event} objects. 
 * This is for use in list views, and has information associated 
 * with selecting an event to learn more about.
 * @author galen
 *
 */
public class CartDto {
	
	/**
	 * @apiDefine cartDto
	 * @apiSuccess (200) {Number} id Id of cart
	 * @apiSuccessExample Single Result Found
	 * [
	 * 	{
	 * 		"id":1,
	 * 		"name":"Kings Canyon Critical Mass",
	 * 		"timeStartLocal":"12/01/2015 12:44:20 AM",
	 * 		"charity":"Ancient Aliens",
	 * 		"city":"San Francisco",
	 * 		"state":"CA",
	 * 		"address":"904 Haight St",
	 * 		"country":"US",
	 * 		"eventTypes":
	 * 		[
	 * 			{
	 * 				"id":1,
	 * 				"typeName":"Run with the Lizards 5k",
	 * 				"distance":"5k",
	 * 				"racetype":"Running",
	 * 				"timeStartLocal":"12/01/2015 12:44:21 AM"
	 * 			}
	 * 		],
	 * 		"organizer":null
	 * 	}
	 * ]
	 */
	
	/**
	 * Constructs a DTO object from a raw {@link com.bibsmobile.model.Cart Cart} object.
	 * @param event
	 */
	CartDto(Cart cart) {
		this.id = cart.getId();
	}
	
	private Long id;
	
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

	/*
	public static String fromEventToDto(Event event ) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(new CartDto(event));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	
	public static String fromEventsToDtoArray(Collection<Event> events ) {
		List <CartDto> dtos = new ArrayList <CartDto>();
		for(Event event : events) {
			dtos.add(new CartDto(event));
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
	*/

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	
}
