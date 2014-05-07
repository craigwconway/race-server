// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.EventAlertController;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventAlert;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect EventAlertController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String EventAlertController.create(@Valid EventAlert eventAlert, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventAlert);
            return "eventalerts/create";
        }
        uiModel.asMap().clear();
        eventAlert.persist();
        return "redirect:/eventalerts/" + encodeUrlPathSegment(eventAlert.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String EventAlertController.createForm(Model uiModel) {
        populateEditForm(uiModel, new EventAlert());
        return "eventalerts/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String EventAlertController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("eventalert", EventAlert.findEventAlert(id));
        uiModel.addAttribute("itemId", id);
        return "eventalerts/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String EventAlertController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("eventalerts", EventAlert.findEventAlertEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) EventAlert.countEventAlerts() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("eventalerts", EventAlert.findAllEventAlerts(sortFieldName, sortOrder));
        }
        return "eventalerts/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String EventAlertController.update(@Valid EventAlert eventAlert, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventAlert);
            return "eventalerts/update";
        }
        uiModel.asMap().clear();
        eventAlert.merge();
        return "redirect:/eventalerts/" + encodeUrlPathSegment(eventAlert.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String EventAlertController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, EventAlert.findEventAlert(id));
        return "eventalerts/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String EventAlertController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EventAlert eventAlert = EventAlert.findEventAlert(id);
        eventAlert.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventalerts";
    }
    
    void EventAlertController.populateEditForm(Model uiModel, EventAlert eventAlert) {
        uiModel.addAttribute("eventAlert", eventAlert);
        uiModel.addAttribute("events", Event.findAllEvents());
    }
    
    String EventAlertController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
