// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.EventMapController;
import com.bibsmobile.model.EventMap;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect EventMapController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String EventMapController.create(@Valid EventMap eventMap, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventMap);
            return "eventmaps/create";
        }
        uiModel.asMap().clear();
        eventMap.persist();
        return "redirect:/eventmaps/" + encodeUrlPathSegment(eventMap.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String EventMapController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("eventmap", EventMap.findEventMap(id));
        uiModel.addAttribute("itemId", id);
        return "eventmaps/show";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String EventMapController.update(@Valid EventMap eventMap, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventMap);
            return "eventmaps/update";
        }
        uiModel.asMap().clear();
        eventMap.merge();
        return "redirect:/eventmaps/" + encodeUrlPathSegment(eventMap.getId().toString(), httpServletRequest);
    }
    
    String EventMapController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
