package com.bibsmobile.model;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord(identifierType = UserAuthoritiesID.class, finders = { "findUserAuthoritiesesByUserProfile", "findUserAuthoritiesesByUserAuthority" })
public class UserAuthorities {

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userProfileId", referencedColumnName = "id",  insertable = false, updatable = false)
    private UserProfile userProfile;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userAuthorityId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserAuthority userAuthority;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userAuthorities", cascade = CascadeType.ALL)
    private Set<UserGroupUserAuthority> userGroupUserAuthorities;
}
