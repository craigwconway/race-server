package com.bibsmobile.controller;
import com.bibsmobile.model.EventCartItem;
import com.bibsmobile.model.EventCartItemPriceChange;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.roo.addon.web.mvc.controller.finder.RooWebFinder;

@RequestMapping("/eventitemspricechanges")
@Controller
@RooWebScaffold(path = "eventitemspricechanges", formBackingObject = EventCartItemPriceChange.class)
@RooWebJson(jsonObject = EventCartItemPriceChange.class)
@RooWebFinder
public class EventCartItemPriceChangeController {

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "eventitem", required = true) Long eventitem, Model uiModel) {
        EventCartItemPriceChange i = new EventCartItemPriceChange();
        EventCartItem e = EventCartItem.findEventCartItem(eventitem);
        i.setEventCartItem(e);
        List<EventCartItem> l = new ArrayList<>();
        l.add(e);
        uiModel.addAttribute("eventcartitems", l);
        populateEditForm(uiModel, i);
        return "eventitemspricechanges/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventCartItemPriceChange i = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        List<EventCartItem> l = new ArrayList<>();
        l.add(i.getEventCartItem());
        uiModel.addAttribute("eventcartitems", l);
        populateEditForm(uiModel, i);
        return "eventitemspricechanges/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "eventitem", required = true) Long eventitem, Model uiModel) {
        EventCartItem eventCartItem = EventCartItem.findEventCartItem(eventitem);
        uiModel.addAttribute("eventitem", eventCartItem);
        uiModel.addAttribute("eventcartitempricechanges", EventCartItemPriceChange.findEventCartItemPriceChangesByEventCartItem(eventCartItem).getResultList());
        addDateTimeFormatPatterns(uiModel);
        return "eventitemspricechanges/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.findEventCartItemPriceChange(id);
        eventCartItemPriceChange.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventitemspricechanges?eventitem=" + eventCartItemPriceChange.getEventCartItem().getId();
    }

    void populateEditForm(Model uiModel, EventCartItemPriceChange eventCartItemPriceChange) {
        uiModel.addAttribute("eventCartItemPriceChange", eventCartItemPriceChange);
        addDateTimeFormatPatterns(uiModel);
    }

    /*
     * JSON
     * */
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public String createFromJson(@RequestBody String json) {
        EventCartItemPriceChange eventCartItemPriceChange = EventCartItemPriceChange.fromJsonToEventCartItemPriceChange(json);
        eventCartItemPriceChange.persist();
        return eventCartItemPriceChange.toJson();
    }
}
