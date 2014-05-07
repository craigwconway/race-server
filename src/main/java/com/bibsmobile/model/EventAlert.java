package com.bibsmobile.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.json.RooJson;

import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class EventAlert {

    /**
     */
    private String text;

    @ManyToOne
    private Event event;
}
