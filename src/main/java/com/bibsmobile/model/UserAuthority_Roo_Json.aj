// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.UserAuthority;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect UserAuthority_Roo_Json {
    
    public String UserAuthority.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String UserAuthority.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static UserAuthority UserAuthority.fromJsonToUserAuthority(String json) {
        return new JSONDeserializer<UserAuthority>()
        .use(null, UserAuthority.class).deserialize(json);
    }
    
    public static String UserAuthority.toJsonArray(Collection<UserAuthority> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String UserAuthority.toJsonArray(Collection<UserAuthority> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<UserAuthority> UserAuthority.fromJsonArrayToUserAuthoritys(String json) {
        return new JSONDeserializer<List<UserAuthority>>()
        .use("values", UserAuthority.class).deserialize(json);
    }
    
}