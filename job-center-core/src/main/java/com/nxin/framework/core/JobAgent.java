package com.nxin.framework.core;

import com.google.common.util.concurrent.AbstractIdleService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

/**
 * Created by petzold on 2015/12/15.
 */
public abstract class JobAgent extends AbstractIdleService
{
    private EventLoopGroup boosGroup;
    private EventLoopGroup workerGroup;
    private int timeout = 3000;
    private int port = 7732;
    private Logger logger = LoggerFactory.getLogger(JobAgent.class);
    @Override
    protected void startUp() throws Exception
    {
        logger.info("开始启动RPC服务器");
        int num = Runtime.getRuntime().availableProcessors();
        boosGroup = new NioEventLoopGroup(num);
        workerGroup = new NioEventLoopGroup(num * 2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup,workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.CONNECT_TIMEOUT_MILLIS,timeout).option(ChannelOption.SO_BACKLOG,1024).option(ChannelOption.SO_KEEPALIVE,true).option(ChannelOption.SO_REUSEADDR,true).option(ChannelOption.SO_SNDBUF,65535).option(ChannelOption.SO_RCVBUF,65535).option(ChannelOption.TCP_NODELAY, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception
            {
                initializeChannel(ch.pipeline());
            }
        });
        bootstrap.bind(new InetSocketAddress(port)).sync();
        logger.info("RPC服务在端口{}启动成功",port);
    }

    @Override
    protected void shutDown() throws Exception
    {
        logger.info("RPC服务开始停止");
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("RPC服务停止成功");
    }

    protected abstract void initializeChannel(ChannelPipeline pipeline);

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
}
