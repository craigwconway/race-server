package com.bibsmobile.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEquals
@RooJpaActiveRecord(finders = { "findEventCartItemsByEvent" })
public class EventCartItem {

    @ManyToOne 
	private Event event;
    
	private String description;
	private double price;
	private int available;
	private int purchased;
	private String coupon;
	private double couponPrice;
	private int couponsAvailable;
	private int couponsUsed;
	private boolean timeLimit;
	
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="MM/dd/yyyy h:mm:ss a")
	private Date timeStart;
    
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="MM/dd/yyyy h:mm:ss a")
	private Date timeEnd;
	
}