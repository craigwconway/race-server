package com.bibsmobile.model;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

//company or team
@RooJavaBean
@RooToString
@RooEquals
@RooJson
@RooJpaActiveRecord
public class UserGroup {

    /**
     */
    private String name;

    private int bibWrites;

    /**
     */
    @NotNull
    @Enumerated
    private UserGroupType groupType;

    @OneToMany(fetch = FetchType.LAZY, cascade = {javax.persistence.CascadeType.ALL}, mappedBy = "eventUserGroup")
    private Set<Event> events;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userGroup", cascade = CascadeType.ALL)
    private Set<UserGroupUserAuthority> userGroupUserAuthorities;
}
