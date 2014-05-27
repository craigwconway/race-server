package com.bibsmobile.model;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import flexjson.JSONSerializer;

@RooJavaBean
@RooToString
@RooEquals
@RooJpaActiveRecord(finders = { "findUserGroupsByNameEquals" })
public class UserGroup {

    private String name;

    private int bibWrites;

    public String toJson() {
        return new JSONSerializer().exclude("*.class", "userProfiles", "events").serialize(this);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "userGroup")
    private Set<Event> events;

    @OneToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL }, mappedBy = "userGroup")
    private Set<UserProfile> userProfiles;
}
