package com.nxin.framework.client;

import com.nxin.framework.codec.JobRequestDecoder;
import com.nxin.framework.core.JobAgent;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by petzold on 2016/1/20.
 */
public class RpcJobWorker extends JobAgent
{
    private JobWorker jobWorker;
    @Override
    protected void initializeChannel(ChannelPipeline pipeline)
    {
        pipeline.addLast("decoder", new JobRequestDecoder());
        pipeline.addLast("timeout", new IdleStateHandler(0, 0, 120));
        pipeline.addLast("handler", new JobRequestHandler(jobWorker));
    }

    public void setJobWorker(JobWorker jobWorker)
    {
        this.jobWorker = jobWorker;
    }
}
