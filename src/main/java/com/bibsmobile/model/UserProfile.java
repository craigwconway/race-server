package com.bibsmobile.model;
import flexjson.JSON;
import flexjson.JSONSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
@RooJavaBean
@RooJson
@RooJpaActiveRecord(finders = { "findUserProfilesByUsernameEquals", "findUserProfilesByForgotPasswordCodeEquals", "findUserProfilesByEmailEquals" })
public class UserProfile implements UserDetails {

    private String firstname;

    private String lastname;

    private String city;

    private String state;

    private int age;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
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

    private String phone;

    private String addressLine1;

    private String addressLine2;

    private String zipCode;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private String hearFrom;

    @OneToOne(fetch = FetchType.LAZY)
    private CartItem cartItem;

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
    public List<UserAuthority> getNotAddedAuthorities() {
        List<UserAuthority> notAddedAuthorities = new ArrayList<>();
        List<UserAuthority> allAuthorities = UserAuthority.findAllUserAuthoritys();
        List<UserAuthority> assignedAuthorities = new ArrayList<>(getAuthorities());
        for (UserAuthority availableAuthority : allAuthorities) {
            boolean exists = false;
            existedLoop: for (UserAuthority assignedAuthority : assignedAuthorities) {
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

    public String getFullName() {
        return new StringBuilder(StringUtils.isEmpty(firstname) ? StringUtils.EMPTY : firstname).append(" ").append(StringUtils.isEmpty(lastname) ? StringUtils.EMPTY : lastname).toString();
    }

    /**
     */
    private String forgotPasswordCode;
}
