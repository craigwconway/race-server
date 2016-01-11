package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;

/**
 * This is a controller for unauthenticated webapp access of the main site.
 * The header bar is removed here.
 * @author galen
 * @since 2016-1-11
 *
 */
@RequestMapping("/r")
@Controller
public class WebController {
	
	/**
	 * Render a view of an organizer profile.
	 * @param id ID of organization to render.
	 * @param uiModel Model for rendering attributes on.
	 * @return Rendered JSPX template.
	 */
    @RequestMapping(value = "/org/{id}", produces = "text/html")
    public String org(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("usergroup", UserGroup.findUserGroup(id));
        return "r/org";
    }
    
    
    
    
}
