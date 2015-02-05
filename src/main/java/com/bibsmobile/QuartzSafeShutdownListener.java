package com.bibsmobile;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class QuartzSafeShutdownListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
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
