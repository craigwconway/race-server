package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;


@RequestMapping("/awardcategorys")
@Controller
public class AwardCategoryController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid AwardCategory awardCategory, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
        	System.out.println("caught errors");
        	System.out.println(bindingResult.getAllErrors());
            populateEditForm(uiModel, awardCategory);
            return "awardcategorys/create";
        }
        uiModel.asMap().clear();

        // hack for medals
        if(httpServletRequest.getParameterMap().containsKey("medal")){
        	awardCategory.setName(AwardCategory.MEDAL_PREFIX + awardCategory.getName());
        }
        
        awardCategory.setSortOrder(AwardCategory.findByEventType(awardCategory.getEventType()).size());
        
        awardCategory.persist();
        return (!awardCategory.isMedal()) 
        		? "redirect:/events/ageGenderRankings?event=" + awardCategory.getEventType().getEvent().getId()+"&gender="+awardCategory.getGender()
        		: "redirect:/events/awards?event=" + awardCategory.getEventType().getEvent().getId()+"&gender="+awardCategory.getGender();
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) long eventTypeId, Model uiModel) {
		EventType eventType = EventType.findEventType(eventTypeId);
		AwardCategory a = new AwardCategory();
		a.setSortOrder(AwardCategory.findByEventType(eventType).size());
        populateEditForm(uiModel, a);
        List<EventType> eventTypes = new ArrayList<EventType>();
        eventTypes.add(eventType);
        uiModel.addAttribute("eventTypes", eventTypes);
        return "awardcategorys/create";
    }
	
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid AwardCategory awardCategory, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, awardCategory);
            return "awardcategory/update";
        }
        uiModel.asMap().clear();
        
        // hack for medals
        if(httpServletRequest.getParameterMap().containsKey("medal")){
        	awardCategory.setName(AwardCategory.MEDAL_PREFIX + awardCategory.getName());
        }
        
        awardCategory.merge();
        return (!awardCategory.isMedal()) 
        		? "redirect:/events/ageGenderRankings?event=" + awardCategory.getEventType().getEvent().getId()+"&gender="+awardCategory.getGender()
        		: "redirect:/events/awards?event=" + awardCategory.getEventType().getEvent().getId()+"&gender="+awardCategory.getGender();
     }
	
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		AwardCategory cat = AwardCategory.findAwardCategory(id);
        populateEditForm(uiModel, cat);
        uiModel.addAttribute("eventType", cat.getEventType());
        return "awardcategorys/update";
    }

	@RequestMapping(value = "/delete/{id}", produces = "text/html",headers = "Accept=application/json")
	public String delete(@PathVariable("id") Long id) {
		AwardCategory a = AwardCategory.findAwardCategory(id);
        a.remove();
        return (!a.isMedal()) 
        		? "redirect:/events/ageGenderRankings?event=" + a.getEventType().getEvent().getId()+"&gender="+a.getGender()
        		: "redirect:/events/awards?event=" + a.getEventType().getEvent().getId()+"&gender="+a.getGender();
    }

	void populateEditForm(Model uiModel, AwardCategory awardCategory) {
        uiModel.addAttribute("awardCategory", awardCategory);
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
