package com.bibsmobile.controller;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/authoritiesingroup")
@Controller
public class UserAuthoritiesInUserGroupsController {


    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "usergroup", required = true) Long userGroupId, Model uiModel) {
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        uiModel.addAttribute("userGroup", userGroup);
        return "authoritiesingroup/list";
    }


    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "usergroup", required = true) Long userGroupId, Model uiModel) {
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        populateEditForm(uiModel, userGroup);
        return "authoritiesingroup/create";
    }


    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserGroup userGroup, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userGroup);
            return "authoritiesingroup/create";
        }
        uiModel.asMap().clear();
        userGroup.persist();
        return "redirect:/authoritiesingroup?usergoup=" + userGroup.getId();
    }

    @RequestMapping(value = "{id}/{uid}/{aid}", produces = "text/html")
    public String show(@PathVariable("id") Long userGroupId, @PathVariable("uid") Long userProfileId, @PathVariable("aid") Long authorityId, Model uiModel) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(
                new UserAuthoritiesID(UserProfile.findUserProfile(userProfileId), UserAuthority.findUserAuthority(authorityId)));
        uiModel.addAttribute("userauthorities", userAuthorities);
        uiModel.addAttribute("itemId", userProfileId + "/" + authorityId);
        return "authoritiesingroup/show";
    }

    @RequestMapping(value = "/{id}/{aid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long userProfileId, @PathVariable("aid") Long authorityId, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(
                new UserAuthoritiesID(UserProfile.findUserProfile(userProfileId), UserAuthority.findUserAuthority(authorityId)));
        userAuthorities.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/authoritiesingroup?userprofile=" + userAuthorities.getUserProfile().getId();
    }

    void populateEditForm(Model uiModel, UserGroup userGroup) {
        uiModel.addAttribute("userGroup", userGroup);
        uiModel.addAttribute("userauthorities", UserAuthorities.findAllUserAuthoritieses());
    }

}
