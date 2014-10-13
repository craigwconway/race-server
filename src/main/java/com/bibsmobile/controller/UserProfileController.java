package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthority;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserProfile;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/userprofiles")
@Controller
@RooWebJson(jsonObject = UserProfile.class)
@RooWebScaffold(path = "userprofiles", formBackingObject = UserProfile.class)
public class UserProfileController {

    @RequestMapping(value = "/byusername/{username}", produces = "text/html")
    public static String showUsername(@PathVariable("username") String username, Model uiModel) {
        UserProfile u = UserProfile.findUserProfilesByUsernameEquals(username).getSingleResult();
        uiModel.addAttribute("userprofile", u);
        uiModel.addAttribute("itemId", u.getId());
        return "userprofiles/show";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        UserProfile u = UserProfile.findUserProfile(id);
        uiModel.addAttribute("userprofile", u);
        uiModel.addAttribute("itemId", u.getId());
        return "userprofiles/show";
    }

    @RequestMapping(value = "/search/byusergroup/{userGroupId}", method = RequestMethod.GET)
    @ResponseBody
    public String findByUserGroup(@PathVariable Long userGroupId) {
        UserGroup userGroup = UserGroup.findUserGroup(userGroupId);
        Map<Long, UserProfile> userProfiles = new HashMap<>();
        if (userGroup != null) {
            for (UserGroupUserAuthority userGroupUserAuthority : userGroup.getUserGroupUserAuthorities()) {
                UserProfile userProfile = userGroupUserAuthority.getUserAuthorities().getUserProfile();
                if (!userProfiles.containsKey(userProfile.getId())) {
                    userProfiles.put(userProfile.getId(), userProfile);
                }
            }
        }
        return UserProfile.toJsonArray(userProfiles.values());
    }

    @RequestMapping(value = "/search/teamcaptainsbyevent/{eventId}", method = RequestMethod.GET)
    @ResponseBody
    public String findTeamCaptainsByEvent(@PathVariable Long eventId) {
        Event event = Event.findEvent(eventId);
        Map<Long, UserProfile> teamCaptains = new HashMap<>();
        if (event != null) {
            for (EventUserGroup eventUserGroup : event.getEventUserGroups()) {
                for (UserGroupUserAuthority userGroupUserAuthority : eventUserGroup.getUserGroup().getUserGroupUserAuthorities()) {
                    UserAuthorities userAuthorities = userGroupUserAuthority.getUserAuthorities();
                    UserProfile userProfile = userAuthorities.getUserProfile();
                    if (userAuthorities.getUserAuthority().getAuthority().equals("ROLE_TEAMCAPTAIN") && !teamCaptains.containsKey(userProfile.getId())) {
                        teamCaptains.put(userProfile.getId(), userProfile);
                    }
                }
            }
        }
        return UserProfile.toJsonArray(teamCaptains.values());
    }


    void populateEditForm(Model uiModel, UserProfile userProfile) {
        uiModel.addAttribute("userProfile", userProfile);
        uiModel.addAttribute("userauthoritys", UserAuthority.findAllUserAuthoritys());
        addDateTimeFormatPatterns(uiModel);
    }

}
