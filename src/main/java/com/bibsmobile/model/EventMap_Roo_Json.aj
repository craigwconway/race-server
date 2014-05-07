// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.EventMap;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect EventMap_Roo_Json {
    
    public String EventMap.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String EventMap.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static EventMap EventMap.fromJsonToEventMap(String json) {
        return new JSONDeserializer<EventMap>()
        .use(null, EventMap.class).deserialize(json);
    }
    
    public static String EventMap.toJsonArray(Collection<EventMap> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String EventMap.toJsonArray(Collection<EventMap> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<EventMap> EventMap.fromJsonArrayToEventMaps(String json) {
        return new JSONDeserializer<List<EventMap>>()
        .use("values", EventMap.class).deserialize(json);
    }
    
}
