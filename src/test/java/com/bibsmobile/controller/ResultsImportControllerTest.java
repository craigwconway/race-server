package com.bibsmobile.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.service.RaceResultService;

public class ResultsImportControllerTest {

    ResultsImportController resultsImportController;

    @Test
    @Ignore
    public void testCreate() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testDoImport() {
        fail("Not yet implemented");
    }

    @Test
    public void testSaveRaceResult_mismatched_mapping() {
        // setup data args
        String[] nextLine = { "craig", "conway", "37", "M" };
        String[] map = { "firstName" };
        Event event = new Event();
        // mock object and method return
        ResultsImport resultsImport = mock(ResultsImport.class);
        when(resultsImport.getErrorRows()).thenReturn("");
        // create class under test
        this.resultsImportController = new ResultsImportController();
        this.resultsImportController.saveRaceResult(resultsImport, event, nextLine, map);
        // assertions
        verify(resultsImport).setErrors(anyInt()); // verify that
                                                   // "resultsImport.setErrors()
                                                   // was called once with any
                                                   // args
        verify(resultsImport).setErrorRows(nextLine[0]); // verify that a
                                                         // specific arg was
                                                         // passed
        verify(resultsImport).merge(); // verify this got called
        verify(resultsImport, never()).setRowsProcessed(anyInt()); // this never
                                                                   // got called
    }

    @Test
    public void testSaveRaceResult_existing_match() {
        String[] nextLine = { "craig", "conway", "37", "M" };
        String[] map = { "firstName", "lastName", "age", "gender" };
        ResultsImport resultsImport = mock(ResultsImport.class);
        Event event = mock(Event.class);
        RaceResult raceResult = new RaceResult();
        RaceResultService raceResultService = mock(RaceResultService.class);
        when(raceResultService.fromJsonToRaceResult(anyString())).thenReturn(raceResult);
        when(raceResultService.findRaceResultsByEventAndBibEquals(any(Event.class), Matchers.anyLong())).thenReturn(raceResult);
        this.resultsImportController = new ResultsImportController(raceResultService);
        this.resultsImportController.saveRaceResult(resultsImport, event, nextLine, map);
        // verify no db txn
        verify(raceResultService, never()).update(any(RaceResult.class), any(RaceResult.class));
        verify(raceResultService, never()).persist(raceResult);
        verify(resultsImport, times(1)).setRowsProcessed(anyInt());
        verify(resultsImport, times(1)).merge();
        assertTrue(1 == 1);
    }

    @Test
    @Ignore
    public void testSaveRaceResult_existing_no_match() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testSaveRaceResult_new() {
        fail("Not yet implemented");
    }

}
