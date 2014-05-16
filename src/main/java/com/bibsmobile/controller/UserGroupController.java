package com.bibsmobile.controller;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventUserGroup;
import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserGroup;
import com.bibsmobile.model.UserGroupType;
import com.bibsmobile.model.UserGroupUserAuthority;
import com.bibsmobile.model.UserProfile;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

@RequestMapping("/usergroups")
@Controller
@RooWebScaffold(path = "usergroups", formBackingObject = UserGroup.class)
@RooWebJson(jsonObject = UserGroup.class)
public class UserGroupController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserGroup userGroup, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userGroup);
            return "usergroups/create";
        }
        uiModel.asMap().clear();
        userGroup.persist();
        return "redirect:/usergroups/" + encodeUrlPathSegment(userGroup.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new UserGroup());
        return "usergroups/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("usergroup", UserGroup.findUserGroup(id));
        uiModel.addAttribute("itemId", id);
        return "usergroups/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("usergroups", UserGroup.findUserGroupEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) UserGroup.countUserGroups() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("usergroups", UserGroup.findAllUserGroups(sortFieldName, sortOrder));
        }
        return "usergroups/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid UserGroup userGroup, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, userGroup);
            return "usergroups/update";
        }
        UserGroup ugToUpdate = UserGroup.findUserGroup(userGroup.getId());
        ugToUpdate.setBibWrites(userGroup.getBibWrites());
        ugToUpdate.setName(userGroup.getName());
        ugToUpdate.setGroupType(userGroup.getGroupType());
        uiModel.asMap().clear();
        ugToUpdate.merge();
        return "redirect:/usergroups/" + encodeUrlPathSegment(userGroup.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, UserGroup.findUserGroup(id));
        return "usergroups/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        UserGroup userGroup = UserGroup.findUserGroup(id);
        userGroup.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/usergroups";
    }

    @RequestMapping(value = "/search/byeventgrouptype", method = RequestMethod.GET)
    @ResponseBody
    public String findByEventGroupType() {
        return UserGroup.toJsonArray(UserGroup.findUserGroupsByGroupType(UserGroupType.COMPANY).getResultList());
    }

    @RequestMapping(value = "/search/teamgroups/byevent/{eventId}", method = RequestMethod.GET)
    @ResponseBody
    public String findTeamGroupsByEvent(@PathVariable Long eventId) {
        Event event = Event.findEvent(eventId);
        Map<Long, UserGroup> userGroups = new HashMap<>();
        if (event != null) {
            for (EventUserGroup eventUserGroup : event.getEventUserGroups()) {
                UserGroup userGroup = eventUserGroup.getUserGroup();
                if (userGroup.getGroupType() == UserGroupType.TEAM && !userGroups.containsKey(userGroup.getId())) {
                    userGroups.put(userGroup.getId(), userGroup);
                }
            }
        }
        return UserGroup.toJsonArray(userGroups.values());
    }

    void populateEditForm(Model uiModel, UserGroup userGroup) {
        uiModel.addAttribute("userGroup", userGroup);
        uiModel.addAttribute("events", Event.findAllEvents());
        uiModel.addAttribute("userauthoritieses", UserAuthorities.findAllUserAuthoritieses());
        uiModel.addAttribute("usergrouptypes", Arrays.asList(UserGroupType.values()));
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
