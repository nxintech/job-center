package com.nxin.framework.core;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by petzold on 2015/12/28.
 */
public class SpringJobFactory implements JobFactory
{
    private static AutowireCapableBeanFactory factory;
    private Logger logger = LoggerFactory.getLogger(SpringJobFactory.class);
    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException
    {
        try
        {
            Job job = bundle.getJobDetail().getJobClass().newInstance();
            factory.autowireBeanProperties(job,AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,false);
            return job;
        } catch (Exception e)
        {
            logger.error("实例化任务失败", e);
            return null;
        }
    }

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        factory = applicationContext.getAutowireCapableBeanFactory();
    }
}
