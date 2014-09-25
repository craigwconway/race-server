package com.bibsmobile.service;

import java.util.List;
import com.bibsmobile.model.UserProfile;

public interface UserProfileService {
	UserProfile findUserProfilesByUsernameEquals(String username);

	public abstract long countAllUserProfiles();


	public abstract void deleteUserProfile(UserProfile userProfile);


	public abstract UserProfile findUserProfile(Long id);


	public abstract List<UserProfile> findAllUserProfiles();


	public abstract List<UserProfile> findUserProfileEntries(int firstResult, int maxResults);


	public abstract void saveUserProfile(UserProfile userProfile);


	public abstract UserProfile updateUserProfile(UserProfile userProfile);

}
