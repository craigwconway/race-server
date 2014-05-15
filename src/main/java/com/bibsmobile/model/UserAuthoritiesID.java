package com.bibsmobile.model;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.jpa.identifier.RooIdentifier;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@RooToString
@RooEquals
@RooJson
@RooIdentifier
public final class UserAuthoritiesID implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userProfileId", referencedColumnName = "id", insertable=false, updatable=false)
    private UserProfile userProfile;

    /**
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userAuthorityId", referencedColumnName = "id", insertable=false, updatable=false)
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
