package com.bibsmobile.model;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(finders = { "findCartItemsByEventCartItem", "findCartItemsByCreatedGreaterThan" })
public class CartItem {

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private EventCartItem eventCartItem;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "cartItem", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    private int quantity;

    private Date created;

    private Date updated;

    private String comment;

    private String coupon;
}
