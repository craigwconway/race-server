package com.bibsmobile.model;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(finders = { "findResultsFilesByEvent","findResultsFilesByNameEqualsAndEvent"} )
public class ResultsFile {

    private String name; 

    @NotNull
    private String contentType;
    @ManyToOne @NotNull
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
