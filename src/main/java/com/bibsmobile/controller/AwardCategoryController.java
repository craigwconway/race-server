package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.AwardCategory;
import com.bibsmobile.model.AwardsTemplate;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import com.bibsmobile.model.UserProfile;
import com.bibsmobile.service.AwardsImmortalCache;
import com.bibsmobile.util.PermissionsUtil;
import com.bibsmobile.util.SpringJSONUtil;
import com.bibsmobile.util.UserProfileUtil;


@RequestMapping("/awardcategorys")
@Controller
public class AwardCategoryController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid AwardCategory awardCategory, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		System.out.println("creating awardcategory");
        if (bindingResult.hasErrors()) {
        	System.out.println("caught errors");
        	System.out.println(bindingResult.getAllErrors());
            populateEditForm(uiModel, awardCategory);
            return "awardcategorys/create";
        }
        System.out.println("clearing uimodel");
        uiModel.asMap().clear();
        System.out.println("Award Category: type - " + awardCategory.getEventType().getTypeName() + " ages: " + awardCategory.getAgeMin() + " to " + awardCategory.getAgeMax()
        		+ " Genders" + awardCategory.getGender());
        // hack for medals
        if(httpServletRequest.getParameterMap().containsKey("medal")){
        	awardCategory.setMedal(true);
        }
        
        awardCategory.setSortOrder(AwardCategory.findByEventType(awardCategory.getEventType()).size());
        
        awardCategory.persist();
        AwardsImmortalCache.clearAwardsCache(awardCategory.getEventType().getId());
        return (!awardCategory.isMedal()) 
        		? "redirect:/events/ageGenderRankings?event=" + awardCategory.getEventType().getEvent().getId()+"&type="+awardCategory.getEventType().getId()+"&gender="+awardCategory.getGender()
        		: "redirect:/events/awards?event=" + awardCategory.getEventType().getEvent().getId()+"&type="+awardCategory.getEventType().getId()+"&gender="+awardCategory.getGender();
    }

	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) long eventTypeId, Model uiModel) {
		EventType eventType = EventType.findEventType(eventTypeId);
		AwardCategory a = new AwardCategory();
		a.setSortOrder(AwardCategory.findByEventType(eventType).size());
        populateEditForm(uiModel, a);
        uiModel.addAttribute("event", eventType.getEvent());
        uiModel.addAttribute("eventType", eventType);
        return "awardcategorys/create";
    }

	@RequestMapping(value = "/template/create", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
    public ResponseEntity <String> createTemplate(@RequestParam(value = "event", required = true) long eventTypeId,
    		@RequestParam(value="name", required = true) String name,
    		@RequestParam(value="default", defaultValue = "false") Boolean defaultStatus) {
		EventType eventType = EventType.findEventType(eventTypeId);
		UserProfile user = UserProfileUtil.getLoggedInUserProfile();
		AwardsTemplate template = new AwardsTemplate(eventType, name, defaultStatus, user);
		if (!PermissionsUtil.isSysAdmin(user)) {
			template.setDefaultTemplate(false);
		}
		for(AwardCategory category : template.getCategories()) {
			category.persist();
		}
		template.persist();
		
        return SpringJSONUtil.returnStatusMessage("created", HttpStatus.OK);
        
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
        	awardCategory.setMedal(true);
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
