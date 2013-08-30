// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.service;

import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.UserProfileService;
import java.util.List;

privileged aspect UserProfileService_Roo_Service {
    
    public abstract long UserProfileService.countAllUserProfiles();    
    public abstract void UserProfileService.deleteUserProfile(UserProfile userProfile);    
    public abstract UserProfile UserProfileService.findUserProfile(Long id);    
    public abstract List<UserProfile> UserProfileService.findAllUserProfiles();    
    public abstract List<UserProfile> UserProfileService.findUserProfileEntries(int firstResult, int maxResults);    
    public abstract void UserProfileService.saveUserProfile(UserProfile userProfile);    
    public abstract UserProfile UserProfileService.updateUserProfile(UserProfile userProfile);    
}
