package com.bibsmobile.controller;
import com.bibsmobile.model.EventUserGroup;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/eventusergroups")
@Controller
@RooWebScaffold(path = "eventusergroups", formBackingObject = EventUserGroup.class)
public class EventUserGroupController {
}
