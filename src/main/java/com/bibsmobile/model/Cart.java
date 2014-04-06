package com.bibsmobile.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooEquals
public class Cart {
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "cart")
	private Set<CartItem> cartItems;
	private double shipping;
	private double total;
	private Date created;
	private Date updated;
	private int status; 
}
