// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.CartItem;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect CartItem_Roo_Equals {
    
    public boolean CartItem.equals(Object obj) {
        if (!(obj instanceof CartItem)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CartItem rhs = (CartItem) obj;
        return new EqualsBuilder().append(cart, rhs.cart).append(comment, rhs.comment).append(coupon, rhs.coupon).append(created, rhs.created).append(eventCartItem, rhs.eventCartItem).append(exported, rhs.exported).append(id, rhs.id).append(quantity, rhs.quantity).append(updated, rhs.updated).append(userProfile, rhs.userProfile).isEquals();
    }
    
    public int CartItem.hashCode() {
        return new HashCodeBuilder().append(cart).append(comment).append(coupon).append(created).append(eventCartItem).append(exported).append(id).append(quantity).append(updated).append(userProfile).toHashCode();
    }
    
}
