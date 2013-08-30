package com.bibsmobile.controller;

import au.com.bytecode.opencsv.CSVReader;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.validation.Valid;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/resultsfilemappings")
@Controller
@RooWebScaffold(path = "resultsfilemappings", formBackingObject = ResultsFileMapping.class)
public class ResultsFileMappingController {

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
            System.out.println("csv");
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
            System.out.println("xls");
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
        System.out.println("setOptions");
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
        System.out.println("getPropertiesFromClasspath");
        Properties props = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        props.load(inputStream);
        return props;
    }
}
