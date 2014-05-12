package com.bibsmobile.model;

import flexjson.JSON;
import flexjson.JSONSerializer;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@SuppressWarnings("serial")
@RooJavaBean
@RooJson
@RooJpaActiveRecord(finders = {"findUserProfilesByUsernameEquals"})
public class UserProfile implements UserDetails {

    private String firstname;
    private String lastname;
    private String city;
    private String state;
    private int age;
    private Date birthdate;
    private String gender;
    private String email;
    private String image;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userProfile", cascade = CascadeType.ALL)
    private Set<UserAuthorities> userAuthorities;
    private String username;
    private String password;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    private String facebookId;
    private String twitterId;
    private String googleId;

    @OneToMany(mappedBy = "userProfile")
    private Set<RaceResult> raceResults;

    @Override
    @JSON(include = false)
    public Set<com.bibsmobile.model.UserAuthority> getAuthorities() {
        Set<UserAuthority> authorities = new HashSet<>();
        for (UserAuthorities uas : userAuthorities) {
            authorities.add(uas.getId().getUserAuthority());
        }
        return authorities;
    }

    @JSON(include = false)
    public List<com.bibsmobile.model.UserAuthority> getNotAddedAuthorities() {
        List<UserAuthority> notAddedAuthorities = new ArrayList<>();
        List<UserAuthority> allAuthorities = UserAuthority.findAllUserAuthoritys();
        List<UserAuthority> assignedAuthorities = new ArrayList<>(getAuthorities());
        for (UserAuthority availableAuthority : allAuthorities) {
            boolean exists = false;
            existedLoop:
            for (UserAuthority assignedAuthority : assignedAuthorities) {
                if (availableAuthority.getId().equals(assignedAuthority.getId())) {
                    exists = true;
                    break existedLoop;
                }
            }
            if (!exists) {
                notAddedAuthorities.add(availableAuthority);
            }
        }
        return notAddedAuthorities;
    }

    @Override
    @JSON(include = false)
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return username;
    }

    public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
}
