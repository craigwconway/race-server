// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.UserAuthoritiesID;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect UserAuthoritiesID_Roo_Json {
    
    public String UserAuthoritiesID.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String UserAuthoritiesID.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static UserAuthoritiesID UserAuthoritiesID.fromJsonToUserAuthoritiesID(String json) {
        return new JSONDeserializer<UserAuthoritiesID>()
        .use(null, UserAuthoritiesID.class).deserialize(json);
    }
    
    public static String UserAuthoritiesID.toJsonArray(Collection<UserAuthoritiesID> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String UserAuthoritiesID.toJsonArray(Collection<UserAuthoritiesID> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<UserAuthoritiesID> UserAuthoritiesID.fromJsonArrayToUserAuthoritiesIDs(String json) {
        return new JSONDeserializer<List<UserAuthoritiesID>>()
        .use("values", UserAuthoritiesID.class).deserialize(json);
    }
    
}
