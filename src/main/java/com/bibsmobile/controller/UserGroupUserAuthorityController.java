package com.bibsmobile.controller;

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthoritiesID;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserGroupUserAuthorityID;
import com.bibsmobile.model.UserProfile;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.TypedQuery;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequestMapping("/usergroupuserauthorities")
@Controller
@RooWebScaffold(path = "usergroupuserauthorities", formBackingObject = UserGroupUserAuthority.class, update = false)
public class UserGroupUserAuthorityController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "usergroup") Long userGroupId, @RequestParam(value = "userprofile", required = false) Long userProfileId, Model uiModel) {
        UserGroupUserAuthority userGroupUserAuthority = new UserGroupUserAuthority();

        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        UserGroupUserAuthorityID id = new UserGroupUserAuthorityID();
        id.setUserGroup(userGroup);
        List<UserProfile> allUserProfiles = UserProfile.findAllUserProfiles();
        if (userProfileId == null) {
            if (CollectionUtils.isNotEmpty(allUserProfiles)) {
                userProfileId = allUserProfiles.get(0).getId();
            }
        }
        if (userProfileId != null) {
            UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
            UserAuthorities userAuthorities = new UserAuthorities();
            userAuthorities.setUserProfile(userProfile);
            UserAuthoritiesID userAuthoritiesID = new UserAuthoritiesID();
            userAuthoritiesID.setUserProfile(userProfile);
            userAuthorities.setId(userAuthoritiesID);
            id.setUserAuthorities(userAuthorities);
            userGroupUserAuthority.setUserAuthorities(userAuthorities);
        }

        userGroupUserAuthority.setId(id);
        userGroupUserAuthority.setUserGroup(userGroup);
        populateEditForm(uiModel, userGroupUserAuthority);
        uiModel.addAttribute("userprofiles", allUserProfiles);
        return "usergroupuserauthorities/createUGUA";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserGroupUserAuthority userGroupUserAuthority, BindingResult bindingResult, Model uiModel) {
        UserGroupUserAuthorityID id = new UserGroupUserAuthorityID();
        UserAuthoritiesID userAuthoritiesID = new UserAuthoritiesID();
        userAuthoritiesID.setUserAuthority(userGroupUserAuthority.getUserAuthorities().getUserAuthority());
        userAuthoritiesID.setUserProfile(userGroupUserAuthority.getUserAuthorities().getUserProfile());
        UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(userAuthoritiesID);
        userGroupUserAuthority.setUserAuthorities(userAuthorities);
        id.setUserAuthorities(userAuthorities);
        id.setUserGroup(userGroupUserAuthority.getUserGroup());
        userGroupUserAuthority.setId(id);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("userprofiles", UserProfile.findAllUserProfiles());
            populateEditForm(uiModel, userGroupUserAuthority);
            return "usergroupuserauthorities/createUGUA";
        }
        uiModel.asMap().clear();
        userGroupUserAuthority.getUserAuthorities().getUserGroupUserAuthorities().add(userGroupUserAuthority);
        userGroupUserAuthority.getUserGroup().getUserGroupUserAuthorities().add(userGroupUserAuthority);
        userGroupUserAuthority.persist();
        return "redirect:/usergroupuserauthorities?usergroup=" + userGroupUserAuthority.getUserGroup().getId();
    }

    @RequestMapping(value = "{id}/{uid}/{aid}", produces = "text/html")
    public String show(@PathVariable("id") Long userGroupId, @PathVariable("uid") Long userProfileId, @PathVariable("aid") Long authorityId, Model uiModel) {
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        UserAuthority userAuthority = UserAuthority.findUserAuthority(authorityId);
        if (userAuthority != null && userProfile != null) {
            UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(new UserAuthoritiesID(userProfile, userAuthority));
            UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
            if (userAuthorities != null && userGroup != null) {
                UserGroupUserAuthority userGroupUserAuthority = UserGroupUserAuthority.findUserGroupUserAuthority(new UserGroupUserAuthorityID(userGroup, userAuthorities));
                if (userGroupUserAuthority != null) {
                    uiModel.addAttribute("usergroupuserauthority", userGroupUserAuthority);
                }
            }
        }
        uiModel.addAttribute("itemId", userGroupId + "/" + userProfileId + "/" + authorityId);
        return "usergroupuserauthorities/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "usergroup", required = true) Long userGroupId, Model uiModel) {
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        if (userGroup != null) {
            TypedQuery<UserGroupUserAuthority> userGroupUserAuthoritysByUserGroup = UserGroupUserAuthority.findUserGroupUserAuthoritysByUserGroup(userGroup);
            uiModel.addAttribute("usergroupuserauthoritys", userGroupUserAuthoritysByUserGroup.getResultList());
        }
        uiModel.addAttribute("usergroup", userGroup);
        return "usergroupuserauthorities/listUGUA";
    }

    @RequestMapping(value = "/{id}/{uid}/{aid}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long userGroupId, @PathVariable("uid") Long userProfileId, @PathVariable("aid") Long authorityId, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserProfile userProfile = UserProfile.findUserProfile(userProfileId);
        UserAuthority userAuthority = UserAuthority.findUserAuthority(authorityId);
        if (userAuthority != null && userProfile != null) {
            UserAuthorities userAuthorities = UserAuthorities.findUserAuthorities(new UserAuthoritiesID(userProfile, userAuthority));
            UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
            if (userAuthorities != null && userGroup != null) {
                UserGroupUserAuthority userGroupUserAuthority = UserGroupUserAuthority.findUserGroupUserAuthority(new UserGroupUserAuthorityID(userGroup, userAuthorities));
                if (userGroupUserAuthority != null) {
                    userGroupUserAuthority.remove();
                }
            }
        }
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/usergroupuserauthorities?usergroup=" + userGroupId;
    }


    void populateEditForm(Model uiModel, UserGroupUserAuthority userGroupUserAuthority) {
        List<UserAuthorities> userAuthoritiesListForRoles;
        UserAuthorities userAuthoritieses = userGroupUserAuthority.getUserAuthorities();
        if (userAuthoritieses != null) {
            UserProfile userProfile = userAuthoritieses.getUserProfile();
            if (userProfile != null) {
                //list to find available roles
                userAuthoritiesListForRoles = UserAuthorities.findUserAuthoritiesesByUserProfile(userProfile).getResultList();
            } else {
                userAuthoritiesListForRoles = Collections.EMPTY_LIST;
            }
        } else {
            userAuthoritiesListForRoles = Collections.EMPTY_LIST;
        }
        List<UserAuthority> userRoles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userAuthoritiesListForRoles)) {

            for (UserAuthorities userAuthorities : userAuthoritiesListForRoles) {
                boolean contains = false;
                for (UserAuthority userRole : userRoles) {
                    rolesIteration:
                    if (userAuthorities.getUserAuthority().getId().equals(userRole.getId())) {
                        contains = true;
                        break rolesIteration;
                    }
                }
                if (!contains) {
                    userRoles.add(userAuthorities.getUserAuthority());
                }
            }
        } else {
            userRoles = UserAuthority.findAllUserAuthoritys();
        }
        uiModel.addAttribute("userGroupUserAuthority", userGroupUserAuthority);
        uiModel.addAttribute("userauthorities", userRoles);
        uiModel.addAttribute("usergroups", Arrays.asList(userGroupUserAuthority.getUserGroup()));
    }
}
