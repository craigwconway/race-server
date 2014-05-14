package com.bibsmobile.model;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.jpa.identifier.RooIdentifier;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@RooToString
@RooEquals
@RooIdentifier
public final class UserAuthoritiesID implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     */
    @ManyToOne
    @JoinColumn(name = "userProfileId", insertable=false, updatable=false)
    private UserProfile userProfile;

    /**
     */
    @ManyToOne
    @JoinColumn(name = "userAuthorityId", insertable=false, updatable=false)
    private UserAuthority userAuthority;

    public UserAuthoritiesID(){
    }

    public void setUserAuthority(UserAuthority userAuthority) {
        this.userAuthority = userAuthority;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
