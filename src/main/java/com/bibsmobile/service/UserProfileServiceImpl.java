package com.bibsmobile.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.UserProfile;


//@Service
//@Transactional
public class UserProfileServiceImpl implements UserProfileService, UserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		System.out.println("login " +username);
		UserDetails user = findUserProfilesByUsernameEquals(username);
		System.out.println(user);
		return user;
	}

	@Override 
	public UserProfile findUserProfilesByUsernameEquals(String username) {
		return UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
	}

	public long countAllUserProfiles() {
        return UserProfile.countUserProfiles();
    }

	public void deleteUserProfile(UserProfile userProfile) {
        userProfile.remove();
    }

	public UserProfile findUserProfile(Long id) {
        return UserProfile.findUserProfile(id);
    }

	public List<UserProfile> findAllUserProfiles() {
        return UserProfile.findAllUserProfiles();
    }

	public List<UserProfile> findUserProfileEntries(int firstResult, int maxResults) {
        return UserProfile.findUserProfileEntries(firstResult, maxResults);
    }

	public void saveUserProfile(UserProfile userProfile) {
        userProfile.persist();
    }

	public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfile.merge();
    }
}
