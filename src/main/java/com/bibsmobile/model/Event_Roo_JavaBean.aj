// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAlert;
import com.bibsmobile.model.EventMap;
import com.bibsmobile.model.EventPhoto;
import com.bibsmobile.model.EventResult;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.UserGroup;
import java.util.Date;
import java.util.List;
import java.util.Set;

privileged aspect Event_Roo_JavaBean {
    
    public Set<RaceImage> Event.getRaceImages() {
        return this.raceImages;
    }
    
    public void Event.setRaceImages(Set<RaceImage> raceImages) {
        this.raceImages = raceImages;
    }
    
    public Set<RaceResult> Event.getRaceResults() {
        return this.raceResults;
    }
    
    public void Event.setRaceResults(Set<RaceResult> raceResults) {
        this.raceResults = raceResults;
    }
    
    public Set<ResultsFile> Event.getResultsFiles() {
        return this.resultsFiles;
    }
    
    public void Event.setResultsFiles(Set<ResultsFile> resultsFiles) {
        this.resultsFiles = resultsFiles;
    }
    
    public List<AwardCategory> Event.getAwardCategorys() {
        return this.awardCategorys;
    }
    
    public void Event.setAwardCategorys(List<AwardCategory> awardCategorys) {
        this.awardCategorys = awardCategorys;
    }
    
    public UserGroup Event.getUserGroup() {
        return this.userGroup;
    }
    
    public void Event.setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
    
    public String Event.getName() {
        return this.name;
    }
    
    public void Event.setName(String name) {
        this.name = name;
    }
    
    public Date Event.getTimeStart() {
        return this.timeStart;
    }
    
    public void Event.setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }
    
    public Date Event.getTimeEnd() {
        return this.timeEnd;
    }
    
    public void Event.setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    public int Event.getFeatured() {
        return this.featured;
    }
    
    public void Event.setFeatured(int featured) {
        this.featured = featured;
    }
    
    public String Event.getCity() {
        return this.city;
    }
    
    public void Event.setCity(String city) {
        this.city = city;
    }
    
    public String Event.getState() {
        return this.state;
    }
    
    public void Event.setState(String state) {
        this.state = state;
    }
    
    public String Event.getCountry() {
        return this.country;
    }
    
    public void Event.setCountry(String country) {
        this.country = country;
    }
    
    public String Event.getLattitude() {
        return this.lattitude;
    }
    
    public void Event.setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }
    
    public String Event.getLongitude() {
        return this.longitude;
    }
    
    public void Event.setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public String Event.getType() {
        return this.type;
    }
    
    public void Event.setType(String type) {
        this.type = type;
    }
    
    public String Event.getWebsite() {
        return this.website;
    }
    
    public void Event.setWebsite(String website) {
        this.website = website;
    }
    
    public String Event.getPhone() {
        return this.phone;
    }
    
    public void Event.setPhone(String phone) {
        this.phone = phone;
    }
    
    public String Event.getEmail() {
        return this.email;
    }
    
    public void Event.setEmail(String email) {
        this.email = email;
    }
    
    public String Event.getRegistration() {
        return this.registration;
    }
    
    public void Event.setRegistration(String registration) {
        this.registration = registration;
    }
    
    public String Event.getParking() {
        return this.parking;
    }
    
    public void Event.setParking(String parking) {
        this.parking = parking;
    }
    
    public String Event.getGeneral() {
        return this.general;
    }
    
    public void Event.setGeneral(String general) {
        this.general = general;
    }
    
    public String Event.getDescription() {
        return this.description;
    }
    
    public void Event.setDescription(String description) {
        this.description = description;
    }
    
    public String Event.getOrganization() {
        return this.organization;
    }
    
    public void Event.setOrganization(String organization) {
        this.organization = organization;
    }
    
    public String Event.getPhoto() {
        return this.photo;
    }
    
    public void Event.setPhoto(String photo) {
        this.photo = photo;
    }
    
    public String Event.getPhoto2() {
        return this.photo2;
    }
    
    public void Event.setPhoto2(String photo2) {
        this.photo2 = photo2;
    }
    
    public String Event.getPhoto3() {
        return this.photo3;
    }
    
    public void Event.setPhoto3(String photo3) {
        this.photo3 = photo3;
    }
    
    public String Event.getMap() {
        return this.map;
    }
    
    public void Event.setMap(String map) {
        this.map = map;
    }
    
    public String Event.getMap2() {
        return this.map2;
    }
    
    public void Event.setMap2(String map2) {
        this.map2 = map2;
    }
    
    public String Event.getMap3() {
        return this.map3;
    }
    
    public void Event.setMap3(String map3) {
        this.map3 = map3;
    }
    
    public String Event.getResults() {
        return this.results;
    }
    
    public void Event.setResults(String results) {
        this.results = results;
    }
    
    public String Event.getResults2() {
        return this.results2;
    }
    
    public void Event.setResults2(String results2) {
        this.results2 = results2;
    }
    
    public String Event.getResults3() {
        return this.results3;
    }
    
    public void Event.setResults3(String results3) {
        this.results3 = results3;
    }
    
    public String Event.getAlert1() {
        return this.alert1;
    }
    
    public void Event.setAlert1(String alert1) {
        this.alert1 = alert1;
    }
    
    public String Event.getAlert2() {
        return this.alert2;
    }
    
    public void Event.setAlert2(String alert2) {
        this.alert2 = alert2;
    }
    
    public String Event.getAlert3() {
        return this.alert3;
    }
    
    public void Event.setAlert3(String alert3) {
        this.alert3 = alert3;
    }
    
    public String Event.getDonateUrl() {
        return this.donateUrl;
    }
    
    public void Event.setDonateUrl(String donateUrl) {
        this.donateUrl = donateUrl;
    }
    
    public String Event.getFacebookUrl1() {
        return this.facebookUrl1;
    }
    
    public void Event.setFacebookUrl1(String facebookUrl1) {
        this.facebookUrl1 = facebookUrl1;
    }
    
    public String Event.getFacebookUrl2() {
        return this.facebookUrl2;
    }
    
    public void Event.setFacebookUrl2(String facebookUrl2) {
        this.facebookUrl2 = facebookUrl2;
    }
    
    public String Event.getPhotoUploadUrl() {
        return this.photoUploadUrl;
    }
    
    public void Event.setPhotoUploadUrl(String photoUploadUrl) {
        this.photoUploadUrl = photoUploadUrl;
    }
    
    public String Event.getCoursemaps() {
        return this.coursemaps;
    }
    
    public void Event.setCoursemaps(String coursemaps) {
        this.coursemaps = coursemaps;
    }
    
    public String Event.getMerchandise() {
        return this.merchandise;
    }
    
    public void Event.setMerchandise(String merchandise) {
        this.merchandise = merchandise;
    }
    
    public String Event.getBeachEvents() {
        return this.beachEvents;
    }
    
    public void Event.setBeachEvents(String beachEvents) {
        this.beachEvents = beachEvents;
    }
    
    public String Event.getShuttles() {
        return this.shuttles;
    }
    
    public void Event.setShuttles(String shuttles) {
        this.shuttles = shuttles;
    }
    
    public String Event.getCourseRules() {
        return this.courseRules;
    }
    
    public void Event.setCourseRules(String courseRules) {
        this.courseRules = courseRules;
    }
    
    public int Event.getRunning() {
        return this.running;
    }
    
    public void Event.setRunning(int running) {
        this.running = running;
    }
    
    public boolean Event.isGunFired() {
        return this.gunFired;
    }
    
    public void Event.setGunFired(boolean gunFired) {
        this.gunFired = gunFired;
    }
    
    public boolean Event.isSync() {
        return this.sync;
    }
    
    public void Event.setSync(boolean sync) {
        this.sync = sync;
    }
    
    public String Event.getSyncId() {
        return this.syncId;
    }
    
    public void Event.setSyncId(String syncId) {
        this.syncId = syncId;
    }
    
    public boolean Event.isRegEnabled() {
        return this.regEnabled;
    }
    
    public void Event.setRegEnabled(boolean regEnabled) {
        this.regEnabled = regEnabled;
    }
    
    public Date Event.getRegStart() {
        return this.regStart;
    }
    
    public void Event.setRegStart(Date regStart) {
        this.regStart = regStart;
    }
    
    public Date Event.getRegEnd() {
        return this.regEnd;
    }
    
    public void Event.setRegEnd(Date regEnd) {
        this.regEnd = regEnd;
    }
    
    public Date Event.getGunTime() {
        return this.gunTime;
    }
    
    public void Event.setGunTime(Date gunTime) {
        this.gunTime = gunTime;
    }
    
    public long Event.getGunTimeStart() {
        return this.gunTimeStart;
    }
    
    public void Event.setGunTimeStart(long gunTimeStart) {
        this.gunTimeStart = gunTimeStart;
    }
    
    public Date Event.getCreated() {
        return this.created;
    }
    
    public void Event.setCreated(Date created) {
        this.created = created;
    }
    
    public Date Event.getUpdated() {
        return this.updated;
    }
    
    public void Event.setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public List<EventPhoto> Event.getPhotos() {
        return this.photos;
    }
    
    public void Event.setPhotos(List<EventPhoto> photos) {
        this.photos = photos;
    }
    
    public List<EventAlert> Event.getAlerts() {
        return this.alerts;
    }
    
    public void Event.setAlerts(List<EventAlert> alerts) {
        this.alerts = alerts;
    }
    
    public List<EventMap> Event.getMaps() {
        return this.maps;
    }
    
    public void Event.setMaps(List<EventMap> maps) {
        this.maps = maps;
    }
    
    public List<EventResult> Event.getEventResults() {
        return this.eventResults;
    }
    
    public void Event.setEventResults(List<EventResult> eventResults) {
        this.eventResults = eventResults;
    }
    
}
