// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Cart;
import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.EventCartItem;
import java.util.Date;

privileged aspect CartItem_Roo_JavaBean {
    
    public Cart CartItem.getCart() {
        return this.cart;
    }
    
    public void CartItem.setCart(Cart cart) {
        this.cart = cart;
    }
    
    public EventCartItem CartItem.getEventCartItem() {
        return this.eventCartItem;
    }
    
    public void CartItem.setEventCartItem(EventCartItem eventCartItem) {
        this.eventCartItem = eventCartItem;
    }
    
    public int CartItem.getQuantity() {
        return this.quantity;
    }
    
    public void CartItem.setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Date CartItem.getCreated() {
        return this.created;
    }
    
    public void CartItem.setCreated(Date created) {
        this.created = created;
    }
    
    public Date CartItem.getUpdated() {
        return this.updated;
    }
    
    public void CartItem.setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public String CartItem.getComment() {
        return this.comment;
    }
    
    public void CartItem.setComment(String comment) {
        this.comment = comment;
    }
    
    public String CartItem.getCoupon() {
        return this.coupon;
    }
    
    public void CartItem.setCoupon(String coupon) {
        this.coupon = coupon;
    }
    
}