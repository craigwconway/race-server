package com.bibsmobile.model;

import javax.persistence.Embeddable;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;


@Embeddable
public class EventAwardsConfig {

    private boolean allowMedalsInAgeGenderRankings=false;
    private boolean allowMastersInNonMasters=true;
    
	public boolean isAllowMedalsInAgeGenderRankings() {
		return allowMedalsInAgeGenderRankings;
	}
	
	public void setAllowMedalsInAgeGenderRankings(
			boolean allowMedalsInAgeGenderRankings) {
		this.allowMedalsInAgeGenderRankings = allowMedalsInAgeGenderRankings;
	}

	public boolean isAllowMastersInNonMasters() {
		return allowMastersInNonMasters;
	}

	public void setAllowMastersInNonMasters(boolean allowMastersInNonMasters) {
		this.allowMastersInNonMasters = allowMastersInNonMasters;
	}
}
