package com.bibsmobile.model.wrapper;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.bibsmobile.model.UserProfile;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Created by Jevgeni on 18.06.2014.
 */
public class UserProfileWrapper {
    private UserProfile userProfile;
    private String userGroupName;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserProfileWrapper)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        UserProfileWrapper rhs = (UserProfileWrapper) obj;
        return new EqualsBuilder().append(this.userGroupName, rhs.userGroupName).append(this.userProfile, rhs.userProfile).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.userGroupName).append(this.userProfile).toHashCode();
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static UserProfileWrapper fromJsonToUserProfileWrapper(String json) {
        return new JSONDeserializer<UserProfileWrapper>().use(null, UserProfileWrapper.class).deserialize(json);
    }

    public static String toJsonArray(Collection<UserProfileWrapper> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<UserProfileWrapper> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<UserProfileWrapper> fromJsonArrayToUserProfileWrappers(String json) {
        return new JSONDeserializer<List<UserProfileWrapper>>().use("values", UserProfileWrapper.class).deserialize(json);
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserGroupName() {
        return this.userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
