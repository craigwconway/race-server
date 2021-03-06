/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Badge;
import com.bibsmobile.model.RaceResult;
import com.bibsmobile.model.Series;
import com.bibsmobile.model.SeriesRegion;
import com.bibsmobile.util.SpringJSONUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Controls for creating badges to be given out in the series.
 * @author galen
 *
 */
@RequestMapping("/series")
@Controller
public class SeriesController {
	@RequestMapping(value = "", produces = "text/html")
	public String list(Model uiModel) {
		List <Series> seriesList = Series.findAllSeries();
		Map <Long, Long> eventCounts = new HashMap <Long, Long>();
		for(Series series : seriesList) {
			eventCounts.put(series.getId(), Event.countFindEventsBySeriesEquals(series));
		}
		uiModel.addAttribute("series", Series.findAllSeries());
		uiModel.addAttribute("eventCounts", eventCounts);
		return "series/list";
	}
	
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String createForm(Model uiModel, @PathVariable("id") Long id,
			@RequestParam(value = "region", required = false) Long regionId) {
		Series series = Series.findSeries(id);

		uiModel.addAttribute("series", series);
		uiModel.addAttribute("regions", SeriesRegion.findSeriesRegionsBySeries(series).getResultList());
		Map <Long, Long> athleteCounts = new HashMap<Long,Long>();

		if(regionId != null) {
			SeriesRegion region = SeriesRegion.findSeries(regionId);
			uiModel.addAttribute("region", region);
			List <Event> events = Event.findEventsByRegionEquals(region).getResultList();
			uiModel.addAttribute("events", events);
			for(Event event : events) {
				athleteCounts.put(event.getId(), RaceResult.countFindRaceResultsByEvent(event));
			}
			uiModel.addAttribute("athleteCounts", athleteCounts);
		} else {
			List <Event> events = Event.findEventsBySeriesEquals(series).getResultList();
			uiModel.addAttribute("events", events);
			for(Event event : events) {
				athleteCounts.put(event.getId(), RaceResult.countFindRaceResultsByEvent(event));
			}
			uiModel.addAttribute("athleteCounts", athleteCounts);
			uiModel.addAttribute("region", null);
		}
		return "series/show";
	}
	
	/**
	 * @api /series/:id/regions Series Regions
	 * @apiName Get Series Regions
	 * @apiDescription Get Regions by Series
	 * @apiGroup Series
	 * @apiSuccess (200) {Object[]} region
	 * @apiSuccess (200) {String} region.name
	 */
	@RequestMapping(value = "/{id}/regions", produces = "application/json")
	public ResponseEntity<String> getRegionsBySeries(
			@PathVariable("id") Long id) {
		Series series = Series.findSeries(id);
		List<SeriesRegion> regions = SeriesRegion.findSeriesRegionsBySeries(series).getResultList();
		return SpringJSONUtil.returnObject(regions, HttpStatus.OK);
	}
}
