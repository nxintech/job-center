package com.nxin.framework.client;

import com.nxin.framework.message.JobRequest;
import com.nxin.framework.message.JobResponse;

/**
 * Created by petzold on 2015/12/16.
 */
public interface IJob
{
    JobResponse exec(JobRequest request);
}
