package com.bibsmobile.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.bibsmobile.model.UserAuthority;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;

@RequestMapping("/userauthoritys")
@Controller
@RooWebScaffold(path = "userauthoritys", formBackingObject = UserAuthority.class)
@RooWebJson(jsonObject = UserAuthority.class)
public class UserAuthorityController {
}
