package com.bibsmobile.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventMap;
import com.bibsmobile.model.UploadFile;
import com.bibsmobile.model.dto.EventMapDto;

import flexjson.JSONSerializer;

@RequestMapping("/eventmaps")
@Controller
public class EventMapController {
	
	// BEGIN AWS bucket management stuff
	
    private AWSCredentials awsS3Credentials;
    @Value("${amazon.aws.s3.access-key}")
    private String awsS3AccessKey;
    @Value("${amazon.aws.s3.secret-key}")
    private String awsS3SecretKey;
    @Value("${amazon.aws.s3.bucket}")
    private String awsS3Bucket;
    @Value("${amazon.aws.s3.url-prefix}")
    private String awsS3UrlPrefix;
    
    public String getAwsS3UrlPrefix(){
    	return awsS3UrlPrefix;
    }

    @PostConstruct
    public void init() {
        this.awsS3Credentials = new BasicAWSCredentials(this.awsS3AccessKey, this.awsS3SecretKey);
    }

    // END AWS Bucket management stuff
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@ModelAttribute("uploadFile") MultipartFile map) throws IOException {
        Map<String, Object> fileInfo = new HashMap<>();
        StringBuilder fileName = new StringBuilder();
        System.out.println("Recieved File");
        System.out.println(map);
        fileName.append(((Long) System.currentTimeMillis())).append(".").append(FilenameUtils.getExtension(map.getOriginalFilename()));
        String fileNameStr = fileName.toString();
        if (map != null && map.getSize() > 0) {
            this.uploadFileToS3(map, fileNameStr);
            fileInfo.put("fileOriginalName", map.getOriginalFilename());
            fileInfo.put("fileType", map.getContentType());
            fileInfo.put("fileSize", map.getSize());
            fileInfo.put("s3FileName", fileNameStr);
            fileInfo.put("s3Url", this.awsS3UrlPrefix + fileNameStr);
        }
        return new JSONSerializer().serialize(fileInfo);
    }
    
    private PutObjectResult uploadFileToS3(MultipartFile file, String fileName) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(this.awsS3Credentials);
        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, stream, new ObjectMetadata());
        return s3Client.putObject(putObjectRequest);
    }
	

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventMap eventMap = new EventMap();
        Event event = Event.findEvent(eventId);
        eventMap.setEvent(event);
        List<Event> events = new ArrayList<>();
        events.add(event);
        uiModel.addAttribute("events", events);
        this.populateEditForm(uiModel, eventMap);
        return "eventmaps/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventMap eventMap = EventMap.findEventMap(id);
        List<Event> events = new ArrayList<>();
        events.add(eventMap.getEvent());
        uiModel.addAttribute("events", events);
        this.populateEditForm(uiModel, eventMap);
        return "eventmaps/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventmaps", EventMap.findEventMapsByEvent(event).getResultList());
        return "eventmaps/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        EventMap eventMap = EventMap.findEventMap(id);
        eventMap.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventmaps?event=" + eventMap.getEvent().getId();
    }

    public void populateEditForm(Model uiModel, EventMap eventMap) {
        uiModel.addAttribute("eventMap", eventMap);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> findEventsMaps(@RequestParam("event") Long eventId) {
        return new ResponseEntity<>(EventMapDto.fromEventsToDtoArray(EventMap.findEventMapsByEventId(eventId).getResultList()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventMap eventMap, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventMap);
            return "eventmaps/create";
        }
        uiModel.asMap().clear();
        eventMap.persist();
        return "redirect:/eventmaps/" + this.encodeUrlPathSegment(eventMap.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("eventmap", EventMap.findEventMap(id));
        uiModel.addAttribute("itemId", id);
        return "eventmaps/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventMap eventMap, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, eventMap);
            return "eventmaps/update";
        }
        uiModel.asMap().clear();
        eventMap.merge();
        return "redirect:/eventmaps/" + this.encodeUrlPathSegment(eventMap.getId().toString(), httpServletRequest);
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        EventMap eventMap = EventMap.findEventMap(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventMap == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventMap.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventMap> result = EventMap.findAllEventMaps();
        return new ResponseEntity<>(EventMap.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        EventMap eventMap = EventMap.fromJsonToEventMap(json);
        eventMap.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = this.getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + eventMap.getId()).build().toUriString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (EventMap eventMap : EventMap.fromJsonArrayToEventMaps(json)) {
            eventMap.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventMap eventMap = EventMap.fromJsonToEventMap(json);
        eventMap.setId(id);
        if (eventMap.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        EventMap eventMap = EventMap.findEventMap(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventMap == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        eventMap.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventMapsByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(EventMapDto.fromEventsToDtoArray(EventMap.findEventMapsByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
}
