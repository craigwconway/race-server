package com.bibsmobile.model.wrapper;

import java.util.Collection;
import java.util.List;

import com.bibsmobile.model.UserGroup;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemPriceChange;
import com.bibsmobile.model.UserProfile;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CartItemReqWrapper {
    private UserProfile userProfile;
    private UserGroup team;

    private boolean newItem;
    private String size;
    private String color;
    private long priceChangeId;
    

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserGroup getTeam() {
        return team;
    }

    public void setTeam(UserGroup team) {
        this.team = team;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }
    
    public EventCartItemPriceChange getEventCartItemPriceChange() {
    	return EventCartItemPriceChange.findEventCartItemPriceChange(this.priceChangeId);
    }
    
    public void setPriceChangeId(long priceChangeId) {
    	this.priceChangeId = priceChangeId;
    }
    
    public long getPriceChangeId(long priceChangeId) {
    	return this.priceChangeId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        CartItemReqWrapper rhs = (CartItemReqWrapper) obj;
        return new EqualsBuilder().append(this.color, rhs.color).append(this.size, rhs.size).append(this.userProfile, rhs.userProfile).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.color).append(this.size).append(this.userProfile).toHashCode();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static CartItemReqWrapper fromJsonToCartItemReqWrapper(String json) {
        return new JSONDeserializer<CartItemReqWrapper>().use(null, CartItemReqWrapper.class).deserialize(json);
    }

    public static String toJsonArray(Collection<CartItemReqWrapper> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<CartItemReqWrapper> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<CartItemReqWrapper> fromJsonArrayToCartItemReqWrappers(String json) {
        return new JSONDeserializer<List<CartItemReqWrapper>>().use("values", CartItemReqWrapper.class).deserialize(json);
    }
}
