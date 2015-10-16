/**
 * 
 */
package com.bibsmobile.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Cheer entity for users. A cheer may be left for a user in context to a particular event.
 * Cheers can be applied based on the CheerTypeEnum.
 * @author galen
 *
 */
@Entity
public class Cheer {
	
	@Id
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@ManyToOne
	private UserProfile target;
	
	@ManyToOne
	private Event event;
	
}

