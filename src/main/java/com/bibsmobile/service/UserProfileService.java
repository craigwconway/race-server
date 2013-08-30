package com.bibsmobile.service;

import org.springframework.roo.addon.layers.service.RooService;

import com.bibsmobile.model.UserProfile;

@RooService(domainTypes = { com.bibsmobile.model.UserProfile.class })
public interface UserProfileService {
	UserProfile findUserProfilesByUsernameEquals(String username);
}
