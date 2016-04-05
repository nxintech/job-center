package com.nxin.framework.domain;

import java.util.Date;

/**
 * Created by petzold on 2015/12/21.
 */
public class JobConfiguration
{
    private String id;
    private String name;
    private String expression;
    private int consumerType;
    private String callbackUrl;
    private boolean needSharding;
    private int shardingTotal;
    private String description;
    private Date createTime;
    private Date updateTime;
    private String extra;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression(String expression)
    {
        this.expression = expression;
    }

    public int getConsumerType()
    {
        return consumerType;
    }

    public void setConsumerType(int consumerType)
    {
        this.consumerType = consumerType;
    }

    public String getCallbackUrl()
    {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
    }

    public boolean isNeedSharding()
    {
        return needSharding;
    }

    public void setNeedSharding(boolean needSharding)
    {
        this.needSharding = needSharding;
    }

    public int getShardingTotal()
    {
        return shardingTotal;
    }

    public void setShardingTotal(int shardingTotal)
    {
        this.shardingTotal = shardingTotal;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getExtra()
    {
        return extra;
    }

    public void setExtra(String extra)
    {
        this.extra = extra;
    }
}
