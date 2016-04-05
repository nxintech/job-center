package com.nxin.framework.core;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.nxin.framework.domain.JobInstanceItem;
import com.nxin.framework.domain.Tuple2;
import com.nxin.framework.message.JobRequest;
import com.nxin.framework.sharing.IJobShardingStrategy;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Created by petzold on 2015/12/21.
 */
public class ElasticJob implements Job
{
    private RequestHelper requestHelper;
    private IJobShardingStrategy jobShardingStrategy;
    private IServiceRegister serviceRegister;
    private JobController jobController;
    private RpcHelper rpcHelper;
    private RedisManager redisManager;
    private Random random = new Random();
    private Logger logger = LoggerFactory.getLogger(ElasticJob.class);
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String name = dataMap.getString("name");
        boolean bl = redisManager.runScript(s -> s.toString().equals("1"),"setNxWithExpire",Lists.newArrayList(name),Lists.newArrayList("0","2"));
        if(bl)
        {
            boolean needSharing = dataMap.getBoolean("needSharding");
            int consumerType = dataMap.getInt("consumerType");
            String id = dataMap.getString("id");
            String extra = dataMap.containsKey("extra") ? dataMap.getString("extra") : null;
            List<Tuple2<String,Integer>> workers = serviceRegister.findJobWorkers(name);
            if(workers == null || workers.isEmpty())
            {
                logger.error("没有发现执行任务【{}】的机器", name);
                return;
            }
            if(needSharing)
            {
                int shardingTotal = dataMap.getInt("shardingTotal");
                Map<String,List<Integer>> map = jobShardingStrategy.sharding(ListAdapter.adapt(workers).collect(tup -> tup.getT1()), shardingTotal);
                List<JobInstanceItem> items = new ArrayList<JobInstanceItem>(map.size());
                boolean isCallback = consumerType != ConsumerType.TCP.ordinal();
                String callback = dataMap.getString("callbackUrl");
                for (Map.Entry<String,List<Integer>> entry : map.entrySet())
                {
                    Tuple2<String, Integer> tup = ListAdapter.adapt(workers).detectWith((tp,ip)->tp.getT1().equals(ip),entry.getKey());
                    JobInstanceItem item = new JobInstanceItem();
                    item.setId(UUID.randomUUID().toString());
                    item.setShardingItems(Joiner.on(",").join(entry.getValue()));
                    items.add(item);
                    if(isCallback)
                    {
                        String url = (consumerType == ConsumerType.HTTP.ordinal() ? "http" : "https") + "://" + tup.getT1() + (tup.getT2() == 80 ? "" : ":" + tup.getT2()) + "/" + callback;
                        Map<String,String> parameter = new HashMap<String, String>();
                        parameter.put("id", item.getId());
                        parameter.put("name", name);
                        parameter.put("sharding", "1");
                        parameter.put("shardingItems",item.getShardingItems());
                        if(extra != null)
                        {
                            parameter.put("extra",extra);
                        }
                        requestHelper.req(url, parameter);
                    }
                    else
                    {
                        rpcHelper.send(tup.getT1(),tup.getT2(),new JobRequest(item.getId(),name,true,entry.getValue(),extra));
                    }
                }
                jobController.newJobInstance(id, items);
            }
            else
            {
                int rand = random.nextInt(workers.size());
                Tuple2<String,Integer> tup = workers.get(rand);
                JobInstanceItem item = new JobInstanceItem();
                item.setShardingItems("all");
                item.setId(UUID.randomUUID().toString());
                if(consumerType == ConsumerType.TCP.ordinal())
                {
                    rpcHelper.send(tup.getT1(),tup.getT2(),new JobRequest(item.getId(),name,false,null,extra));
                }
                else
                {
                    String callback = dataMap.getString("callbackUrl");
                    String url = (consumerType == ConsumerType.HTTP.ordinal() ? "http" : "https") + "://" + tup.getT1() + (tup.getT2() == 80 ? "" : ":" + tup.getT2()) + "/" + callback;
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("id",item.getId());
                    map.put("name",name);
                    map.put("sharding","0");
                    if(extra != null)
                    {
                        map.put("extra",extra);
                    }
                    requestHelper.req(url,map);
                }
                jobController.newJobInstance(id, Lists.newArrayList(item));
            }
        }
        else
        {
            logger.info("末选中执行任务，跳过");
        }
    }

    public void setRequestHelper(RequestHelper requestHelper)
    {
        this.requestHelper = requestHelper;
    }

    public void setJobShardingStrategy(IJobShardingStrategy jobShardingStrategy)
    {
        this.jobShardingStrategy = jobShardingStrategy;
    }

    public void setServiceRegister(IServiceRegister serviceRegister)
    {
        this.serviceRegister = serviceRegister;
    }

    public void setJobController(JobController jobController)
    {
        this.jobController = jobController;
    }

    public void setRpcHelper(RpcHelper rpcHelper)
    {
        this.rpcHelper = rpcHelper;
    }

    public void setRedisManager(RedisManager redisManager)
    {
        this.redisManager = redisManager;
    }
}
