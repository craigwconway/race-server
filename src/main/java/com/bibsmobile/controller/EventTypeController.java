package com.bibsmobile.controller;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventType;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/eventtypes")
@Controller
public class EventTypeController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventType eventType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventType);
            return "eventtypes/create";
        }
        uiModel.asMap().clear();
        eventType.persist();
        return "redirect:/eventtypes/" + this.encodeUrlPathSegment(eventType.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new EventType());
        return "eventtypes/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("eventtype", EventType.findEventType(id));
        uiModel.addAttribute("itemId", id);
        return "eventtypes/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("eventtypes", EventType.findEventTypeEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) EventType.countEventTypes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("eventtypes", EventType.findAllEventTypes(sortFieldName, sortOrder));
        }
        this.addDateTimeFormatPatterns(uiModel);
        return "eventtypes/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventType eventType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventType);
            return "eventtypes/update";
        }
        uiModel.asMap().clear();
        eventType.merge();
        return "redirect:/eventtypes/" + this.encodeUrlPathSegment(eventType.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, EventType.findEventType(id));
        return "eventtypes/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        EventType eventType = EventType.findEventType(id);
        eventType.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventtypes";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("eventType_starttime_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

    void populateEditForm(Model uiModel, EventType eventType) {
        uiModel.addAttribute("eventType", eventType);
        this.addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("events", Event.findAllEvents());
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
