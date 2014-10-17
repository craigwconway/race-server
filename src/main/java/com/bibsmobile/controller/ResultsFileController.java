package com.bibsmobile.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.ResultsFile;
import com.bibsmobile.model.ResultsFileMapping;
import com.bibsmobile.model.ResultsImport;
import com.bibsmobile.service.UserProfileService;

@RequestMapping("/resultsfiles")
@Controller
public class ResultsFileController {

    @Autowired
    private ResultsFileMappingController resultsFileMappingController;

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    @RequestMapping(value = "create.json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> createJson(@Valid ResultsFile resultsFile, BindingResult bindingResult, Model uiModel, @RequestParam("content") CommonsMultipartFile content,
            HttpServletRequest httpServletRequest) {
        String result = create(resultsFile, bindingResult, uiModel, content, httpServletRequest);
        if (result.startsWith("redirect")) {
            String idValue = result.replace("redirect:/resultsfilemappings/", "");
            idValue = idValue.replace("?form", "");
            Long id = Long.decode(idValue);
            return this.resultsFileMappingController.updateFormJson(id, uiModel);
        }
        return new ResponseEntity<>("error", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ResultsFile resultsFile, BindingResult bindingResult, Model uiModel, @RequestParam("content") CommonsMultipartFile content,
            HttpServletRequest httpServletRequest) {
        File testDataDir = new File("/data");
        if (!testDataDir.exists())
            testDataDir.mkdir();
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
        try {
            resultsFile = ResultsFile.findResultsFilesByNameEqualsAndEvent(resultsFile.getName(), resultsFile.getEvent()).getSingleResult();
            System.out.println("not new");
        } catch (Exception e) {
            System.out.println("new");
            resultsFile.persist();
        }

        System.out.println("file e=" + resultsFile.getEvent().getId());
        ResultsFileMapping mapping = new ResultsFileMapping();
        mapping.setResultsFile(resultsFile);
        mapping.setName(resultsFile.getName());
        mapping.persist();
        mapping.flush();

        return "redirect:/resultsfilemappings/" + mapping.getId() + "?form";
    }

    @Autowired
    UserProfileService userProfileService;

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new ResultsFile());
        return "resultsfiles/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("resultsfile", ResultsFile.findResultsFile(id));
        uiModel.addAttribute("itemId", id);
        return "resultsfiles/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("resultsfiles", ResultsFile.findResultsFileEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) ResultsFile.countResultsFiles() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("resultsfiles", ResultsFile.findAllResultsFiles(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "resultsfiles/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid ResultsFile resultsFile, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, resultsFile);
            return "resultsfiles/update";
        }
        uiModel.asMap().clear();
        resultsFile.merge();
        return "redirect:/resultsfiles/" + encodeUrlPathSegment(resultsFile.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, ResultsFile.findResultsFile(id));
        return "resultsfiles/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        ResultsFile resultsFile = ResultsFile.findResultsFile(id);
        resultsFile.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/resultsfiles";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("resultsFile_created_date_format", DateTimeFormat.patternForStyle("SS", LocaleContextHolder.getLocale()));
    }

    void populateEditForm(Model uiModel, ResultsFile resultsFile) {
        uiModel.addAttribute("resultsFile", resultsFile);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("events", Event.findAllEvents());
        uiModel.addAttribute("resultsfilemappings", ResultsFileMapping.findAllResultsFileMappings());
        uiModel.addAttribute("resultsimports", ResultsImport.findAllResultsImports());
        uiModel.addAttribute("userprofiles", this.userProfileService.findAllUserProfiles());
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
