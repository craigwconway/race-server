package com.bibsmobile.controller;
import com.bibsmobile.model.UserGroupUserAuthority;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/usergroupuserauthorities")
@Controller
@RooWebScaffold(path = "usergroupuserauthorities", formBackingObject = UserGroupUserAuthority.class)
public class UserGroupUserAuthorityController {
}
