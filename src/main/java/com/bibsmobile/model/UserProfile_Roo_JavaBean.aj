// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.CartItem;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserProfile;
import java.util.Date;
import java.util.Set;

privileged aspect UserProfile_Roo_JavaBean {
    
    public String UserProfile.getFirstname() {
        return this.firstname;
    }
    
    public void UserProfile.setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String UserProfile.getLastname() {
        return this.lastname;
    }
    
    public void UserProfile.setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String UserProfile.getCity() {
        return this.city;
    }
    
    public void UserProfile.setCity(String city) {
        this.city = city;
    }
    
    public String UserProfile.getState() {
        return this.state;
    }
    
    public void UserProfile.setState(String state) {
        this.state = state;
    }
    
    public int UserProfile.getAge() {
        return this.age;
    }
    
    public void UserProfile.setAge(int age) {
        this.age = age;
    }
    
    public Date UserProfile.getBirthdate() {
        return this.birthdate;
    }
    
    public void UserProfile.setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
    
    public String UserProfile.getGender() {
        return this.gender;
    }
    
    public void UserProfile.setGender(String gender) {
        this.gender = gender;
    }
    
    public String UserProfile.getEmail() {
        return this.email;
    }
    
    public void UserProfile.setEmail(String email) {
        this.email = email;
    }
    
    public String UserProfile.getImage() {
        return this.image;
    }
    
    public void UserProfile.setImage(String image) {
        this.image = image;
    }
    
    public Set<UserAuthorities> UserProfile.getUserAuthorities() {
        return this.userAuthorities;
    }
    
    public void UserProfile.setUserAuthorities(Set<UserAuthorities> userAuthorities) {
        this.userAuthorities = userAuthorities;
    }
    
    public void UserProfile.setUsername(String username) {
        this.username = username;
    }
    
    public void UserProfile.setPassword(String password) {
        this.password = password;
    }
    
    public void UserProfile.setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }
    
    public void UserProfile.setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    
    public void UserProfile.setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
    
    public void UserProfile.setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String UserProfile.getFacebookId() {
        return this.facebookId;
    }
    
    public void UserProfile.setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
    
    public String UserProfile.getTwitterId() {
        return this.twitterId;
    }
    
    public void UserProfile.setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }
    
    public String UserProfile.getGoogleId() {
        return this.googleId;
    }
    
    public void UserProfile.setGoogleId(String googleId) {
        this.googleId = googleId;
    }
    
    public Set<RaceResult> UserProfile.getRaceResults() {
        return this.raceResults;
    }
    
    public void UserProfile.setRaceResults(Set<RaceResult> raceResults) {
        this.raceResults = raceResults;
    }
    
    public String UserProfile.getPhone() {
        return this.phone;
    }
    
    public void UserProfile.setPhone(String phone) {
        this.phone = phone;
    }
    
    public String UserProfile.getAddressLine1() {
        return this.addressLine1;
    }
    
    public void UserProfile.setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String UserProfile.getAddressLine2() {
        return this.addressLine2;
    }
    
    public void UserProfile.setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String UserProfile.getZipCode() {
        return this.zipCode;
    }
    
    public void UserProfile.setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String UserProfile.getEmergencyContactName() {
        return this.emergencyContactName;
    }
    
    public void UserProfile.setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String UserProfile.getEmergencyContactPhone() {
        return this.emergencyContactPhone;
    }
    
    public void UserProfile.setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String UserProfile.getHearFrom() {
        return this.hearFrom;
    }
    
    public void UserProfile.setHearFrom(String hearFrom) {
        this.hearFrom = hearFrom;
    }
    
    public String UserProfile.getDropboxId() {
        return this.dropboxId;
    }
    
    public void UserProfile.setDropboxId(String dropboxId) {
        this.dropboxId = dropboxId;
    }
    
    public String UserProfile.getDropboxAccessToken() {
        return this.dropboxAccessToken;
    }
    
    public void UserProfile.setDropboxAccessToken(String dropboxAccessToken) {
        this.dropboxAccessToken = dropboxAccessToken;
    }
    
    public Set<ResultsFile> UserProfile.getResultsFiles() {
        return this.resultsFiles;
    }
    
    public void UserProfile.setResultsFiles(Set<ResultsFile> resultsFiles) {
        this.resultsFiles = resultsFiles;
    }
    
    public CartItem UserProfile.getCartItem() {
        return this.cartItem;
    }
    
    public void UserProfile.setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
    
    public String UserProfile.getForgotPasswordCode() {
        return this.forgotPasswordCode;
    }
    
    public void UserProfile.setForgotPasswordCode(String forgotPasswordCode) {
        this.forgotPasswordCode = forgotPasswordCode;
    }
    
}
