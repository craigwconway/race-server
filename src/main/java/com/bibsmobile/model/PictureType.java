package com.bibsmobile.model;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.List;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findPictureTypesByPictureTypeEquals", "findPictureTypesByPictureTypes"})
public class PictureType {

    @NotNull
    private String pictureType;

    @ManyToMany
    private List<RaceImage> raceImages;

}
