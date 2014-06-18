package com.bibsmobile.model;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

@SuppressWarnings({ "serial" })
@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findUserAuthoritysByAuthorityEquals" })
public class UserAuthority implements org.springframework.security.core.GrantedAuthority {

    @NotNull
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userAuthority", cascade = CascadeType.ALL)
    private Set<UserAuthorities> userAuthorities;
}
