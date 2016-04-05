package com.nxin.framework.domain;

/**
 * Created by petzold on 2015/12/21.
 */
public class JobInstanceItem
{
    private String id;
    private String instanceId;
    private String shardingItems;
    private int status;
    private String error;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getShardingItems()
    {
        return shardingItems;
    }

    public void setShardingItems(String shardingItems)
    {
        this.shardingItems = shardingItems;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
}
