package com.bibsmobile.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEquals
@RooJpaActiveRecord(finders = { "findCartsByUser" })
public class Cart {
	
	public static final int NEW = 0;
	public static final int SAVED = 0;
	public static final int PROCESSING = 0;
	public static final int COMPLETE = 0;
	public static final int REFUND_REQUEST = 0;
	public static final int REFUNDED = 0;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "cart")
	private Set<CartItem> cartItems;
	private double shipping;
	private double total;
	private Date created;
	private Date updated;
	private int status;
	@ManyToOne
	private UserProfile user;
}
