package com.nxin.framework.web;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.security.auth.login.CredentialException;

/**
 * Created by petzold on 2015/12/21.
 */
public class QuartzTest
{
    private SchedulerFactory factory;
    @Before
    public void init()
    {
        factory = new StdSchedulerFactory();
    }
    @Test
    public void test1() throws SchedulerException, InterruptedException
    {
        String name ="adobe";
        JobDetail detail = JobBuilder.newJob(ElasticJob.class).withIdentity(name).usingJobData("asd0", "dsd").usingJobData("dsds", 123).withDescription("ddsaa").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
        Scheduler scheduler = factory.getScheduler();
        scheduler.scheduleJob(detail, trigger);
        scheduler.start();
        JobDetail jobDetail = scheduler.getJobDetail(new JobKey(name));
        Assert.assertEquals(jobDetail,detail);
        Trigger trigger1 = scheduler.getTrigger(new TriggerKey(name));
        Assert.assertEquals(trigger,trigger1);
        name = "microsoft";
        JobDetail detail2 = JobBuilder.newJob(ElasticJob.class).withIdentity(name).usingJobData("asd0", "dsfwww").usingJobData("dsds", 345).withDescription("ddsaa").build();
        CronTrigger trigger2 = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(CronScheduleBuilder.cronSchedule("0/8 * * * * ?")).build();
        scheduler.scheduleJob(detail2,trigger2);
        scheduler.start();
        Thread.sleep(100000000L);
    }
    @Test
    public void test2()
    {
        boolean bl = CronExpression.isValidExpression("asasa");
        Assert.assertFalse(bl);
        Assert.assertTrue(CronExpression.isValidExpression("0/5 * * * * ?"));
    }
}
