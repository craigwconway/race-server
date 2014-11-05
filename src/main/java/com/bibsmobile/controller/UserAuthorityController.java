package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

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

import com.bibsmobile.model.UserAuthorities;
import com.bibsmobile.model.UserAuthority;

@RequestMapping("/userauthoritys")
@Controller
public class UserAuthorityController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid UserAuthority userAuthority, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, userAuthority);
            return "userauthoritys/create";
        }
        uiModel.asMap().clear();
        userAuthority.persist();
        return "redirect:/userauthoritys/" + this.encodeUrlPathSegment(userAuthority.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new UserAuthority());
        return "userauthoritys/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("userauthority", UserAuthority.findUserAuthority(id));
        uiModel.addAttribute("itemId", id);
        return "userauthoritys/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("userauthoritys", UserAuthority.findUserAuthorityEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) UserAuthority.countUserAuthoritys() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("userauthoritys", UserAuthority.findAllUserAuthoritys(sortFieldName, sortOrder));
        }
        return "userauthoritys/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid UserAuthority userAuthority, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, userAuthority);
            return "userauthoritys/update";
        }
        uiModel.asMap().clear();
        userAuthority.merge();
        return "redirect:/userauthoritys/" + this.encodeUrlPathSegment(userAuthority.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, UserAuthority.findUserAuthority(id));
        return "userauthoritys/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        UserAuthority userAuthority = UserAuthority.findUserAuthority(id);
        userAuthority.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/userauthoritys";
    }

    void populateEditForm(Model uiModel, UserAuthority userAuthority) {
        uiModel.addAttribute("userAuthority", userAuthority);
        uiModel.addAttribute("userauthoritieses", UserAuthorities.findAllUserAuthoritieses());
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
        UserAuthority userAuthority = UserAuthority.findUserAuthority(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (userAuthority == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userAuthority.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<UserAuthority> result = UserAuthority.findAllUserAuthoritys();
        return new ResponseEntity<>(UserAuthority.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        UserAuthority userAuthority = UserAuthority.fromJsonToUserAuthority(json);
        userAuthority.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = this.getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + userAuthority.getId()).build().toUriString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (UserAuthority userAuthority : UserAuthority.fromJsonArrayToUserAuthoritys(json)) {
            userAuthority.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        UserAuthority userAuthority = UserAuthority.fromJsonToUserAuthority(json);
        userAuthority.setId(id);
        if (userAuthority.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        UserAuthority userAuthority = UserAuthority.findUserAuthority(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (userAuthority == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        userAuthority.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByAuthorityEquals", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindUserAuthoritysByAuthorityEquals(@RequestParam("authority") String authority) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(UserAuthority.toJsonArray(UserAuthority.findUserAuthoritysByAuthorityEquals(authority).getResultList()), headers, HttpStatus.OK);
    }
}
