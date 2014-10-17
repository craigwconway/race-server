package com.bibsmobile.service;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;

public class RaceResultService {

    public RaceResult fromJsonToRaceResult(String string) {
        return RaceResult.fromJsonToRaceResult(string);
    }

    public RaceResult findRaceResultsByEventAndBibEquals(Event event, String bib) {
        return RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getSingleResult();
    }

    public void update(RaceResult exists, RaceResult result) {
        exists.merge(result);
    }

    public void persist(RaceResult result) {
        result.persist();
    }

}
