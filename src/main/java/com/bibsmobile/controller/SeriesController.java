/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bibsmobile.model.Event;
import com.bibsmobile.model.Badge;
import com.bibsmobile.model.Series;

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
	
	@RequestMapping(value = "/show/{id}", produces = "text/html")
	public String createForm(Model uiModel, @PathVariable("id") Long id) {
		Series series = Series.findSeries(id);
		uiModel.addAttribute("series", series);
		return "series/show";
	}
}
