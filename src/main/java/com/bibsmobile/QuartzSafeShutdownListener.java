package com.bibsmobile;

import java.util.TimeZone;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.bibsmobile.util.BuildTypeUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QuartzSafeShutdownListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	try {
    		// If this is a registration build, the JVM should always be set to UTC time.
    		if(BuildTypeUtil.usesRegistration()) {
    			System.out.println("Setting timezone to UTC time");
    			//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    			}
    		} catch (Exception e) {
    			System.out.println("Error setting timezone to UTC time");
    		}
    	System.out.println("timezone: " + TimeZone.getDefault().getID());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            // piece of shit quartz can't shutdown properly, unless we do this.
            // this prevents Tomcat from shutting down at all, creating memory leaks
            // and all other kinds of shitstorms.
            StdSchedulerFactory.getDefaultScheduler().shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
