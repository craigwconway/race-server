package com.bibsmobile.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Configurable
@Embeddable
public final class UserAuthoritiesID implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile", referencedColumnName = "id", insertable = false, updatable = false)
    private UserProfile userProfile;

    /**
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_authorities", referencedColumnName = "id", insertable = false, updatable = false)
    private UserAuthority userAuthority;

    public UserAuthoritiesID() {
        super();
    }

    public void setUserAuthority(UserAuthority userAuthority) {
        this.userAuthority = userAuthority;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserAuthoritiesID fromJsonToUserAuthoritiesID(String json) {
        return new JSONDeserializer<UserAuthoritiesID>().use(null, UserAuthoritiesID.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserAuthoritiesID> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserAuthoritiesID> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserAuthoritiesID> fromJsonArrayToUserAuthoritiesIDs(String json) {
        return new JSONDeserializer<List<UserAuthoritiesID>>().use("values", UserAuthoritiesID.class).deserialize(json);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (obj.getClass() != this.getClass()) return false;
        UserAuthoritiesID rhs = (UserAuthoritiesID) obj;
        return new EqualsBuilder().append(this.userAuthority, rhs.userAuthority).append(this.userProfile, rhs.userProfile).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.userAuthority).append(this.userProfile).toHashCode();
    }

    public UserAuthoritiesID(UserProfile userProfile, UserAuthority userAuthority) {
        super();
        this.userProfile = userProfile;
        this.userAuthority = userAuthority;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public UserAuthority getUserAuthority() {
        return this.userAuthority;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
