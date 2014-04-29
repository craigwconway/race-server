// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.model;

import com.bibsmobile.model.Event;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect Event_Roo_Equals {
    
    public boolean Event.equals(Object obj) {
        if (!(obj instanceof Event)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Event rhs = (Event) obj;
        return new EqualsBuilder().append(alert1, rhs.alert1).append(alert2, rhs.alert2).append(alert3, rhs.alert3).append(beachEvents, rhs.beachEvents).append(city, rhs.city).append(country, rhs.country).append(courseRules, rhs.courseRules).append(coursemaps, rhs.coursemaps).append(created, rhs.created).append(description, rhs.description).append(donateUrl, rhs.donateUrl).append(email, rhs.email).append(facebookUrl1, rhs.facebookUrl1).append(facebookUrl2, rhs.facebookUrl2).append(featured, rhs.featured).append(general, rhs.general).append(gunFired, rhs.gunFired).append(gunTime, rhs.gunTime).append(gunTimeStart, rhs.gunTimeStart).append(id, rhs.id).append(lattitude, rhs.lattitude).append(longitude, rhs.longitude).append(map, rhs.map).append(map2, rhs.map2).append(map3, rhs.map3).append(merchandise, rhs.merchandise).append(name, rhs.name).append(organization, rhs.organization).append(parking, rhs.parking).append(phone, rhs.phone).append(photo, rhs.photo).append(photo2, rhs.photo2).append(photo3, rhs.photo3).append(photoUploadUrl, rhs.photoUploadUrl).append(regEnabled, rhs.regEnabled).append(regEnd, rhs.regEnd).append(regStart, rhs.regStart).append(registration, rhs.registration).append(results, rhs.results).append(results2, rhs.results2).append(results3, rhs.results3).append(running, rhs.running).append(shuttles, rhs.shuttles).append(state, rhs.state).append(sync, rhs.sync).append(syncId, rhs.syncId).append(timeEnd, rhs.timeEnd).append(timeStart, rhs.timeStart).append(type, rhs.type).append(updated, rhs.updated).append(userGroup, rhs.userGroup).append(website, rhs.website).isEquals();
    }
    
    public int Event.hashCode() {
        return new HashCodeBuilder().append(alert1).append(alert2).append(alert3).append(beachEvents).append(city).append(country).append(courseRules).append(coursemaps).append(created).append(description).append(donateUrl).append(email).append(facebookUrl1).append(facebookUrl2).append(featured).append(general).append(gunFired).append(gunTime).append(gunTimeStart).append(id).append(lattitude).append(longitude).append(map).append(map2).append(map3).append(merchandise).append(name).append(organization).append(parking).append(phone).append(photo).append(photo2).append(photo3).append(photoUploadUrl).append(regEnabled).append(regEnd).append(regStart).append(registration).append(results).append(results2).append(results3).append(running).append(shuttles).append(state).append(sync).append(syncId).append(timeEnd).append(timeStart).append(type).append(updated).append(userGroup).append(website).toHashCode();
    }
    
}