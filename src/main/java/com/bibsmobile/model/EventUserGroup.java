package com.bibsmobile.model;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(identifierType = EventUserGroupId.class, finders = { "findEventUserGroupsByEvent", "findEventUserGroupsByUserGroup" })
public class EventUserGroup {

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventId", referencedColumnName = "id", insertable = false, updatable = false)
    private Event event;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userGroupId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserGroup userGroup;
}
