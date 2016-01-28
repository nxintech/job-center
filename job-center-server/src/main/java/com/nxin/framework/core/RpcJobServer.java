package com.nxin.framework.core;

import com.nxin.codec.ICodec;
import com.nxin.framework.codec.JobMessageDecoder;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.Map;

/**
 * Created by petzold on 2016/1/19.
 */
public class RpcJobServer extends JobAgent
{
    private Map<Integer,IJobMessageHandler> handlerMap;
    private ICodec codec;
    @Override
    protected void initializeChannel(ChannelPipeline pipeline)
    {
        pipeline.addLast("decoder", new JobMessageDecoder(codec));
        pipeline.addLast("timeout", new IdleStateHandler(0, 0, 120));
        pipeline.addLast("handler", new JobMessageHandler(handlerMap));
    }

    public void setHandlerMap(Map<Integer, IJobMessageHandler> handlerMap)
    {
        this.handlerMap = handlerMap;
    }

    public void setCodec(ICodec codec)
    {
        this.codec = codec;
    }
}
