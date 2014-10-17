package com.bibsmobile.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Embeddable
public final class UserGroupUserAuthorityID implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "userGroupId")
    private UserGroup userGroup;

    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "user_profile", insertable = false, updatable = false), @JoinColumn(name = "user_authorities", insertable = false, updatable = false) })
    private UserAuthorities userAuthorities;

    public UserGroupUserAuthorityID() {
    }

    public UserGroupUserAuthorityID(UserGroup userGroup, UserAuthorities userAuthorities) {
        this.userGroup = userGroup;
        this.userAuthorities = userAuthorities;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserGroupUserAuthorityID fromJsonToUserGroupUserAuthorityID(String json) {
        return new JSONDeserializer<UserGroupUserAuthorityID>().use(null, UserGroupUserAuthorityID.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserGroupUserAuthorityID> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserGroupUserAuthorityID> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserGroupUserAuthorityID> fromJsonArrayToUserGroupUserAuthorityIDs(String json) {
        return new JSONDeserializer<List<UserGroupUserAuthorityID>>().use("values", UserGroupUserAuthorityID.class).deserialize(json);
    }

    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public UserAuthorities getUserAuthorities() {
        return this.userAuthorities;
    }

    public void setUserAuthorities(UserAuthorities userAuthorities) {
        this.userAuthorities = userAuthorities;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserGroupUserAuthorityID)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        UserGroupUserAuthorityID rhs = (UserGroupUserAuthorityID) obj;
        return new EqualsBuilder().append(this.userAuthorities, rhs.userAuthorities).append(this.userGroup, rhs.userGroup).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.userAuthorities).append(this.userGroup).toHashCode();
    }
}
