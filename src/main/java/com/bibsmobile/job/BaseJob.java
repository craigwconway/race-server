package com.bibsmobile.job;

import java.util.Random;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public abstract class BaseJob implements Job {
    private static Scheduler scheduler;

    static {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private static JobDetail registerJob(Class<? extends Job> jobClass) throws SchedulerException {
        JobKey jobKey = new JobKey(jobClass.getSimpleName() + "Job");
        if (scheduler.checkExists(jobKey))
            return scheduler.getJobDetail(jobKey);

        JobDetail job = JobBuilder.newJob(jobClass)
            .withIdentity(jobKey)
            .storeDurably()
            .build();
        scheduler.addJob(job, false);
        return job;
    }

    public static void scheduleNow(Class<? extends Job> jobClass) throws SchedulerException {
        scheduler.triggerJob(registerJob(jobClass).getKey());
    }

    public static void scheduleOnceInSeconds(Class<? extends Job> jobClass, int seconds) throws SchedulerException {
        String triggerName = jobClass.getSimpleName() + "Trigger" + new Random().nextInt();
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerName)
            .startAt(DateBuilder.futureDate(seconds, DateBuilder.IntervalUnit.SECOND))
            .forJob(registerJob(jobClass))
            .build();
        scheduler.scheduleJob(trigger);
    }
}
