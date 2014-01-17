package com.bibsmobile.controller;

import java.util.Date;
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
import com.bibsmobile.service.ThingMagicTimer;
import com.bibsmobile.service.Timer;

@RequestMapping("/timers")
@Controller
@RooWebJson(jsonObject = TimerConfig.class)
@RooWebScaffold(path = "timers", formBackingObject = TimerConfig.class)
public class TimerConfigController {
	
	private HashMap<TimerConfig,Timer> timers = new HashMap<TimerConfig,Timer>();

	private Timer getTimer(long id){
		TimerConfig timerConfig = TimerConfig.findTimerConfig(id);
    	Timer timer = null;
    	if(timers.containsKey(timerConfig)){
    		System.out.println("Found cached timer!");
    		timer = timers.get(timerConfig);
    	}else{
    		if(timerConfig.getType() == 0)
    			timer = new DummyTimer();
    		else if(timerConfig.getType() == 1)
    			timer = new ThingMagicTimer();
    		timers.put(timerConfig, timer);
    		System.out.println("Put cached timer!");
    	}
		timer.setTimerConfig(timerConfig);
    	return timer;
	}
	
	@RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String status(@PathVariable(value = "id") long id) {
		return getTimer(id).getStatus()+"";
	}
	
	@RequestMapping(value = "/connect/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String connect(@PathVariable(value = "id") long id){
    	getTimer(id).disconnect(); 
    	getTimer(id).connect();
        return "true";
    }
	
	@RequestMapping(value = "/disconnect/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String disconnect(@PathVariable(value = "id") long id){
    	getTimer(id).disconnect();
        return "true";
    }
    
    @RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String start(@PathVariable(value = "id") long id){
    	getTimer(id).startReader();
        return "true";
    }
    
    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String stop(@PathVariable(value = "id") long id){
    	getTimer(id).stopReader();
        return "true";
    }
    
    @RequestMapping(value = "/write/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String write(@PathVariable(value = "id") long id) throws Exception{
    	getTimer(1).write(id); // use timer 1 TODO ?
        return "true";
    } 

    @RequestMapping(value = "/time/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String time(@PathVariable(value = "id") long id) {
    	long timer = getTimer(id).getDateTime();
    	long server = new Date().getTime();
    	if(server - timer > 1000){ // 1 second
    		System.out.println("Timer/Server "+timer+"/"+server);
    		return "false";
    	}
        return "true";
    }
    
}
