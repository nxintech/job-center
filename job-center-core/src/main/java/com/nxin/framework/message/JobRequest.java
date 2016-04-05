package com.nxin.framework.message;

import java.util.List;

/**
 * Created by petzold on 2015/12/15.
 */
public class JobRequest
{
    private String id;
    private String jobName;
    private boolean sharding;
    private List<Integer> shardingItems;
    private String extra;

    public JobRequest(String id, String jobName, boolean sharding, List<Integer> shardingItems, String extra)
    {
        this.id = id;
        this.jobName = jobName;
        this.sharding = sharding;
        this.shardingItems = shardingItems;
        this.extra = extra;
    }

    public String getId()
    {
        return id;
    }

    public String getJobName()
    {
        return jobName;
    }

    public boolean isSharding()
    {
        return sharding;
    }

    public List<Integer> getShardingItems()
    {
        return shardingItems;
    }

    public String getExtra()
    {
        return extra;
    }
}
