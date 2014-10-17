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
public class EventUserGroupId implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventId", referencedColumnName = "id", insertable = false, updatable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userGroupId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserGroup userGroup;

    public EventUserGroupId() {
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public EventUserGroupId(Event event, UserGroup userGroup) {
        super();
        this.event = event;
        this.userGroup = userGroup;
    }

    public Event getEvent() {
        return this.event;
    }

    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

    public String toJson(String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(this);
    }

    public static EventUserGroupId fromJsonToEventUserGroupId(String json) {
        return new JSONDeserializer<EventUserGroupId>().use(null, EventUserGroupId.class).deserialize(json);
    }

    public static String toJsonArray(Collection<EventUserGroupId> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

    public static String toJsonArray(Collection<EventUserGroupId> collection, String[] fields) {
        return new JSONSerializer().include(fields).exclude("*.class").serialize(collection);
    }

    public static Collection<EventUserGroupId> fromJsonArrayToEventUserGroupIds(String json) {
        return new JSONDeserializer<List<EventUserGroupId>>().use("values", EventUserGroupId.class).deserialize(json);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EventUserGroupId)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EventUserGroupId rhs = (EventUserGroupId) obj;
        return new EqualsBuilder().append(this.event, rhs.event).append(this.userGroup, rhs.userGroup).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.event).append(this.userGroup).toHashCode();
    }
}
