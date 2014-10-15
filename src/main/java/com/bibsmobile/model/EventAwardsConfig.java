package com.bibsmobile.model;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class EventAwardsConfig {

    private boolean allowMedalsInAgeGenderRankings;
    private boolean allowMastersInNonMasters;
    
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
	
	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer().serialize(this);
    }

	public static EventAwardsConfig fromJson(String json) {
        return new JSONDeserializer<EventAwardsConfig>().use(null, EventAwardsConfig.class).deserialize(json);
    }

}
