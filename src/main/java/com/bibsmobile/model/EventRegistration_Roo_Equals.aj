// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.EventRegistration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect EventRegistration_Roo_Equals {
    
    public boolean EventRegistration.equals(Object obj) {
        if (!(obj instanceof EventRegistration)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EventRegistration rhs = (EventRegistration) obj;
        return new EqualsBuilder().append(cart, rhs.cart).append(description, rhs.description).append(event, rhs.event).append(id, rhs.id).append(price, rhs.price).isEquals();
    }
    
    public int EventRegistration.hashCode() {
        return new HashCodeBuilder().append(cart).append(description).append(event).append(id).append(price).toHashCode();
    }
    
}
