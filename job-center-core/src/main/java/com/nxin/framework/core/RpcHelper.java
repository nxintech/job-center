package com.nxin.framework.core;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.domain.Tuple2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Random;

/**
 * Created by petzold on 2015/12/18.
 */
public abstract class RpcHelper<TR extends ChannelHandler> extends AbstractIdleService
{
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private int connectionTimeOut = 15000;
    private Random random = new Random();
    private Logger logger = LoggerFactory.getLogger(RpcHelper.class);
    private LoadingCache<String,ChannelFuture> cache = CacheBuilder.newBuilder().softValues().build(new CacheLoader<String, ChannelFuture>()
    {
        @Override
        public ChannelFuture load(String key) throws Exception
        {
            String[] arr = key.split(":");
            ChannelFuture future = bootstrap.connect(arr[0], Integer.parseInt(arr[1])).sync();
            future.awaitUninterruptibly();
            if(!future.isDone() || !future.isSuccess())
            {
                logger.error("连接到【{}】失败",key);
                return null;
            }
            return future;
        }
    });
    protected void startUp() throws Exception
    {
        logger.info("客户端开始启动");
        int num = Runtime.getRuntime().availableProcessors();
        logger.info("开始构建bootstrap");
        workerGroup = new NioEventLoopGroup(2 * num);
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.CONNECT_TIMEOUT_MILLIS,connectionTimeOut).option(ChannelOption.TCP_NODELAY,true).option(ChannelOption.SO_REUSEADDR,true).option(ChannelOption.SO_KEEPALIVE,true).option(ChannelOption.SO_SNDBUF,65535).option(ChannelOption.SO_RCVBUF, 65535);
        bootstrap.handler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception
            {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("encoder", newEncoder());
                pipeline.addLast("timeout", new IdleStateHandler(0, 0, 120));
            }
        });
        logger.info("完成构建bootstrap");
    }

    protected abstract TR newEncoder();

    public <T> void send(String ip,int port,T data)
    {
        logger.info("发送消息到【{}:{}】.内容【{}】成功", ip, port, JSON.toJSONString(data));
        try
        {
            final String key = String.format("%s:%d",ip,port);
            ChannelFuture future = cache.get(key);
            if(!future.channel().isOpen())
            {
                cache.invalidate(key);
                future = cache.get(key);
            }
            ChannelFuture cf = future.channel().writeAndFlush(data);
            cf.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception
                {
                    if(future.isSuccess())
                    {
                        logger.info("消息发送成功");
                        return;
                    }
                    else
                    {
                        if(future.channel().isOpen())
                        {
                            future.channel().close();
                        }
                        cache.invalidate(key);
                        logger.error("消息发送失败【{}】",future.cause().getMessage());
                    }
                }
            });
        }
        catch (Exception e)
        {
            logger.error("发送消息失败",e);
        }
    }

    public <T> void sendAny(final List<Tuple2<String,Integer>> servers,final T data)
    {
        if(servers == null || servers.isEmpty())
        {
            logger.error("目的地不存在，消息将不会发送");
            return;
        }
        logger.info("发送消息到【{}】.内容【{}】", JSON.toJSONString(servers), JSON.toJSONString(data));
        final int rand = random.nextInt(servers.size());
        Tuple2<String,Integer> tup = servers.get(rand);
        try
        {
            final String key = String.format("%s:%d",tup.getT1(),tup.getT2());
            ChannelFuture future = cache.get(key);
            ChannelFuture cf = future.channel().writeAndFlush(data);
            cf.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception
                {
                    if (future.isSuccess())
                    {
                        logger.info("消息发送成功");
                        return;
                    } else
                    {
                        if (future.channel().isOpen())
                        {
                            future.channel().close();
                        }
                        cache.invalidate(key);
                        servers.remove(rand);
                        sendAny(servers, data);
                        logger.error("消息发送失败【{}】将重试另一台服务器", future.cause().getMessage());
                    }
                }
            });
        }
        catch (Exception e)
        {
            logger.error("发送消息失败",e);
        }
    }

    public <T> void sendAll(List<Tuple2<String,Integer>> servers,T data)
    {
        for (Tuple2<String,Integer> tup : servers)
        {
            send(tup.getT1(),tup.getT2(),data);
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        workerGroup.shutdownGracefully();
    }

    public void setConnectionTimeOut(int connectionTimeOut)
    {
        this.connectionTimeOut = connectionTimeOut;
    }
}
