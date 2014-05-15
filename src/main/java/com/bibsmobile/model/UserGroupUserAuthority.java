package com.bibsmobile.model;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(identifierType = UserGroupUserAuthorityID.class, finders = { "findUserGroupUserAuthoritysByUserAuthorities", "findUserGroupUserAuthoritysByUserGroup", "findUserGroupUserAuthoritysByUserGroupAndUserAuthorities" })
public class UserGroupUserAuthority {

    @MapsId("id")
    @ManyToOne
    @JoinColumn(name = "userGroupId", insertable = false, updatable = false)
    private UserGroup userGroup;

    @MapsId("id")
    @ManyToOne
    @JoinColumns({ @javax.persistence.JoinColumn(name = "userProfileId", insertable = false, updatable = false), @javax.persistence.JoinColumn(name = "userAuthorityId", insertable = false, updatable = false) })
    private UserAuthorities userAuthorities;
}
