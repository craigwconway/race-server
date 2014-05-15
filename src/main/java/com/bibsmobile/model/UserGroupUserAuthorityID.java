package com.bibsmobile.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.jpa.identifier.RooIdentifier;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@Embeddable
public final class UserGroupUserAuthorityID implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "userGroupId")
    private UserGroup userGroup;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "userProfileId", insertable = false, updatable = false),
            @JoinColumn(name = "userAuthorityId", insertable = false, updatable = false)})
    private UserAuthorities userAuthorities;

    public UserGroupUserAuthorityID(){
    }

    public UserGroupUserAuthorityID(UserGroup userGroup, UserAuthorities userAuthorities) {
        this.userGroup = userGroup;
        this.userAuthorities = userAuthorities;
    }

}
