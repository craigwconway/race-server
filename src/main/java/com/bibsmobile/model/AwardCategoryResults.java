package com.bibsmobile.model;

import java.util.List;

public class AwardCategoryResults implements Comparable{

	private final AwardCategory category;
	private final List<RaceResult> results;
	
	public AwardCategoryResults(AwardCategory category, List<RaceResult> results){
		this.category = category;
		this.results = results;
	}

	public AwardCategory getCategory() {
		return category;
	}

	public List<RaceResult> getResults() {
		return results;
	}
	
	@Override
	public int compareTo(Object o) {
		if(o instanceof AwardCategoryResults){
			AwardCategoryResults a = (AwardCategoryResults)o;
			if(null != a.getCategory() && null != getCategory()){
				return getCategory().getSortOrder() - a.getCategory().getSortOrder();
			}
		}
		return 0;
	}
	
}
