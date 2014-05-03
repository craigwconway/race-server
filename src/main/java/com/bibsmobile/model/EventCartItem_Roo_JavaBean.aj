// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.EventCartItemTypeEnum;
import java.util.Date;
import java.util.Set;

privileged aspect EventCartItem_Roo_JavaBean {
    
    public Event EventCartItem.getEvent() {
        return this.event;
    }
    
    public void EventCartItem.setEvent(Event event) {
        this.event = event;
    }
    
    public String EventCartItem.getName() {
        return this.name;
    }
    
    public void EventCartItem.setName(String name) {
        this.name = name;
    }
    
    public String EventCartItem.getDescription() {
        return this.description;
    }
    
    public void EventCartItem.setDescription(String description) {
        this.description = description;
    }
    
    public double EventCartItem.getPrice() {
        return this.price;
    }
    
    public void EventCartItem.setPrice(double price) {
        this.price = price;
    }
    
    public int EventCartItem.getAvailable() {
        return this.available;
    }
    
    public void EventCartItem.setAvailable(int available) {
        this.available = available;
    }
    
    public int EventCartItem.getPurchased() {
        return this.purchased;
    }
    
    public void EventCartItem.setPurchased(int purchased) {
        this.purchased = purchased;
    }
    
    public String EventCartItem.getCoupon() {
        return this.coupon;
    }
    
    public void EventCartItem.setCoupon(String coupon) {
        this.coupon = coupon;
    }
    
    public double EventCartItem.getCouponPrice() {
        return this.couponPrice;
    }
    
    public void EventCartItem.setCouponPrice(double couponPrice) {
        this.couponPrice = couponPrice;
    }
    
    public int EventCartItem.getCouponsAvailable() {
        return this.couponsAvailable;
    }
    
    public void EventCartItem.setCouponsAvailable(int couponsAvailable) {
        this.couponsAvailable = couponsAvailable;
    }
    
    public int EventCartItem.getCouponsUsed() {
        return this.couponsUsed;
    }
    
    public void EventCartItem.setCouponsUsed(int couponsUsed) {
        this.couponsUsed = couponsUsed;
    }
    
    public boolean EventCartItem.isTimeLimit() {
        return this.timeLimit;
    }
    
    public void EventCartItem.setTimeLimit(boolean timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public Date EventCartItem.getTimeStart() {
        return this.timeStart;
    }
    
    public void EventCartItem.setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }
    
    public Date EventCartItem.getTimeEnd() {
        return this.timeEnd;
    }
    
    public void EventCartItem.setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    public EventCartItemTypeEnum EventCartItem.getType() {
        return this.type;
    }
    
    public void EventCartItem.setType(EventCartItemTypeEnum type) {
        this.type = type;
    }
    
    public double EventCartItem.getDonationAmount() {
        return this.donationAmount;
    }
    
    public void EventCartItem.setDonationAmount(double donationAmount) {
        this.donationAmount = donationAmount;
    }
    
    public String EventCartItem.getCharityName() {
        return this.charityName;
    }
    
    public void EventCartItem.setCharityName(String charityName) {
        this.charityName = charityName;
    }
    
    public String EventCartItem.getTshortSizes() {
        return this.tshortSizes;
    }
    
    public void EventCartItem.setTshortSizes(String tshortSizes) {
        this.tshortSizes = tshortSizes;
    }
    
    public String EventCartItem.getTshortColors() {
        return this.tshortColors;
    }
    
    public void EventCartItem.setTshortColors(String tshortColors) {
        this.tshortColors = tshortColors;
    }
    
    public String EventCartItem.getTshortImageUrls() {
        return this.tshortImageUrls;
    }
    
    public void EventCartItem.setTshortImageUrls(String tshortImageUrls) {
        this.tshortImageUrls = tshortImageUrls;
    }
    
    public Set<EventCartItemPriceChange> EventCartItem.getPriceChanges() {
        return this.priceChanges;
    }
    
    public void EventCartItem.setPriceChanges(Set<EventCartItemPriceChange> priceChanges) {
        this.priceChanges = priceChanges;
    }
    
}
