package com.nxin.framework.core;

import com.alibaba.fastjson.JSON;
import com.nxin.framework.message.JobMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * Created by petzold on 2015/12/18.
 */
public class JobMessageHandler extends SimpleChannelInboundHandler<JobMessage>
{
    private Map<Integer,IJobMessageHandler> handlerMap;
    private Logger logger = LoggerFactory.getLogger(JobMessageHandler.class);

    public JobMessageHandler(Map<Integer, IJobMessageHandler> handlerMap)
    {
        this.handlerMap = handlerMap;
    }

    protected void messageReceived(ChannelHandlerContext ctx, JobMessage msg) throws Exception
    {
        logger.info("收到消息【{}】", JSON.toJSONString(msg.getMessage()));
        IJobMessageHandler handler = handlerMap.get(msg.getType());
        handler.hand(msg.getMessage());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        logger.error("处理请求时失败",cause);
        ctx.close();
    }
}
