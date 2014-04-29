// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Cart;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect Cart_Roo_Equals {
    
    public boolean Cart.equals(Object obj) {
        if (!(obj instanceof Cart)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Cart rhs = (Cart) obj;
        return new EqualsBuilder().append(coupons, rhs.coupons).append(created, rhs.created).append(id, rhs.id).append(shipping, rhs.shipping).append(status, rhs.status).append(total, rhs.total).append(updated, rhs.updated).append(user, rhs.user).isEquals();
    }
    
    public int Cart.hashCode() {
        return new HashCodeBuilder().append(coupons).append(created).append(id).append(shipping).append(status).append(total).append(updated).append(user).toHashCode();
    }
    
}