// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;
import java.util.List;

privileged aspect UserGroup_Roo_JavaBean {
    
    public String UserGroup.getName() {
        return this.name;
    }
    
    public void UserGroup.setName(String name) {
        this.name = name;
    }
    
    public List<UserProfile> UserGroup.getUsers() {
        return this.users;
    }
    
    public void UserGroup.setUsers(List<UserProfile> users) {
        this.users = users;
    }
    
    public List<Event> UserGroup.getEvents() {
        return this.events;
    }
    
    public void UserGroup.setEvents(List<Event> events) {
        this.events = events;
    }
    
}
