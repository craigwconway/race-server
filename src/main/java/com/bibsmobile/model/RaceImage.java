package com.bibsmobile.model;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findRaceImagesByEvent", "findRaceImagesByRaceResults" })
public class RaceImage {

    @NotNull
    String filePath;

    @ManyToOne
    RaceResult raceResult;

    @ManyToOne
    Event event;

    @ManyToOne
    UserProfile userProfile;

    boolean nonPublic;

    @ManyToMany
    Set<PictureType> pictureTypes = new HashSet<>();

    public RaceImage(){}

    public RaceImage(String filePath, long eventId){
        this.filePath = filePath;
        this.event = Event.findEvent(eventId);
    }

    public RaceImage(String filePath, RaceResult raceResult, Event event) {
        this.filePath = filePath;
        this.raceResult = raceResult;
        this.event = event;
    }

    public RaceImage(String filePath, long eventId, String bib){
        this.filePath = filePath;
        this.event = Event.findEvent(eventId);
        this.raceResult = RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getSingleResult();
    }

    public RaceImage(String filePath, long eventId, List<String> bibs) {
        this(filePath, eventId);
        if (CollectionUtils.isNotEmpty(bibs)) {
            List<RaceResult> raceResults = RaceResult.findRaceResultsByEventAndMultipleBibs(event, bibs);
            for (RaceResult raceResult : raceResults) {
                new RaceImage(filePath, raceResult, event).persist();
            }
        }
    }

    public RaceImage(String filePath, long eventId, List<String> bibs, List<String> types) {
        this(filePath, eventId, bibs);
        if (CollectionUtils.isNotEmpty(types)) {
            for (String type : types) {
                PictureType pictureType;
                if (PictureType.countFindPictureTypesByPictureTypeEquals(type) > 0) {
                    pictureType = PictureType.findPictureTypesByPictureTypeEquals(type).getSingleResult();
                } else {
                    pictureType = new PictureType();
                    pictureType.setPictureType(type);
                    pictureType.persist();
                }
                pictureTypes.add(pictureType);
            }
        }
        this.persist();
    }


    public static TypedQuery<RaceImage> findRaceImagesByRaceResults(List<RaceResult> raceResults) {
        if (raceResults == null) throw new IllegalArgumentException("The raceResults argument is required");
        EntityManager em = RaceImage.entityManager();
        TypedQuery<RaceImage> q = em.createQuery("SELECT o FROM RaceImage AS o WHERE o.raceResult IN (:raceResults)", RaceImage.class);
        q.setParameter("raceResults", raceResults);
        return q;
    }

}