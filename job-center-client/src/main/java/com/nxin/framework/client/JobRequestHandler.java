package com.nxin.framework.client;

import com.nxin.framework.message.JobRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by petzold on 2015/12/16.
 */
public class JobRequestHandler extends SimpleChannelInboundHandler<JobRequest>
{
    private JobWorker worker;
    private Logger logger = LoggerFactory.getLogger(JobRequestHandler.class);

    public JobRequestHandler(JobWorker worker)
    {
        this.worker = worker;
    }

    protected void messageReceived(ChannelHandlerContext ctx, JobRequest msg) throws Exception
    {
        worker.pushJob(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        logger.error("处理请求时失败",cause);
        ctx.close();
    }
}
