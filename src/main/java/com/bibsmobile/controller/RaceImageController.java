package com.bibsmobile.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.PictureHashtag;
import com.bibsmobile.model.RaceImage;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.dto.RaceImageDetailsDto;
import com.bibsmobile.model.dto.RaceImageViewDto;
import com.bibsmobile.service.UserProfileService;

@RequestMapping("/raceimages")
@Controller
public class RaceImageController {

	/**
	 * @api {get} /raceimages/api API
	 * @apiGroup raceimages
	 * @apiName API
	 * @apiParam {Number} raceId Id of event tagged in race image
	 * @apiParam {String} filePath Location of image in online datastore
	 * @apiParam {Number[]} [bib] Bib Numbers to tag in image
	 * @apiParam {String[]} [hashtag] Hashtags to add to this image
	 * @apiParamExample Sample-Image-Upload:
	 * 	https://bibslabs.co/bibs-server/raceimages/api?filePath=http://bibstest.s3.amazonaws.com/path/1420874330.png&raceId=1&bib=777&bib=1&bib=2&bib=3&hashtag=face&hashtag=punch 
	 * @apiSuccess (201) No Response
	 * @apiPermission none
	 */
    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public ResponseEntity<String> api(@RequestParam("filePath") String filePath, @RequestParam("raceId") long raceId,
            @RequestParam(value = "bib", required = false) List<Long> bib, @RequestParam(value = "hashtag", required = false) List<String> hashtags) {
        RaceImage raceImage = new RaceImage(filePath, raceId, bib, hashtags);
//        if (CollectionUtils.isEmpty(bib)) {
//        	System.out.println("no bib found");
//            raceImage.persist();
//        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * @api {get} /raceimages/search Search
     * @apiGroup raceimages
     * @apiName Search
     * @apiParam {Number} eventId Id of event to search
     * @apiParam {Number} [bib] bib number of athlete to search
     * @apiParam {Number} [page=1] Page number for result pagination
     * @apiParam {Number} [size=20] Page size for result pagination
     * @apiSuccess (200) {Object[]} raceImage objects returned
     * @apiPermission none
     */
    @RequestMapping(value = "/search", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceImagesByEventId(@RequestParam Long eventId, @RequestParam(required = false) Long bib, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        Event event = Event.findEvent(eventId);
        if (event != null) {
            List<RaceImage> raceImages = null;
            if (bib != null) {
                List<RaceResult> raceResults = RaceResult.findRaceResultsByEventAndBibEquals(event, bib).getResultList();
                if (CollectionUtils.isNotEmpty(raceResults)) {
                    raceImages = RaceImage.findRaceImagesByRaceResults(raceResults).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
                }
            } else {
                raceImages = RaceImage.findRaceImagesByEvent(event).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
            }
            if (CollectionUtils.isNotEmpty(raceImages)) {
                return new ResponseEntity<>(RaceImageViewDto.fromRaceImagesToDtoArray(raceImages), headers, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * @api {get} /raceimages/count Count
     * @apiGroup raceimages
     * @apiDescription Count Race Images in a search query
     * @apiName Count
     * @apiParam {Number} eventId Id of event to search
     * @apiParam {Number} [bib] bib number of athlete to search
     * @apiSuccess (200) {Object[]} raceImage objects returned
     * @apiPermission none
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Long> countRaceImagesByEventId(@RequestParam Long eventId, @RequestParam(required = false) Long bib) {
        HttpHeaders headers = new HttpHeaders();
        Event event = Event.findEvent(eventId);
        if (event != null) {
            return new ResponseEntity<>(RaceImage.countFindRaceImagesByEvent(event), HttpStatus.OK);
            
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * @api {get} /raceimages/details/:id Details
     * @apiName Details
     * @apigroup raceimages
     * @apiDescription Get details for a single race image. This pulls information about athletes in the image
     * and hashtags on the image.
     * @apiUse raceImageDetailsDto
     * @return
     */
    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        RaceImage raceImage = RaceImage.findRaceImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (raceImage == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(RaceImageDetailsDto.fromRaceImageToDto(raceImage), headers, HttpStatus.OK);
    }    
    @Autowired
    UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid RaceImage raceImage, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, raceImage);
            return "raceimages/create";
        }
        uiModel.asMap().clear();
        raceImage.persist();
        return "redirect:/raceimages/" + this.encodeUrlPathSegment(raceImage.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        this.populateEditForm(uiModel, new RaceImage());
        return "raceimages/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("raceimage", RaceImage.findRaceImage(id));
        uiModel.addAttribute("itemId", id);
        return "raceimages/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("raceimages", RaceImage.findRaceImageEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) RaceImage.countRaceImages() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("raceimages", RaceImage.findAllRaceImages(sortFieldName, sortOrder));
        }
        return "raceimages/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid RaceImage raceImage, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            this.populateEditForm(uiModel, raceImage);
            return "raceimages/update";
        }
        uiModel.asMap().clear();
        raceImage.merge();
        return "redirect:/raceimages/" + this.encodeUrlPathSegment(raceImage.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        this.populateEditForm(uiModel, RaceImage.findRaceImage(id));
        return "raceimages/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        RaceImage raceImage = RaceImage.findRaceImage(id);
        raceImage.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/raceimages";
    }

    void populateEditForm(Model uiModel, RaceImage raceImage) {
        uiModel.addAttribute("raceImage", raceImage);
        uiModel.addAttribute("events", Event.findAllEvents());
        uiModel.addAttribute("picturetypes", PictureHashtag.findAllPictureHashtags());
        uiModel.addAttribute("raceresults", RaceResult.findAllRaceResults());
        uiModel.addAttribute("userprofiles", this.userProfileService.findAllUserProfiles());
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


    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<RaceImage> result = RaceImage.findAllRaceImages();
        return new ResponseEntity<>(RaceImage.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json, UriComponentsBuilder uriBuilder) {
        RaceImage raceImage = RaceImage.fromJsonToRaceImage(json);
        raceImage.persist();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RequestMapping a = this.getClass().getAnnotation(RequestMapping.class);
        headers.add("Location", uriBuilder.path(a.value()[0] + "/" + raceImage.getId()).build().toUriString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (RaceImage raceImage : RaceImage.fromJsonArrayToRaceImages(json)) {
            raceImage.persist();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        RaceImage raceImage = RaceImage.fromJsonToRaceImage(json);
        raceImage.setId(id);
        if (raceImage.merge() == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        RaceImage raceImage = RaceImage.findRaceImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (raceImage == null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
        raceImage.remove();
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(params = "find=ByEvent", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> jsonFindRaceImagesByEvent(@RequestParam("event") Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(RaceImage.toJsonArray(RaceImage.findRaceImagesByEvent(event).getResultList()), headers, HttpStatus.OK);
    }
}
