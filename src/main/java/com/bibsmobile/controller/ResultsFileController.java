package com.bibsmobile.controller;

import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;

import java.io.File;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

@RequestMapping("/resultsfiles")
@Controller
@RooWebScaffold(path = "resultsfiles", formBackingObject = ResultsFile.class) 
public class ResultsFileController {

    @Autowired
    private ResultsFileMappingController resultsFileMappingController;

	@InitBinder
	protected void initBinder(HttpServletRequest request,
	        ServletRequestDataBinder binder) throws ServletException {
	    binder.registerCustomEditor(byte[].class,
	            new ByteArrayMultipartFileEditor());
	}

    @RequestMapping(value = "create.json", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> createJson(@Valid ResultsFile resultsFile, BindingResult bindingResult, Model uiModel, @RequestParam("content") CommonsMultipartFile content, HttpServletRequest httpServletRequest) {
        String result = create(resultsFile, bindingResult, uiModel, content, httpServletRequest);
        if (result.startsWith("redirect")) {
            String idValue = result.replace("redirect:/resultsfilemappings/", "");
            idValue = idValue.replace("?form", "");
            Long id = Long.decode(idValue);
            return resultsFileMappingController.updateFormJson(id, uiModel);
        } else {
            return new ResponseEntity<>("error", HttpStatus.OK);
        }
    }
	
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ResultsFile resultsFile, BindingResult bindingResult, Model uiModel, @RequestParam("content") CommonsMultipartFile content, HttpServletRequest httpServletRequest) {
        File testDataDir = new File("/data");
        if(!testDataDir.exists()) testDataDir.mkdir();
    	File dest = new File("/data/" + content.getOriginalFilename());
        try { 
            content.transferTo(dest);
            resultsFile.setCreated(new Date());
            resultsFile.setName(dest.getName());
            resultsFile.setFilesize(content.getSize());
            resultsFile.setFilePath(dest.getAbsolutePath());
            resultsFile.setContentType(content.getContentType());
        } catch (Exception e) {
            e.printStackTrace();
            return "resultsfiles/create";   
        }
        uiModel.asMap().clear();
        try{
        	resultsFile = ResultsFile.findResultsFilesByNameEqualsAndEvent(
        			resultsFile.getName(), resultsFile.getEvent()).getSingleResult();
        	System.out.println("not new");
        }catch(Exception e){
        	System.out.println("new");
            resultsFile.persist();
        }

        System.out.println("file e="+resultsFile.getEvent().getId());
        ResultsFileMapping mapping = new ResultsFileMapping();
        mapping.setResultsFile(resultsFile);
        mapping.setName(resultsFile.getName());
        mapping.persist();
        mapping.flush();
        
        return "redirect:/resultsfilemappings/"+mapping.getId()+"?form";
    }
}
