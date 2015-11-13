/**
 * 
 */
package com.bibsmobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	@RequestMapping(value = "/list", produces = "text/html")
	public String createForm(Model uiModel) {
		List <Series> seriesList = Series.findAllSeries();
		Map <Long, Long> eventCounts = new HashMap <Long, Long>();
		for(Series series : seriesList) {
			eventCounts.put(series.getId(), Event.countFindEventsBySeriesEquals(series));
		}
		uiModel.addAttribute("series", Series.findAllSeries());
		uiModel.addAttribute("eventCounts", eventCounts);
		return "series/list";
	}
}
