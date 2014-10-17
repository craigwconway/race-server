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

@RequestMapping("/usergroups")
@Controller
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
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
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
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        UserGroup userGroup = UserGroup.findUserGroup(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userGroup == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(userGroup.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<UserGroup> result = UserGroup.findAllUserGroups();
        return new ResponseEntity<String>(UserGroup.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        UserGroup userGroup = UserGroup.fromJsonToUserGroup(json);
        userGroup.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + userGroup.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (UserGroup userGroup : UserGroup.fromJsonArrayToUserGroups(json)) {
            userGroup.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserGroup userGroup = UserGroup.fromJsonToUserGroup(json);
        userGroup.setId(id);
        if (userGroup.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        UserGroup userGroup = UserGroup.findUserGroup(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userGroup == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        userGroup.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByGroupType", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserGroupsByGroupType(@RequestParam("groupType") UserGroupType groupType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserGroup.toJsonArray(UserGroup.findUserGroupsByGroupType(groupType).getResultList()), headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByNameEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserGroupsByNameEquals(@RequestParam("name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(UserGroup.toJsonArray(UserGroup.findUserGroupsByNameEquals(name).getResultList()), headers, HttpStatus.OK);
    }
}
