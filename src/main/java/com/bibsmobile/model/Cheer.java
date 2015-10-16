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
	
	private CheerTypeEnum type;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the target
	 */
	public UserProfile getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(UserProfile target) {
		this.target = target;
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return the type
	 */
	public CheerTypeEnum getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(CheerTypeEnum type) {
		this.type = type;
	}
}

