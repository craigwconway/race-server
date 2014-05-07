package com.bibsmobile.controller;
import com.bibsmobile.model.EventResult;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

@RequestMapping("/eventresults")
@Controller
@RooWebScaffold(path = "eventresults", formBackingObject = EventResult.class)
@RooWebJson(jsonObject = EventResult.class)
public class EventResultController {
}
