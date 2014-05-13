package com.bibsmobile.controller;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/userauthorities")
@Controller
@RooWebScaffold(path = "userauthorities", formBackingObject = UserAuthorities.class, update = false)
public class UserAuthoritiesController {

    @Autowired
    private ConversionService conversionService;

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "userprofile", required = true) Long userProfileId, Model uiModel) {
        UserAuthorities userAuthorities = new UserAuthorities();
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        UserAuthoritiesID id = new UserAuthoritiesID(userProfile, userProfile.getNotAddedAuthorities().get(0));
        userAuthorities.setId(id);

        populateEditForm(uiModel, userAuthorities);
        return "userauthorities/createUA";
    }


    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserAuthorities userAuthorities, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        UserAuthoritiesID id = new UserAuthoritiesID();
        id.setUserAuthority(userAuthorities.getUserAuthority());
        id.setUserProfile(userAuthorities.getUserProfile());
        userAuthorities.setId(id);
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userAuthorities);
            return "userauthorities/createUA";
        }
        uiModel.asMap().clear();
        userAuthorities.persist();
        return "redirect:/userauthorities?userprofile=" + userAuthorities.getUserProfile().getId();
    }

    @RequestMapping(value = "/{id}/{aid}", produces = "text/html")
    public String show(@PathVariable("id") Long userProfileId, @PathVariable("aid") Long authorityId, Model uiModel) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(
                new UserAuthoritiesID(UserProfile.findUserProfile(userProfileId), UserAuthority.findUserAuthority(authorityId)));
        uiModel.addAttribute("userauthorities", userAuthorities);
        uiModel.addAttribute("itemId", userProfileId + "/" + authorityId);
        return "userauthorities/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "userprofile", required = true) Long userProfileId, Model uiModel) {
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        TypedQuery<UserAuthorities> userAuthoritiesesByUserProfile = UserAuthorities.findUserAuthoritiesesByUserProfile(userProfile);
        uiModel.addAttribute("userauthoritieses", userAuthoritiesesByUserProfile.getResultList());
        uiModel.addAttribute("userprofile", userProfile);
        return "userauthorities/listUA";
    }

    @RequestMapping(value = "/{id}/{aid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long userProfileId, @PathVariable("aid") Long authorityId, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(
                new UserAuthoritiesID(UserProfile.findUserProfile(userProfileId), UserAuthority.findUserAuthority(authorityId)));
        userAuthorities.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/userauthorities?userprofile=" + userAuthorities.getUserProfile().getId();
    }

    void populateEditForm(Model uiModel, UserAuthorities userAuthorities) {
        uiModel.addAttribute("userAuthorities", userAuthorities);
        uiModel.addAttribute("userauthoritys", userAuthorities.getId().getUserProfile().getNotAddedAuthorities());
        List<UserProfile> userProfiles = new ArrayList<>();
        userProfiles.add(userAuthorities.getId().getUserProfile());
        uiModel.addAttribute("userprofiles", userProfiles);
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
