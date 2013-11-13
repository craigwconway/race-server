package com.bibsmobile.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(finders = { "findResultsFilesByEvent","findResultsFilesByNameEqualsAndEvent"} )
public class ResultsFile {

    private String name; 	

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "resultsFile")
	private Set<ResultsFileMapping> resultsFileMapping;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "resultsFile")
	private Set<ResultsImport> resultsImport;

    @NotNull
    private String contentType;
    @ManyToOne
    private Event event;
    
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "SS")
    private Date created;

    @NotNull
    private long filesize;

    @NotNull
    @Size(max = 128)
    private String filePath; 
    
    @Transient
    private byte[] content;
    
    @Override
    public String toString(){
    	return name;
    }
    
}
