package com.nxin.framework.core;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.framework.domain.JobConfiguration;
import com.nxin.framework.manager.IJobRepository;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by petzold on 2015/12/21.
 */
public class JobManager extends AbstractIdleService
{
    private Scheduler scheduler;
    private IJobRepository jobRepository;
    private Logger logger = LoggerFactory.getLogger(JobManager.class);

    @Override
    protected void startUp() throws Exception
    {
        syncTime();
        logger.info("服务启动,初始化任务参数");
        Properties properties = new Properties();
        properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.put("org.quartz.threadPool.threadCount","3");
        properties.put("org.quartz.scheduler.jobFactory.class", SpringJobFactory.class.getName());
        properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        StdSchedulerFactory factory = new StdSchedulerFactory(properties);
        scheduler = factory.getScheduler();
        scheduler.start();
        logger.info("服务启动成功");
        initJobs(jobRepository.getAllJobs());
    }

    public void initJobs(List<JobConfiguration> configurations)
    {
        logger.info("重新加载任务配置");
        try
        {
            scheduler.clear();
        } catch (SchedulerException e)
        {
            logger.error("清理全部任务失败",e);
        }
        for (JobConfiguration configuration : configurations)
        {
            registerJob(configuration);
        }
    }
    public void registerJob(JobConfiguration configuration)
    {
        try
        {
            JobBuilder builder = JobBuilder.newJob(ElasticJob.class).storeDurably().withIdentity(configuration.getName()).usingJobData("id",configuration.getId()).usingJobData("name",configuration.getName());
            JobDetail jobDetail = initBuilder(builder, configuration).build();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(configuration.getName()).withSchedule(CronScheduleBuilder.cronSchedule(configuration.getExpression())).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e)
        {
            logger.error(String.format("注册任务%s失败",configuration.getName()),e);
        }
    }

    public void updateJob(JobConfiguration configuration)
    {
        try
        {
            JobDetail detail = scheduler.getJobDetail(new JobKey(configuration.getName()));
            initBuilder(detail.getJobBuilder(),configuration).build();
            TriggerKey triggerKey = new TriggerKey(configuration.getName());
            TriggerBuilder tb = scheduler.getTrigger(triggerKey).getTriggerBuilder();
            Trigger trigger = tb.withSchedule(CronScheduleBuilder.cronSchedule(configuration.getExpression())).build();
            scheduler.rescheduleJob(triggerKey,trigger);
            scheduler.addJob(detail, true);
        }
        catch (SchedulerException e)
        {
            logger.error("更新任务配置失败",e);
        }
    }

    private JobBuilder initBuilder(JobBuilder builder, JobConfiguration configuration)
    {
        builder.usingJobData("expression",configuration.getExpression()).usingJobData("needSharding",configuration.isNeedSharding());
        if(configuration.isNeedSharding())
        {
            builder.usingJobData("shardingTotal",configuration.getShardingTotal());
        }
        if(!Strings.isNullOrEmpty(configuration.getDescription()))
        {
            builder.usingJobData("description",configuration.getDescription()).withDescription(configuration.getDescription());
        }
        builder.usingJobData("consumerType", configuration.getConsumerType());
        if(configuration.getConsumerType() == ConsumerType.HTTP.ordinal())
        {
            builder.usingJobData("callbackUrl",configuration.getCallbackUrl());
        }
        if(!Strings.isNullOrEmpty(configuration.getExtra()))
        {
            builder.usingJobData("extra", configuration.getExtra());
        }
        return builder;
    }

    public void deleteJob(String name)
    {
        try
        {
            JobKey key = new JobKey(name);
            if(scheduler.checkExists(key))
            {
                scheduler.deleteJob(key);
            }
        } catch (SchedulerException e)
        {
            logger.error("删除任务失败",e);
        }
    }

    public void deleteJobs(List<String> names)
    {
        try
        {
            scheduler.deleteJobs(Lists.transform(names, new Function<String, JobKey>()
            {
                @Override
                public JobKey apply(String input)
                {
                    return new JobKey(input);
                }
            }));
        }
        catch (SchedulerException e)
        {
            logger.error("批量删除任务失败",e);
        }
    }

    public void runJob(String name)
    {
        try
        {
            JobKey key = new JobKey(name);
            if(scheduler.checkExists(key))
            {
                scheduler.triggerJob(new JobKey(name));
            }
            else
            {
                JobConfiguration configuration = jobRepository.getJobByName(name);
                registerJob(configuration);
                scheduler.triggerJob(key);
            }
        } catch (SchedulerException e)
        {
            logger.error(String.format("执行任务[%s]失败",name),e);
        }
    }

    public void runJobs(List<String> names)
    {
        for (String name : names)
        {
            runJob(name);
        }
    }

    private void syncTime()
    {
        try
        {
            String osName = System.getProperty("os.name");
            String cmd = osName.matches("^(?i)Windows.*$") ? "w32tm /resync" : "/usr/sbin/ntpdate -u cn.pool.ntp.org";
            logger.info("开始同步系统时间");
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            logger.info("同步系统时间成功");
        }
        catch (Exception e)
        {
            logger.error("同步系统时间失败",e);
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        logger.info("服务关闭");
        scheduler.shutdown(true);
    }

    public void setJobRepository(IJobRepository jobRepository)
    {
        this.jobRepository = jobRepository;
    }
}
