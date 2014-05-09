// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Cart;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Cart_Roo_Json {
    
    public String Cart.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String Cart.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static Cart Cart.fromJsonToCart(String json) {
        return new JSONDeserializer<Cart>()
        .use(null, Cart.class).deserialize(json);
    }
    
    public static String Cart.toJsonArray(Collection<Cart> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String Cart.toJsonArray(Collection<Cart> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<Cart> Cart.fromJsonArrayToCarts(String json) {
        return new JSONDeserializer<List<Cart>>()
        .use("values", Cart.class).deserialize(json);
    }
    
}