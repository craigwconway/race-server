// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthorityID;

privileged aspect UserGroupUserAuthorityID_Roo_JavaBean {
    
    public UserGroup UserGroupUserAuthorityID.getUserGroup() {
        return this.userGroup;
    }
    
    public void UserGroupUserAuthorityID.setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
    
    public UserAuthorities UserGroupUserAuthorityID.getUserAuthorities() {
        return this.userAuthorities;
    }
    
    public void UserGroupUserAuthorityID.setUserAuthorities(UserAuthorities userAuthorities) {
        this.userAuthorities = userAuthorities;
    }
    
}