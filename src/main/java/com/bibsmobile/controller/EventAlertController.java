package com.bibsmobile.controller;
import com.bibsmobile.model.EventAlert;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

@RequestMapping("/eventalerts")
@Controller
@RooWebScaffold(path = "eventalerts", formBackingObject = EventAlert.class)
@RooWebJson(jsonObject = EventAlert.class)
public class EventAlertController {
}
