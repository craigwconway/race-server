// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.UserGroupUserAuthorityID;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect UserGroupUserAuthorityID_Roo_Json {
    
    public String UserGroupUserAuthorityID.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String UserGroupUserAuthorityID.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static UserGroupUserAuthorityID UserGroupUserAuthorityID.fromJsonToUserGroupUserAuthorityID(String json) {
        return new JSONDeserializer<UserGroupUserAuthorityID>()
        .use(null, UserGroupUserAuthorityID.class).deserialize(json);
    }
    
    public static String UserGroupUserAuthorityID.toJsonArray(Collection<UserGroupUserAuthorityID> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String UserGroupUserAuthorityID.toJsonArray(Collection<UserGroupUserAuthorityID> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<UserGroupUserAuthorityID> UserGroupUserAuthorityID.fromJsonArrayToUserGroupUserAuthorityIDs(String json) {
        return new JSONDeserializer<List<UserGroupUserAuthorityID>>()
        .use("values", UserGroupUserAuthorityID.class).deserialize(json);
    }
    
}
