// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.UserGroupUserAuthority;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect UserGroupUserAuthority_Roo_Json {
    
    public String UserGroupUserAuthority.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String UserGroupUserAuthority.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static UserGroupUserAuthority UserGroupUserAuthority.fromJsonToUserGroupUserAuthority(String json) {
        return new JSONDeserializer<UserGroupUserAuthority>()
        .use(null, UserGroupUserAuthority.class).deserialize(json);
    }
    
    public static String UserGroupUserAuthority.toJsonArray(Collection<UserGroupUserAuthority> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String UserGroupUserAuthority.toJsonArray(Collection<UserGroupUserAuthority> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<UserGroupUserAuthority> UserGroupUserAuthority.fromJsonArrayToUserGroupUserAuthoritys(String json) {
        return new JSONDeserializer<List<UserGroupUserAuthority>>()
        .use("values", UserGroupUserAuthority.class).deserialize(json);
    }
    
}
