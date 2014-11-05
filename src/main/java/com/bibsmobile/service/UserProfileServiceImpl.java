package com.bibsmobile.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibsmobile.model.UserProfile;

@Service
@Transactional
// @Service
// @Transactional
public class UserProfileServiceImpl implements UserProfileService, UserDetailsService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private SimpleMailMessage registrationMessage;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("login " + username);
        UserDetails user = this.findUserProfilesByUsernameEquals(username);
        System.out.println(user);
        return user;
    }

    @Override
    public UserProfile findUserProfilesByUsernameEquals(String username) {
        return UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
    }

    @Override
    public long countAllUserProfiles() {
        return UserProfile.countUserProfiles();
    }

    @Override
    public void deleteUserProfile(UserProfile userProfile) {
        userProfile.remove();
    }

    @Override
    public UserProfile findUserProfile(Long id) {
        return UserProfile.findUserProfile(id);
    }

    @Override
    public List<UserProfile> findAllUserProfiles() {
        return UserProfile.findAllUserProfiles();
    }

    @Override
    public List<UserProfile> findUserProfileEntries(int firstResult, int maxResults) {
        return UserProfile.findUserProfileEntries(firstResult, maxResults);
    }

    @Override
    public void saveUserProfile(UserProfile userProfile) {
        userProfile.persist();
        if (StringUtils.isNotEmpty(userProfile.getEmail())) {
            try {
                this.registrationMessage.setTo(userProfile.getEmail());
                this.mailSender.send(this.registrationMessage);
            } catch (Exception e) {
                System.out.println("EXCEPTION: Email Send Fail - " + e.getMessage());
            }
        }
    }

    @Override
    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfile.merge();
    }
}
