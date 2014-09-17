package com.bibsmobile.controller;  

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Ignore;
import org.junit.Test;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.service.RaceResultService;

public class ResultsImportControllerUT {
	
	ResultsImportController resultsImportController; 

	@Test @Ignore
	public void testCreate() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testDoImport() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveRaceResult_mismatched_mapping() {
		// setup data args
		String[] nextLine = {"craig","conway","37","M"};
		String[] map = {"firstName"};
		Event event = new Event();
		// mock object and method return
		ResultsImport resultsImport = mock(ResultsImport.class);
		when(resultsImport.getErrorRows()).thenReturn("");
		// create class under test
		resultsImportController = new ResultsImportController();
		resultsImportController.saveRaceResult(resultsImport, event, nextLine, map);
		// assertions
		verify(resultsImport).setErrors(anyInt()); // verify that "resultsImport.setErrors() was called once with any args
		verify(resultsImport).setErrorRows(nextLine[0]); // verify that a specific arg was passed
		verify(resultsImport).merge(); // verify this got called
		verify(resultsImport,never()).setRowsProcessed(anyInt()); // this never got called
	}

	@Test
	public void testSaveRaceResult_existing_match() {
		String[] nextLine = {"craig","conway","37","M"};
		String[] map = {"firstName","lastName","age","gender"};
		ResultsImport resultsImport = mock(ResultsImport.class);
		Event event = mock(Event.class);
		RaceResult raceResult = new RaceResult();
		RaceResultService raceResultService = mock(RaceResultService.class);
		when(raceResultService.fromJsonToRaceResult(anyString())).thenReturn(raceResult);
		when(raceResultService.findRaceResultsByEventAndBibEquals(any(Event.class), anyString())).thenReturn(raceResult);
		resultsImportController = new ResultsImportController(raceResultService);
		resultsImportController.saveRaceResult(resultsImport, event, nextLine, map);
		// verify no db txn
		verify(raceResultService,never()).update(any(RaceResult.class), any(RaceResult.class));
		verify(raceResultService,never()).persist(raceResult);
		verify(resultsImport,times(1)).setRowsProcessed(anyInt());
		verify(resultsImport,times(1)).merge();
		assertTrue(1==1);
	}

	@Test @Ignore
	public void testSaveRaceResult_existing_no_match() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testSaveRaceResult_new() {
		fail("Not yet implemented");
	}

}
