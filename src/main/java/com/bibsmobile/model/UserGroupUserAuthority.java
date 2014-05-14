package com.bibsmobile.model;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = UserGroupUserAuthorityID.class)
public class UserGroupUserAuthority {

    @MapsId("id")
    @ManyToOne
    @JoinColumn(name = "userGroupId", insertable = false, updatable = false)
    private UserGroup userGroup;

    @MapsId("id")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "userProfileId", insertable = false, updatable = false),
            @JoinColumn(name = "userAuthorityId", insertable = false, updatable = false)})
    private UserAuthorities userAuthorities;

}
