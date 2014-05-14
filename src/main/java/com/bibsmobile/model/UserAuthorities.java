package com.bibsmobile.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierType = UserAuthoritiesID.class, finders = { "findUserAuthoritiesesByUserProfile" })
public class UserAuthorities {

    @MapsId("id")
    @ManyToOne
    @JoinColumn(name = "userProfileId", insertable = false, updatable = false)
    private UserProfile userProfile;

    @MapsId("id")
    @ManyToOne
    @JoinColumn(name = "userAuthorityId", insertable = false, updatable = false)
    private UserAuthority userAuthority;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userAuthorities", cascade = CascadeType.ALL)
    private Set<UserGroupUserAuthority> userGroupUserAuthorities;

}
