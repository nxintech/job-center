package com.nxin.framework.client;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.framework.core.IServiceRegister;
import com.nxin.framework.core.RpcHelper;
import com.nxin.framework.message.JobMessage;
import com.nxin.framework.message.JobRequest;
import com.nxin.framework.message.JobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by petzold on 2016/1/20.
 */
public class JobWorker extends AbstractIdleService
{
    private Map<String,IJob> handlerMap;
    private String ip;
    private IServiceRegister serviceRegister;
    private RpcHelper rpcHelper;
    private int port = 7732;
    private ExecutorService executor;
    private Logger logger = LoggerFactory.getLogger(JobWorker.class);

    protected void startUp() throws Exception
    {
        logger.info("初始化获取本机IP");
        InetAddress address = InetAddress.getLocalHost();
        this.ip = address.getHostAddress();
        logger.info("本机IP为【{}】",ip);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        serviceRegister.registerJobWorkers(handlerMap.keySet(), ip, port);
    }

    public void pushJob(JobRequest msg)
    {
        executor.submit(new Runnable1<JobRequest>(msg)
        {
            @Override
            void run(JobRequest request)
            {
                IJob job = handlerMap.get(request.getJobName());
                JobResponse response = job.exec(request);
                JobMessage message = new JobMessage();
                message.setType(1);
                message.setMessageType(JobResponse.class.getName());
                message.setMessage(response);
                logger.info("任务处理完毕，结果:{}", JSON.toJSONString(message));
                rpcHelper.sendAny(serviceRegister.findJobServers(),message);
            }
        });
    }

    @Override
    protected void shutDown() throws Exception
    {
        executor.shutdown();
    }

    public void setHandlerMap(Map<String, IJob> handlerMap)
    {
        this.handlerMap = handlerMap;
    }

    public void setServiceRegister(IServiceRegister serviceRegister)
    {
        this.serviceRegister = serviceRegister;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setRpcHelper(RpcHelper rpcHelper)
    {
        this.rpcHelper = rpcHelper;
    }

    private abstract class Runnable1<T> implements Runnable
    {
        private T t;

        public Runnable1(T t)
        {
            this.t = t;
        }

        @Override
        public void run()
        {
            run(t);
        }

        abstract void run(T t);
    }
}
