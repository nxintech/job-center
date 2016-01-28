package com.nxin.framework.core;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by petzold on 2015/12/28.
 */
public class SpringJobFactory implements JobFactory
{
    private static AutowireCapableBeanFactory factory;
    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException
    {
        ElasticJob job = new ElasticJob();
        factory.autowireBeanProperties(job,AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,false);
        return job;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        factory = applicationContext.getAutowireCapableBeanFactory();
    }
}
