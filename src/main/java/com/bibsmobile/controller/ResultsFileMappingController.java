package com.bibsmobile.controller;

import au.com.bytecode.opencsv.CSVReader;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.util.XlsToCsv;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/resultsfilemappings")
@Controller
@RooWebScaffold(path = "resultsfilemappings", formBackingObject = ResultsFileMapping.class)
public class ResultsFileMappingController {

    @RequestMapping(value = "/updateForm.json")
    public ResponseEntity<String> updateFormJson(Long id, Model uiModel) {
        updateForm(id, uiModel);
        ResultsFileMapping resultsFileMapping = (ResultsFileMapping) uiModel.asMap().get("resultsFileMapping");
        return new ResponseEntity<>(resultsFileMapping.toJson(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        ResultsFileMapping resultsFileMapping = ResultsFileMapping.findResultsFileMapping(id);
        try {
            initMap(resultsFileMapping);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        populateEditForm(uiModel, resultsFileMapping);
        return "resultsfilemappings/update";
    }

    public void initMap(ResultsFileMapping resultsFileMapping) throws IOException, InvalidFormatException {
        setOptions(resultsFileMapping);
        setRows(resultsFileMapping);
    }

    public void setRows(ResultsFileMapping resultsFileMapping) throws IOException, InvalidFormatException {
        ResultsFile resultsFile = resultsFileMapping.getResultsFile();
        File file = new File(resultsFile.getFilePath());
        if (resultsFile.getFilePath().endsWith(".csv") || resultsFile.getFilePath().endsWith(".txt")
        		|| resultsFile.getFilePath().endsWith(".CSV") || resultsFile.getFilePath().endsWith(".TXT")) {
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            int line = 0;
            while ((nextLine = reader.readNext()) != null && line < 2) {
                List<String> row = new ArrayList<String>();
                for (String cell : nextLine) {
                    row.add(cell);
                }
                if (line == 0) resultsFileMapping.setRow1(row);
                if (line == 1) resultsFileMapping.setRow2(row);
                line++;
            }
            reader.close();
        } else if (resultsFile.getFilePath().endsWith(".xlsx") || resultsFile.getFilePath().endsWith(".xls")
        		|| resultsFile.getFilePath().endsWith(".XLSX") || resultsFile.getFilePath().endsWith(".XLS")) {
            XlsToCsv csv = new XlsToCsv();
            Workbook wb = WorkbookFactory.create(file);
            Sheet sheet = wb.getSheetAt(0);
            String rowCsv = csv.rowToCSV(sheet.getRow(0));
            List<String> row = new ArrayList<String>();
            for (String val : rowCsv.split(",")) {
                row.add(val);
            }
            resultsFileMapping.setRow1(row);
            rowCsv = csv.rowToCSV(sheet.getRow(1));
            row = new ArrayList<String>();
            for (String val : rowCsv.split(",")) {
                row.add(val);
            }
            resultsFileMapping.setRow2(row);
        }
    }

    public void setOptions(ResultsFileMapping resultsFileMapping) throws IOException {
        Properties p = getPropertiesFromClasspath("application.properties");
        final String prefix = "label_com_bibsmobile_model_raceresult_";
        for (Object key : p.keySet()) {
            if (!key.toString().startsWith(prefix)) continue;
            String _key = key.toString().replaceAll(prefix, "");
            String _val = p.get(key).toString();
            resultsFileMapping.getOptions().put(_key, _val);
        }
    }

    public Properties getPropertiesFromClasspath(String propFileName) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        props.load(inputStream);
        return props;
    }

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ResultsFileMapping resultsFileMapping, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, resultsFileMapping);
            return "resultsfilemappings/create";
        }
        uiModel.asMap().clear();
        resultsFileMapping.persist();
        return "redirect:/resultsfilemappings/" + encodeUrlPathSegment(resultsFileMapping.getId().toString(), httpServletRequest)+"?form";
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new ResultsFileMapping());
        return "resultsfilemappings/create";
    }

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("resultsfilemapping", ResultsFileMapping.findResultsFileMapping(id));
        uiModel.addAttribute("itemId", id);
        return "resultsfilemappings/show";
    }

	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("resultsfilemappings", ResultsFileMapping.findResultsFileMappingEntries(firstResult, sizeNo));
            float nrOfPages = (float) ResultsFileMapping.countResultsFileMappings() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("resultsfilemappings", ResultsFileMapping.findAllResultsFileMappings());
        }
        return "resultsfilemappings/list";
    }

    @RequestMapping(value = "/update.json", method = RequestMethod.PUT)
    public ResponseEntity<String> updateJson(@Valid ResultsFileMapping resultsFileMapping, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        String result = update(resultsFileMapping, bindingResult, uiModel, httpServletRequest);
        if (result.startsWith("redirect")) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("FAIL", HttpStatus.OK);
        }
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid ResultsFileMapping resultsFileMapping, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, resultsFileMapping);
            return "resultsfilemappings/update";
        }
        uiModel.asMap().clear();
        resultsFileMapping.merge();
        //  cwc      return "redirect:/resultsfilemappings/" + encodeUrlPathSegment(resultsFileMapping.getId().toString(), httpServletRequest);
        // do import
        ResultsFileMapping mapping = ResultsFileMapping.findResultsFileMapping(resultsFileMapping.getId());
        ResultsImport resultsImport = new ResultsImport();
        resultsImport.setResultsFile(mapping.getResultsFile());
        resultsImport.setResultsFileMapping(mapping);
        resultsImport.persist();
        try {
			doImport(resultsImport);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "redirect:/raceresults/";
        
        //return "redirect:/resultsimports/" + encodeUrlPathSegment(resultsImport.getId().toString(), httpServletRequest);
    }
	
	
	// ##############################################


    public void doImport(ResultsImport resultsImport) throws IOException, InvalidFormatException {
        System.out.println("Starting import (mapping)...");
        resultsImport.setRunDate(new Date());
        ResultsFileMapping resultsFileMapping = resultsImport.getResultsFileMapping();
        Event event = resultsImport.getResultsFile().getEvent();
        ResultsFile resultsFile = resultsImport.getResultsFile();
        resultsImport.setRunDate(new Date());
        File file = new File(resultsFile.getFilePath());
        String[] map = resultsFileMapping.getMap().split(",");
        if (resultsFile.getFilePath().endsWith(".csv") || resultsFile.getFilePath().endsWith(".txt")
        		|| resultsFile.getFilePath().endsWith(".CSV") || resultsFile.getFilePath().endsWith(".TXT")) {
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            if(resultsFileMapping.isSkipFirstRow() ) reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                saveRaceResult(resultsImport, event, nextLine, map);
            }
            reader.close();
        } else if (resultsFile.getFilePath().endsWith(".xlsx") || resultsFile.getFilePath().endsWith(".xls")
        		|| resultsFile.getFilePath().endsWith(".XLSX") || resultsFile.getFilePath().endsWith(".XLS")) {
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
            return;
        }
        StringBuffer json = new StringBuffer("{");
        for (int j = 0; j < nextLine.length; j++) {
            if (map[j].equals("-")) continue;
            if (!json.toString().equals("{")) json.append(",");
            json.append(map[j] + ":\"" + nextLine[j] + "\"");
        }
        json.append("}");
        RaceResult result = RaceResult.fromJsonToRaceResult(json.toString());
        result.setEvent(event);
        RaceResult exists = null;
        try {
            exists = RaceResult.findRaceResultsByEventAndBibEquals(event, result.getBib()).getSingleResult();
        } catch (Exception e) {
        }
        if (null != exists) {
            exists.merge(result);
            exists.merge();
        } else {
            result.persist();
        }
        resultsImport.setRowsProcessed(resultsImport.getRowsProcessed() + 1);
    }
    // #################################

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        ResultsFileMapping resultsFileMapping = ResultsFileMapping.findResultsFileMapping(id);
        resultsFileMapping.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/resultsfilemappings";
    }

	void populateEditForm(Model uiModel, ResultsFileMapping resultsFileMapping) {
        uiModel.addAttribute("resultsFileMapping", resultsFileMapping);
        uiModel.addAttribute("resultsfiles", ResultsFile.findAllResultsFiles());
    }

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
}
