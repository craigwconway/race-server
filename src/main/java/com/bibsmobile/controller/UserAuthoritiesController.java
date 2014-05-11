package com.bibsmobile.controller;
import com.bibsmobile.model.UserAuthorities;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/userauthorities")
@Controller
@RooWebScaffold(path = "userauthorities", formBackingObject = UserAuthorities.class)
public class UserAuthoritiesController {
}
