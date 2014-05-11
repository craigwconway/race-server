package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class AwardCategory {

    @ManyToOne
    private Event event;
    
    private int sortOrder;
	private String name;
	private String gender;
	private int ageMin;
	private int ageMax;
	private int listSize;

	public static List<AwardCategory> eventDefaults() {
		List<AwardCategory> awardCategories = new ArrayList<AwardCategory>();
		AwardCategory awardCategory = new AwardCategory();
		awardCategory.setName("Overall Winners");
		awardCategory.setListSize(5);
		awardCategories.add(awardCategory);
		return awardCategories;
	}

}
