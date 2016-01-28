package com.nxin.framework.handlers;

import com.nxin.framework.core.IJobMessageHandler;
import com.nxin.framework.core.JobManager;
import com.nxin.framework.domain.JobConfiguration;

/**
 * Created by petzold on 2016/1/3.
 */
public class UpdateJobHandler implements IJobMessageHandler<JobConfiguration>
{
    private JobManager jobManager;
    @Override
    public void hand(JobConfiguration message)
    {
        jobManager.updateJob(message);
    }

    public void setJobManager(JobManager jobManager)
    {
        this.jobManager = jobManager;
    }
}
