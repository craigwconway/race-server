// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;
import java.util.Set;

privileged aspect UserGroup_Roo_JavaBean {
    
    public String UserGroup.getName() {
        return this.name;
    }
    
    public void UserGroup.setName(String name) {
        this.name = name;
    }
    
    public int UserGroup.getBibWrites() {
        return this.bibWrites;
    }
    
    public void UserGroup.setBibWrites(int bibWrites) {
        this.bibWrites = bibWrites;
    }
    
    public Set<UserProfile> UserGroup.getUserProfiles() {
        return this.userProfiles;
    }
    
    public void UserGroup.setUserProfiles(Set<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }
    
    public Set<Event> UserGroup.getEvents() {
        return this.events;
    }
    
    public void UserGroup.setEvents(Set<Event> events) {
        this.events = events;
    }
    
}
