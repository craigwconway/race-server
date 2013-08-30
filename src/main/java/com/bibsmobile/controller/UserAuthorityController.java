package com.bibsmobile.controller;

import com.bibsmobile.model.UserAuthority;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/userauthoritys")
@Controller
@RooWebScaffold(path = "userauthoritys", formBackingObject = UserAuthority.class)
public class UserAuthorityController {
}
