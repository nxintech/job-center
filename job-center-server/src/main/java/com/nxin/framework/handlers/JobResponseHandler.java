package com.nxin.framework.handlers;

import com.nxin.framework.core.IJobMessageHandler;
import com.nxin.framework.manager.IJobRepository;
import com.nxin.framework.message.JobResponse;

/**
 * Created by petzold on 2016/1/12.
 */
public class JobResponseHandler implements IJobMessageHandler<JobResponse>
{
    private IJobRepository jobRepository;
    @Override
    public void hand(JobResponse message)
    {
        jobRepository.updateItem(message.getId(),message.getStatus(),message.getError());
    }

    public void setJobRepository(IJobRepository jobRepository)
    {
        this.jobRepository = jobRepository;
    }
}
