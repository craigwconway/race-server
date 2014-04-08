package com.bibsmobile.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
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
@RooJpaActiveRecord
@RooEquals
public class CartCheckout {

	private Cart cart;
	
	private enum status{ NEW, PROCESSING, COMPLETED, WISHLIST }; 
	
	private enum paymentType {
		
	}
	
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="MM/dd/yyyy h:mm:ss a")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern="MM/dd/yyyy h:mm:ss a") 
    private Date updated;
}
