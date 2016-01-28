package com.nxin.framework.domain;

import java.util.Date;
import java.util.List;

/**
 * Created by petzold on 2016/1/12.
 */
public class JobInstanceInfo
{
    private String id;
    private String jobName;
    private String jobId;
    private Date execTime;
    private List<JobInstanceItem> items;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public Date getExecTime()
    {
        return execTime;
    }

    public void setExecTime(Date execTime)
    {
        this.execTime = execTime;
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
