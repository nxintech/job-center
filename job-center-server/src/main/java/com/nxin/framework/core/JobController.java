package com.nxin.framework.core;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.collection.ListAdapter;
import com.nxin.domain.Tuple2;
import com.nxin.framework.domain.JobConfiguration;
import com.nxin.framework.domain.JobInstanceItem;
import com.nxin.framework.manager.IJobRepository;
import com.nxin.framework.message.JobMessage;
import com.nxin.functions.Action3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by petzold on 2015/12/28.
 */
public class JobController extends AbstractIdleService implements ApplicationContextAware
{
    private JobManager jobManager;
    private IJobRepository jobRepository;
    private IServiceRegister serviceRegister;
    private RpcHelper rpcHelper;
    private String ip;
    private int port = 7732;
    private Logger logger = LoggerFactory.getLogger(JobController.class);

    protected void startUp() throws Exception
    {
        initIp();
        serviceRegister.registerJobServer(ip,port);
        serviceRegister.onReconnected(new Action3<Object,JobManager,IJobRepository>()
        {
            @Override
            public void call(Object o, JobManager manager,IJobRepository jobRepository)
            {
                manager.initJobs(jobRepository.getAllJobs());
            }
        }, jobManager, jobRepository);
    }

    public int newJob(JobConfiguration configuration)
    {
        if(jobRepository.jobNameExist(configuration.getName()))
        {
            return 1000001;
        }
        JobMessage message = new JobMessage();
        message.setType(2);
        message.setMessageType(JobConfiguration.class.getName());
        message.setMessage(configuration);
        for (Tuple2<String,Integer> tup : getOtherServers())
        {
            rpcHelper.send(tup.getT1(),tup.getT2(),message);
        }
        jobManager.registerJob(configuration);
        jobRepository.addJob(configuration);
        return 0;
    }

    public void runJob(String id)
    {
        JobConfiguration configuration = jobRepository.getJob(id);
        jobManager.runJob(configuration.getName());
    }

    public void runJobs(List<String> ids)
    {
        List<JobConfiguration> configurations = jobRepository.getJobByIdList(ids);
        jobManager.runJobs(Lists.transform(configurations,JobConfiguration::getName));
    }

    public void updateJob(JobConfiguration configuration)
    {
        jobRepository.updateJob(configuration);
        JobMessage message = new JobMessage();
        message.setType(3);
        message.setMessageType(JobConfiguration.class.getName());
        message.setMessage(configuration);
        for (Tuple2<String,Integer> tup : getOtherServers())
        {
            rpcHelper.send(tup.getT1(),tup.getT2(),message);
        }
        jobManager.updateJob(configuration);
    }

    public void deleteJob(String name)
    {
        jobRepository.deleteJobByName(name);
        JobMessage message = new JobMessage();
        message.setType(4);
        message.setMessageType(String.class.getName());
        message.setMessage(name);
        for (Tuple2<String,Integer> tup : getOtherServers())
        {
            rpcHelper.send(tup.getT1(),tup.getT2(),message);
        }
        jobManager.deleteJob(name);
    }

    public void deleteJobs(List<String> ids)
    {
        List<JobConfiguration> configurations = jobRepository.getJobByIdList(ids);
        List<String> names = ListAdapter.transform(JobConfiguration::getName ,configurations);
        jobRepository.deleteJobs(ids);
        JobMessage message = new JobMessage();
        message.setType(5);
        message.setMessageType(ids.getClass().getName());
        message.setMessage(names);
        for (Tuple2<String,Integer> tup : getOtherServers())
        {
            rpcHelper.send(tup.getT1(),tup.getT2(),message);
        }
        jobManager.deleteJobs(names);
    }

    public void deleteJobById(String id)
    {
        JobConfiguration configuration = jobRepository.getJob(id);
        deleteJob(configuration.getName());
    }
    private List<Tuple2<String,Integer>> getOtherServers()
    {
        return ListAdapter.rejectWith((Tuple2<String, Integer> tup,String ip) -> tup.getT1().equals(ip) ,serviceRegister.findJobServers(),ip);
    }
    public void newJobInstance(String jobId, List<JobInstanceItem> items)
    {
        jobRepository.newInstance(jobId, items);
    }

    private void initIp()
    {
        try
        {
            logger.info("初始化获取本机IP");
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            logger.info("本机IP为【{}】",ip);
            this.ip = ip;
        }
        catch (UnknownHostException e)
        {
            logger.error("初始化本机IP失败",e);
        }
    }
    @Override
    protected void shutDown() throws Exception
    {
        logger.info("任务控制器销毁");
    }

    public void setJobManager(JobManager jobManager)
    {
        this.jobManager = jobManager;
    }

    public void setJobRepository(IJobRepository jobRepository)
    {
        this.jobRepository = jobRepository;
    }

    public void setServiceRegister(IServiceRegister serviceRegister)
    {
        this.serviceRegister = serviceRegister;
    }

    public void setRpcHelper(RpcHelper rpcHelper)
    {
        this.rpcHelper = rpcHelper;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringJobFactory.setApplicationContext(applicationContext);
    }
}
