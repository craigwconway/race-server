package com.bibsmobile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(finders = { "findResultsFileMappingsByResultsFile"})
public class ResultsFileMapping {

	private String name;
	@ManyToOne 
	private ResultsFile resultsFile;
	private boolean skipFirstRow;
	private String map;
	
	@Transient
	private List<String> row1 = new ArrayList<String>();
	@Transient
	private List<String> row2 = new ArrayList<String>();
	@Transient
	private TreeMap<String, String> options = new TreeMap<String,String>();
	
	@Override
	public String toString(){
		return map;
	}
	
}
