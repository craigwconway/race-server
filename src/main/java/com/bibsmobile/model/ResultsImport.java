package com.bibsmobile.model;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findResultsImportsByResultsFile" }) 
public class ResultsImport {
	
	@ManyToOne @NotNull
	@Cascade({CascadeType.ALL})
	private ResultsFile resultsFile;
	@ManyToOne @NotNull
	@Cascade({CascadeType.ALL})
	private ResultsFileMapping resultsFileMapping;
	
	private Date runDate = new Date(); 
	private int rowsProcessed;
	private int errors;
	private String errorRows = new String();
}
