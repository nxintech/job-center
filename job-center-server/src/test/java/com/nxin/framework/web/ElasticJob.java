package com.nxin.framework.web;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by petzold on 2015/12/21.
 */
public class ElasticJob implements Job
{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        System.out.println("asd0="+jobDataMap.getString("asd0"));
        System.out.println("dsds="+jobDataMap.getInt("dsds"));
    }
}
