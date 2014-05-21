package com.bibsmobile.controller;
import com.bibsmobile.model.EventType;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/eventtypes")
@Controller
@RooWebScaffold(path = "eventtypes", formBackingObject = EventType.class)
public class EventTypeController {
}
