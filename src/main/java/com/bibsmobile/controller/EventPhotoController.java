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
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.EventPhoto;
import com.bibsmobile.model.UploadFile;

import flexjson.JSONSerializer;

@RequestMapping("/eventphotos")
@Controller
public class EventPhotoController {

    private static AWSCredentials awsS3Credentials;
    @Value("${amazon.aws.s3.access-key}")
    private String awsS3AccessKey;
    @Value("${amazon.aws.s3.secret-key}")
    private String awsS3SecretKey;
    @Value("${amazon.aws.s3.bucket}")
    private String awsS3Bucket;
    @Value("${amazon.aws.s3.url-prefix}")
    private String awsS3UrlPrefix;

    @PostConstruct
    public void init() {
        awsS3Credentials = new BasicAWSCredentials(this.awsS3AccessKey, this.awsS3SecretKey);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        EventPhoto eventPhoto = new EventPhoto();
        Event event = Event.findEvent(eventId);
        eventPhoto.setEvent(event);
        List<Event> events = new ArrayList<>();
        events.add(event);
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventPhoto);
        return "eventphotos/create";
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        EventPhoto eventPhoto = EventPhoto.findEventPhoto(id);
        List<Event> events = new ArrayList<>();
        events.add(eventPhoto.getEvent());
        uiModel.addAttribute("events", events);
        populateEditForm(uiModel, eventPhoto);
        return "eventphotos/update";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "event", required = true) Long eventId, Model uiModel) {
        Event event = Event.findEvent(eventId);
        uiModel.addAttribute("event", event);
        uiModel.addAttribute("eventphotos", EventPhoto.findEventPhotoesByEvent(event).getResultList());
        return "eventphotos/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        EventPhoto eventPhoto = EventPhoto.findEventPhoto(id);
        eventPhoto.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/eventphotos?event=" + eventPhoto.getEvent().getId();
    }

    public void populateEditForm(Model uiModel, EventPhoto eventPhoto) {
        uiModel.addAttribute("eventPhoto", eventPhoto);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String findEventsPhotos(@RequestParam(value = "event") Long eventId) {
        return EventPhoto.toJsonArray(EventPhoto.findEventPhotosByEventId(eventId).getResultList());
    }

    @RequestMapping(value = "/fileupload", method = RequestMethod.GET)
    public String getUploadForm(Model model) {
        model.addAttribute("uploadFile", new UploadFile());
        return "eventphotos/upload";
    }

    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@ModelAttribute("uploadFile") UploadFile uploadFile) throws IOException {
        MultipartFile file = uploadFile.getFile();
        Map<String, Object> fileInfo = new HashMap<>();
        StringBuilder fileName = new StringBuilder();
        fileName.append(((Long) System.currentTimeMillis()).toString()).append(".").append(FilenameUtils.getExtension(file.getOriginalFilename()));
        String fileNameStr = fileName.toString();
        if (file != null && file.getSize() > 0) {
            uploadFileToS3(file, fileNameStr);
            fileInfo.put("fileOriginalName", file.getOriginalFilename());
            fileInfo.put("fileType", file.getContentType());
            fileInfo.put("fileSize", file.getSize());
            fileInfo.put("s3FileName", fileNameStr);
            fileInfo.put("s3Url", this.awsS3UrlPrefix.concat(fileNameStr));
        }
        return new JSONSerializer().serialize(fileInfo);
    }

    @RequestMapping(value = "/filedelete", method = RequestMethod.DELETE)
    public ResponseEntity<String> fileDelete(@RequestParam(value = "key", required = true) String fileName) {
        deleteFileFromS3(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private PutObjectResult uploadFileToS3(MultipartFile file, String fileName) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(awsS3Credentials);
        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3Bucket, fileName, stream, new ObjectMetadata());
        return s3Client.putObject(putObjectRequest);
    }

    private void deleteFileFromS3(String fileName) {
        AmazonS3 s3Client = new AmazonS3Client(awsS3Credentials);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.awsS3Bucket, fileName);
        s3Client.deleteObject(deleteObjectRequest);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid EventPhoto eventPhoto, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventPhoto);
            return "eventphotos/create";
        }
        uiModel.asMap().clear();
        eventPhoto.persist();
        return "redirect:/eventphotos/" + encodeUrlPathSegment(eventPhoto.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("eventphoto", EventPhoto.findEventPhoto(id));
        uiModel.addAttribute("itemId", id);
        return "eventphotos/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid EventPhoto eventPhoto, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eventPhoto);
            return "eventphotos/update";
        }
        uiModel.asMap().clear();
        eventPhoto.merge();
        return "redirect:/eventphotos/" + encodeUrlPathSegment(eventPhoto.getId().toString(), httpServletRequest);
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
        EventPhoto eventPhoto = EventPhoto.findEventPhoto(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (eventPhoto == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(eventPhoto.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<EventPhoto> result = EventPhoto.findAllEventPhotoes();
        return new ResponseEntity<String>(EventPhoto.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        EventPhoto eventPhoto = EventPhoto.fromJsonToEventPhoto(json);
        eventPhoto.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + eventPhoto.getId().toString()).build().toUriString());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (EventPhoto eventPhoto : EventPhoto.fromJsonArrayToEventPhotoes(json)) {
            eventPhoto.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        EventPhoto eventPhoto = EventPhoto.fromJsonToEventPhoto(json);
        eventPhoto.setId(id);
        if (eventPhoto.merge() == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        EventPhoto eventPhoto = EventPhoto.findEventPhoto(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (eventPhoto == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        eventPhoto.remove();
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindEventPhotoesByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<String>(EventPhoto.toJsonArray(EventPhoto.findEventPhotoesByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
}
