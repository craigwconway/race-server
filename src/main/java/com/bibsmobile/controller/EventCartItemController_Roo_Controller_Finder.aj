// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.bibsmobile.controller;

import com.bibsmobile.controller.EventCartItemController;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventCartItem;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect EventCartItemController_Roo_Controller_Finder {
    
    @RequestMapping(params = { "find=ByEvent", "form" }, method = RequestMethod.GET)
    public String EventCartItemController.findEventCartItemsByEventForm(Model uiModel) {
        uiModel.addAttribute("events", Event.findAllEvents());
        return "eventitems/findEventCartItemsByEvent";
    }
    
    @RequestMapping(params = "find=ByEvent", method = RequestMethod.GET)
    public String EventCartItemController.findEventCartItemsByEvent(@RequestParam("event") Event event, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("eventcartitems", EventCartItem.findEventCartItemsByEvent(event, sortFieldName, sortOrder).setFirstResult(firstResult).setMaxResults(sizeNo).getResultList());
            float nrOfPages = (float) EventCartItem.countFindEventCartItemsByEvent(event) / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("eventcartitems", EventCartItem.findEventCartItemsByEvent(event, sortFieldName, sortOrder).getResultList());
        }
        addDateTimeFormatPatterns(uiModel);
        return "eventitems/list";
    }
    
}
