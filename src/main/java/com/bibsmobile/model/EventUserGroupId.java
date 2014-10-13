package com.bibsmobile.model;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.jpa.identifier.RooIdentifier;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@RooToString
@RooEquals
@RooJson
@RooIdentifier
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
}
