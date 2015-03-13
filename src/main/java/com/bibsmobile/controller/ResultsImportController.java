package com.bibsmobile.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.service.RaceResultService;
import com.bibsmobile.util.BuildTypeUtil;
import com.bibsmobile.util.XlsToCsv;

@RequestMapping("/resultsimports")
@Controller
public class ResultsImportController {

    private RaceResultService raceResultService;

    public ResultsImportController() {
        super();
    }

    protected ResultsImportController(RaceResultService raceResultService) {
        super();
        this.raceResultService = raceResultService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ResultsImport resultsImport, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) throws InvalidFormatException,
            IOException {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, resultsImport);
            uiModel.addAttribute("build", BuildTypeUtil.getBuild());
            return "resultsimports/create";
        }
        uiModel.asMap().clear();
        this.doImport(resultsImport);
        resultsImport.persist();
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/resultsimports/" + this.encodeUrlPathSegment(resultsImport.getId().toString(), httpServletRequest);
    }

    public void doImport(final ResultsImport resultsImport) throws IOException, InvalidFormatException {
    	
    	new Thread(){
    	@Override
    	public void run(){
    	try{	
    	final ResultsImportController _this = new ResultsImportController();
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
                _this.saveRaceResult(resultsImport, event, nextLine, map);
            }
            reader.close();
        } else if (StringUtils.endsWithIgnoreCase(resultsFile.getFilePath(), ".xls") || StringUtils.endsWithIgnoreCase(resultsFile.getFilePath(), ".xlsx")) {
            XlsToCsv csv = new XlsToCsv();
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            for (int i = (resultsFileMapping.isSkipFirstRow() ? 1 : 0); i <= sheet.getLastRowNum(); i++) {
                final String[] nextLine = csv.rowToCSV(sheet.getRow(i)).split(",");
                _this.saveRaceResult(resultsImport, event, nextLine, map);
            }
        }
        System.out.println("Done");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	}}; // thread run
    }

    public void saveRaceResult(ResultsImport resultsImport, Event event, String[] nextLine, String[] map) {
        if (nextLine.length != map.length || nextLine.length == 0) {
            resultsImport.setErrors(resultsImport.getErrors() + 1);
            resultsImport.setErrorRows(resultsImport.getErrorRows() + nextLine[0]);
            resultsImport.merge();
            return;
        }
        StringBuilder json = new StringBuilder("{");
        for (int j = 0; j < nextLine.length; j++) {
            if (map[j].equals("-"))
                continue;
            if (!json.toString().equals("{"))
                json.append(",");
            json.append(map[j] + ":\"" + nextLine[j] + "\"");
        }
        json.append("}");
        RaceResult result = this.raceResultService.fromJsonToRaceResult(json.toString());
        result.setEvent(event);
        boolean same = false;
        RaceResult exists = null;
        try {
            exists = this.raceResultService.findRaceResultsByEventAndBibEquals(event, result.getBib());
            if (exists.equals(result)) {
                same = true;
            }
        } catch (Exception e) {
        }
        if (null != exists && !same) {
            this.raceResultService.update(exists, result);
        } else if (null == exists) {
            this.raceResultService.persist(result);
        }
        resultsImport.setRowsProcessed(resultsImport.getRowsProcessed() + 1);
        resultsImport.merge();
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new ResultsImport());
        return "resultsimports/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("resultsimport", ResultsImport.findResultsImport(id));
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "resultsimports/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("resultsimports", ResultsImport.findResultsImportEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) ResultsImport.countResultsImports() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("resultsimports", ResultsImport.findAllResultsImports(sortFieldName, sortOrder));
        }
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "resultsimports/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid ResultsImport resultsImport, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, resultsImport);
            return "resultsimports/update";
        }
        uiModel.asMap().clear();
        resultsImport.merge();
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/resultsimports/" + this.encodeUrlPathSegment(resultsImport.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, ResultsImport.findResultsImport(id));
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "resultsimports/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        ResultsImport resultsImport = ResultsImport.findResultsImport(id);
        resultsImport.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        uiModel.addAttribute("build", BuildTypeUtil.getBuild());
        return "redirect:/resultsimports";
    }

    void populateEditForm(Model uiModel, ResultsImport resultsImport) {
        uiModel.addAttribute("resultsImport", resultsImport);
        uiModel.addAttribute("resultsfiles", ResultsFile.findAllResultsFiles());
        uiModel.addAttribute("resultsfilemappings", ResultsFileMapping.findAllResultsFileMappings());
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }
}
