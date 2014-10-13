package com.bibsmobile.controller;

import au.com.bytecode.opencsv.CSVReader; 

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.service.RaceResultService;
import com.bibsmobile.util.XlsToCsv;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

@RequestMapping("/resultsimports")
@Controller
@RooWebScaffold(path = "resultsimports", formBackingObject = ResultsImport.class)
public class ResultsImportController {
	
	private RaceResultService raceResultService;
	
	public ResultsImportController(){}
	
	protected ResultsImportController(RaceResultService raceResultService){
		this.raceResultService = raceResultService;
	}

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ResultsImport resultsImport, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws InvalidFormatException, IOException {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, resultsImport);
            return "resultsimports/create";
        }
        uiModel.asMap().clear();
        doImport(resultsImport);
        resultsImport.persist();
        return "redirect:/resultsimports/" + encodeUrlPathSegment(resultsImport.getId().toString(), httpServletRequest);
    }

    public void doImport(ResultsImport resultsImport) throws IOException, InvalidFormatException {  
        System.out.println("Starting import...");
        resultsImport.setRunDate(new Date());
        ResultsFileMapping resultsFileMapping = resultsImport.getResultsFileMapping();
        Event event = resultsImport.getResultsFile().getEvent();
        ResultsFile resultsFile = resultsImport.getResultsFile();
        resultsImport.setRunDate(new Date());
        File file = new File(resultsFile.getFilePath());
        String[] map = resultsFileMapping.getMap().split(",");
        if (StringUtils.endsWithIgnoreCase(resultsFile.getFilePath(), ".csv") || StringUtils.endsWithIgnoreCase(resultsFile.getFilePath(), ".txt")) {
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextLine; 
            while ((nextLine = reader.readNext()) != null) {
                saveRaceResult(resultsImport, event, nextLine, map);
            }
            reader.close();
        } else if (StringUtils.endsWithIgnoreCase(resultsFile.getFilePath(), ".xls") || StringUtils.endsWithIgnoreCase(resultsFile.getFilePath(), ".xlsx")) {
            XlsToCsv csv = new XlsToCsv();
            Workbook wb = WorkbookFactory.create(file); 
            Sheet sheet = wb.getSheetAt(0);
            for (int i = (resultsFileMapping.isSkipFirstRow() ? 1 : 0); i <= sheet.getLastRowNum(); i++) {
                final String nextLine[] = csv.rowToCSV(sheet.getRow(i)).split(",");
                saveRaceResult(resultsImport, event, nextLine, map);
            }
        }
        System.out.println("Done");
    }

    public void saveRaceResult(ResultsImport resultsImport, Event event, String[] nextLine, String[] map) {
        if (nextLine.length != map.length || nextLine.length == 0) {
            resultsImport.setErrors(resultsImport.getErrors() + 1);
            resultsImport.setErrorRows(resultsImport.getErrorRows().concat(nextLine[0]));
            resultsImport.merge();
            return;
        }
        StringBuffer json = new StringBuffer("{");
        for (int j = 0; j < nextLine.length; j++) {
            if (map[j].equals("-")) continue;
            if (!json.toString().equals("{")) json.append(",");
            json.append(map[j] + ":\"" + nextLine[j] + "\"");
        }
        json.append("}");
        RaceResult result = raceResultService.fromJsonToRaceResult(json.toString());
        result.setEvent(event);
        boolean same = false;
        RaceResult exists = null;
        try {
            exists = raceResultService.findRaceResultsByEventAndBibEquals(event, result.getBib());
            if(exists.equals(result)){
            	same = true;
            }
        } catch (Exception e) {
        }
        if (null != exists && !same) {
        	raceResultService.update(exists,result);
        } else if(null == exists){
        	raceResultService.persist(result);
        }
        resultsImport.setRowsProcessed(resultsImport.getRowsProcessed() + 1);
        resultsImport.merge();
    }

}
