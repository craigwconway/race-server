package com.bibsmobile.controller;
import com.bibsmobile.model.EventMap;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

@RequestMapping("/eventmaps")
@Controller
@RooWebScaffold(path = "eventmaps", formBackingObject = EventMap.class)
@RooWebJson(jsonObject = EventMap.class)
public class EventMapController {
}
