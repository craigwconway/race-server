package com.bibsmobile.controller;
import com.bibsmobile.model.EventPhoto;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

@RequestMapping("/eventphotos")
@Controller
@RooWebScaffold(path = "eventphotos", formBackingObject = EventPhoto.class)
@RooWebJson(jsonObject = EventPhoto.class)
public class EventPhotoController {

}
