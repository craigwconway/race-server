package com.bibsmobile.controller;

import com.bibsmobile.model.ResultsFile;
import java.io.File;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@RequestMapping("/resultsfiles")
@Controller
@RooWebScaffold(path = "resultsfiles", formBackingObject = ResultsFile.class)
public class ResultsFileController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ResultsFile resultsFile, BindingResult bindingResult, Model uiModel, @RequestParam("content") CommonsMultipartFile content, HttpServletRequest httpServletRequest) {
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
        
        return "redirect:/resultsfiles/" + encodeUrlPathSegment(resultsFile.getId().toString(), httpServletRequest);
    }
}
