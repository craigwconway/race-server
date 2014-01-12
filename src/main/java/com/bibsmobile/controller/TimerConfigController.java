package com.bibsmobile.controller;

import java.util.HashMap;

import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bibsmobile.model.TimerConfig;
import com.bibsmobile.service.DummyTimer;
import com.bibsmobile.service.Timer;

@RequestMapping("/timers")
@Controller
@RooWebJson(jsonObject = TimerConfig.class)
@RooWebScaffold(path = "timers", formBackingObject = TimerConfig.class)
public class TimerConfigController {
	
	private HashMap<Long,Timer> timers = new HashMap<Long,Timer>();

	private Timer getTimer(long id){
		TimerConfig timerConfig = TimerConfig.findTimerConfig(id);
    	Timer timer;
    	if(timers.containsKey(id)){
    		timer = timers.get(id);
    	}else{
    		timer = new DummyTimer();
    		timer.setTimerConfig(timerConfig);
    		timers.put(id, timer);
    	}
    	return timer;
	}
	
	@RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String status(@PathVariable(value = "id") long id) {
		return String.valueOf(getTimer(id).getStatus());
	}
	
	@RequestMapping(value = "/connect/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String connect(@PathVariable(value = "id") long id){
    	getTimer(id).connect();
        return "true";
    }
    
    @RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String start(@PathVariable(value = "id") long id){
    	getTimer(id).start();
        return "true";
    }
    
    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String stop(@PathVariable(value = "id") long id){
    	getTimer(id).stop();
        return "true";
    }
    
    @RequestMapping(value = "/write/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String write(@PathVariable(value = "id") long id) throws Exception{
    	getTimer(id).write(id);
        return "true";
    }
}
