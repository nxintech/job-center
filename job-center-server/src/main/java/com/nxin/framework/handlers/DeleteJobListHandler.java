package com.nxin.framework.handlers;

import com.nxin.framework.core.IJobMessageHandler;
import com.nxin.framework.core.JobManager;

import java.util.List;

/**
 * Created by petzold on 2016/1/12.
 */
public class DeleteJobListHandler implements IJobMessageHandler<List<String>>
{
    private JobManager jobManager;
    @Override
    public void hand(List<String> message)
    {
        jobManager.deleteJobs(message);
    }

    public void setJobManager(JobManager jobManager)
    {
        this.jobManager = jobManager;
    }
}
