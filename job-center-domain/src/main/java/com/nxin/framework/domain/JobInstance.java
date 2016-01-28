package com.nxin.framework.domain;

import java.util.Date;
import java.util.List;

/**
 * Created by petzold on 2015/12/21.
 */
public class JobInstance
{
    private String id;
    private String jobId;
    private Date createTime;
    private List<JobInstanceItem> items;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public List<JobInstanceItem> getItems()
    {
        return items;
    }

    public void setItems(List<JobInstanceItem> items)
    {
        this.items = items;
    }
}
