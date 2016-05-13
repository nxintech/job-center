package com.nxin.framework.core;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.nxin.framework.domain.ConnState;
import com.nxin.framework.domain.JobConfiguration;
import com.nxin.framework.domain.JobInstanceItem;
import com.nxin.framework.domain.Tuple2;
import com.nxin.framework.functions.Action3;
import com.nxin.framework.manager.IJobRepository;
import com.nxin.framework.message.JobMessage;
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
public class JobController extends AbstractIdleService implements IStateListener<ConnState>, ApplicationContextAware
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
        List<Tuple2<String,Integer>> servers = getOtherServers();
        logger.info("发送新建任务消息[{}]给其它机器[{}]", JSON.toJSONString(message), JSON.toJSONString(servers));
        rpcHelper.sendAll(servers, message);
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
        List<Tuple2<String,Integer>> servers = getOtherServers();
        logger.info("发送更新任务配置消息[{}]给其它机器[{}]", JSON.toJSONString(message), JSON.toJSONString(servers));
        rpcHelper.sendAll(servers, message);
        jobManager.updateJob(configuration);
    }

    public void deleteJob(String name)
    {
        jobRepository.deleteJobByName(name);
        JobMessage message = new JobMessage();
        message.setType(4);
        message.setMessageType(String.class.getName());
        message.setMessage(name);
        List<Tuple2<String,Integer>> servers = getOtherServers();
        logger.info("发送删除任务消息[{}]给其它机器[{}]", JSON.toJSONString(message), JSON.toJSONString(servers));
        rpcHelper.sendAll(servers, message);
        jobManager.deleteJob(name);
    }

    public void deleteJobs(List<String> ids)
    {
        List<JobConfiguration> configurations = jobRepository.getJobByIdList(ids);
        List<String> names = Lists.transform(configurations,JobConfiguration::getName);
        jobRepository.deleteJobs(ids);
        JobMessage message = new JobMessage();
        message.setType(5);
        message.setMessageType(ids.getClass().getName());
        message.setMessage(names);
        List<Tuple2<String,Integer>> servers = getOtherServers();
        logger.info("发送删除任务消息[{}]给其它机器[{}]", JSON.toJSONString(message), JSON.toJSONString(servers));
        rpcHelper.sendAll(servers, message);
        jobManager.deleteJobs(names);
    }

    public void deleteJobById(String id)
    {
        JobConfiguration configuration = jobRepository.getJob(id);
        deleteJob(configuration.getName());
    }

    private List<Tuple2<String,Integer>> getOtherServers()
    {
        List<Tuple2<String,Integer>> svs = serviceRegister.findJobServers();
        return ListAdapter.adapt(svs).rejectWith((tup, i) -> tup.getT1().equals(i), ip);
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
    public void onStateChanged(ConnState state)
    {
        if(state == ConnState.LOST)
        {
            jobManager.emptyJobs();
        }
        else if(state == ConnState.RECONNECTED)
        {
            jobManager.initJobs(jobRepository.getAllJobs());
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
