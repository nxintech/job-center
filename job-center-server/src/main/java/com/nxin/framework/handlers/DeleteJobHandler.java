package com.nxin.framework.handlers;

import com.nxin.framework.core.IJobMessageHandler;
import com.nxin.framework.core.JobManager;

/**
 * Created by petzold on 2016/1/3.
 */
public class DeleteJobHandler implements IJobMessageHandler<String>
{
    private JobManager jobManager;
    @Override
    public void hand(String name)
    {
        jobManager.deleteJob(name);
    }

    public void setJobManager(JobManager jobManager)
    {
        this.jobManager = jobManager;
    }
}
