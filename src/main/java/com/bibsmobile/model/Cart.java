package com.bibsmobile.model;

import flexjson.JSONDeserializer;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(finders = {"findCartsByUser"})
public class Cart {

    public static final int NEW = 0;
    public static final int SAVED = 1;
    public static final int PROCESSING = 2;
    public static final int COMPLETE = 3;
    public static final int REFUND_REQUEST = 4;
    public static final int REFUNDED = 5;

    @ManyToOne
    private UserProfile user;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "cart")
    private List<CartItem> cartItems;

    private double shipping;
    private double total;
    private Date created;
    private Date updated;
    private int status;
    private String coupons;

    public static Cart fromJsonToCartWithUser(String json) {
        return new JSONDeserializer<Cart>().use(null, Cart.class).use("user", UserProfile.class).deserialize(json);
    }
}
