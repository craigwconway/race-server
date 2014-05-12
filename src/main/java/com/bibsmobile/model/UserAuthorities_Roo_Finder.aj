// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserProfile;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect UserAuthorities_Roo_Finder {
    
    public static Long UserAuthorities.countFindUserAuthoritiesesByUserProfile(UserProfile userProfile) {
        if (userProfile == null) throw new IllegalArgumentException("The userProfile argument is required");
        EntityManager em = UserAuthorities.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM UserAuthorities AS o WHERE o.userProfile = :userProfile", Long.class);
        q.setParameter("userProfile", userProfile);
        return ((Long) q.getSingleResult());
    }
    
    public static TypedQuery<UserAuthorities> UserAuthorities.findUserAuthoritiesesByUserProfile(UserProfile userProfile) {
        if (userProfile == null) throw new IllegalArgumentException("The userProfile argument is required");
        EntityManager em = UserAuthorities.entityManager();
        TypedQuery<UserAuthorities> q = em.createQuery("SELECT o FROM UserAuthorities AS o WHERE o.userProfile = :userProfile", UserAuthorities.class);
        q.setParameter("userProfile", userProfile);
        return q;
    }
    
    public static TypedQuery<UserAuthorities> UserAuthorities.findUserAuthoritiesesByUserProfile(UserProfile userProfile, String sortFieldName, String sortOrder) {
        if (userProfile == null) throw new IllegalArgumentException("The userProfile argument is required");
        EntityManager em = UserAuthorities.entityManager();
        String jpaQuery = "SELECT o FROM UserAuthorities AS o WHERE o.userProfile = :userProfile";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserAuthorities> q = em.createQuery(jpaQuery, UserAuthorities.class);
        q.setParameter("userProfile", userProfile);
        return q;
    }
    
}
