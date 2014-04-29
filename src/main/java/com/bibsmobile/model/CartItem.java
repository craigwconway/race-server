package com.bibsmobile.model;

import java.util.Date;

import javax.persistence.ManyToOne;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooEquals
public class CartItem {

    @ManyToOne 
	private Cart cart;
    
    @ManyToOne
    private EventCartItem eventCartItem;
    
    private int quantity;
	private Date created;
	private Date updated;
	private String comment; 
	private String coupon;
}