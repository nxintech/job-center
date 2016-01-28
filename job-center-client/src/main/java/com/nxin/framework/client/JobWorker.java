package com.nxin.framework.client;

import com.google.common.util.concurrent.AbstractIdleService;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.nxin.domain.Tuple5;
import com.nxin.framework.core.IServiceRegister;
import com.nxin.framework.core.RpcHelper;
import com.nxin.framework.message.JobMessage;
import com.nxin.framework.message.JobRequest;
import com.nxin.framework.message.JobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by petzold on 2016/1/20.
 */
public class JobWorker extends AbstractIdleService
{
    private Map<String,IJob> handlerMap;
    private RingBuffer<Tuple5<String,String,Boolean,List<Integer>,String>> ringBuffer;
    private ExecutorService executor;
    private WaitStrategy waitStrategy;
    private int bufferSize;
    private String ip;
    private Disruptor<Tuple5<String,String,Boolean,List<Integer>,String>> disruptor;
    private IServiceRegister serviceRegister;
    private RpcHelper rpcHelper;
    private int port = 7732;
    private Logger logger = LoggerFactory.getLogger(JobWorker.class);

    protected void startUp() throws Exception
    {
        logger.info("初始化获取本机IP");
        InetAddress address = InetAddress.getLocalHost();
        this.ip = address.getHostAddress();
        logger.info("本机IP为【{}】",ip);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        disruptor = new Disruptor<Tuple5<String,String,Boolean,List<Integer>,String>>(new EventFactory<Tuple5<String, String, Boolean, List<Integer>, String>>()
        {
            @Override
            public Tuple5<String, String, Boolean, List<Integer>, String> newInstance()
            {
                return new Tuple5<String,String,Boolean,List<Integer>,String>();
            }
        },bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE,waitStrategy);
        disruptor.handleEventsWith(new EventHandler<Tuple5<String,String,Boolean,List<Integer>,String>>()
        {
            @Override
            public void onEvent(Tuple5<String,String,Boolean,List<Integer>,String> request, long sequence, boolean endOfBatch) throws Exception
            {
                IJob job = handlerMap.get(request.getT2());
                JobResponse response = job.exec(new JobRequest(request.getT1(), request.getT2(), request.getT3(), request.getT4(), request.getT5()));
                JobMessage message = new JobMessage();
                message.setType(1);
                message.setMessageType(JobResponse.class.getName());
                message.setMessage(response);
                rpcHelper.sendAny(serviceRegister.findJobServers(),message);
            }
        });
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
        serviceRegister.registerJobWorkers(handlerMap.keySet(), ip, port);
    }

    public void pushJob(JobRequest msg)
    {
        ringBuffer.publishEvent(new EventTranslatorOneArg<Tuple5<String,String,Boolean,List<Integer>,String>, JobRequest>()
        {
            @Override
            public void translateTo(Tuple5<String,String,Boolean,List<Integer>,String> event, long sequence, JobRequest req)
            {
                event.setT1(req.getId());
                event.setT2(req.getJobName());
                event.setT3(req.isSharding());
                event.setT4(req.getShardingItems());
                event.setT5(req.getExtra());
            }
        },msg);
    }

    @Override
    protected void shutDown() throws Exception
    {
        executor.shutdown();
        disruptor.shutdown();
    }

    public void setHandlerMap(Map<String, IJob> handlerMap)
    {
        this.handlerMap = handlerMap;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    public void setWaitStrategy(WaitStrategy waitStrategy)
    {
        this.waitStrategy = waitStrategy;
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
}
