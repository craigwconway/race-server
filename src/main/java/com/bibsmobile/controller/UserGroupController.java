package com.bibsmobile.controller;
import com.bibsmobile.model.UserGroup;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/usergroups")
@Controller
@RooWebScaffold(path = "usergroups", formBackingObject = UserGroup.class)
public class UserGroupController {
}
