package com.nxin.framework.core;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.framework.domain.Tuple2;
import com.nxin.framework.domain.Tuple3;
import com.nxin.framework.domain.Tuple5;
import com.nxin.framework.functions.Action2;
import com.nxin.framework.functions.Action3;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by petzold on 2015/12/18.
 */
public abstract class RpcHelper<TR extends ChannelHandler> extends AbstractIdleService
{
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private int connectionTimeOut = 15000;
    private Random random = new Random();
    private int retryNum = 3;
    private long sleepWaitTime = 500L;
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
        /*logger.info("发送消息到【{}:{}】.内容【{}】", ip, port, JSON.toJSONString(data));
        try
        {
            String key = String.format("%s:%d",ip,port);
            ChannelFuture future = cache.get(key);
            if(future == null)
            {
                logger.error("不能建立到{}:{}的连接",ip,port);
                return;
            }
            if(!future.channel().isOpen())
            {
                cache.invalidate(key);
                future = cache.get(key);
                if(future == null || !future.channel().isOpen())
                {
                    logger.error("不能建立到{}:{}的连接",ip,port);
                    return;
                }
            }
            ChannelFuture cf = future.channel().writeAndFlush(data);
            cf.addListener(new FutureListener1<String>(key)
            {
                @Override
                void operationComplete(ChannelFuture future, String key) throws Exception
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
        }*/
        logger.info("发送消息到【{}:{}】.内容【{}】", ip, port, JSON.toJSONString(data));
        send(ip, port, data, retryNum, sleepWaitTime);
    }

    public <T> void sendAny(List<Tuple2<String,Integer>> servers, T data)
    {
        if(servers == null || servers.isEmpty())
        {
            logger.error("目的地不存在或者没一台能够发送消息，消息将不会发送");
            return;
        }
        logger.info("发送消息到【{}】任意一台.内容【{}】", JSON.toJSONString(servers), JSON.toJSONString(data));
        int rand = random.nextInt(servers.size());
        Tuple2<String,Integer> tup = servers.remove(rand);
        try
        {
            String key = String.format("%s:%d", tup.getT1(), tup.getT2());
            ChannelFuture future = cache.get(key);
            if(future == null)
            {
                sendAny(servers, data);
                return;
            }
            if(!future.channel().isOpen())
            {
                cache.invalidate(key);
                future = cache.get(key);
                if(future == null || !future.channel().isOpen())
                {
                    sendAny(servers, data);
                    return;
                }
            }
            ChannelFuture cf = future.channel().writeAndFlush(data);
            cf.addListener(new FutureListener1<Tuple3<String,List<Tuple2<String,Integer>>,T>>(new Tuple3<String, List<Tuple2<String, Integer>>, T>(key, servers, data))
            {
                @Override
                void operationComplete(ChannelFuture future, Tuple3<String, List<Tuple2<String, Integer>>, T> tup) throws Exception
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
                        cache.invalidate(tup.getT1());
                        if(!tup.getT2().isEmpty())
                        {
                            sendAny(tup.getT2(), tup.getT3());
                            logger.error("消息发送失败【{}】将重试另一台服务器", future.cause().getMessage());
                        }
                        else
                        {
                            logger.error("无法将消息发送给任何一台服务器,异常:【{}】", future.cause().getMessage());
                        }
                    }
                }
            });
        }
        catch (Exception e)
        {
            logger.error("发送消息失败",e);
            sendAny(servers, data);
        }
    }

    public <T> void sendAll(List<Tuple2<String,Integer>> servers,T data)
    {
        for (Tuple2<String,Integer> tup : servers)
        {
            executor.submit(new Runnable3<Tuple2<String,Integer>,T,Integer>(tup, data, retryNum)
            {
                @Override
                void run(Tuple2<String, Integer> tup, T t, Integer retryNum)
                {
                    if(retryNum > 0)
                    {
                        send(tup.getT1(), tup.getT2(), t, retryNum, sleepWaitTime);
                    }
                }
            });
        }
    }

    private <T> void send(String host, int port, T data, int retryNum, long sleepTime)
    {
        try
        {
            String key = String.format("%s:%d", host, port);
            ChannelFuture future = cache.get(key);
            if(future == null)
            {
                Thread.sleep(sleepTime);
                send(host, port, data, retryNum - 1, sleepTime);
                return;
            }
            if(!future.channel().isOpen())
            {
                cache.invalidate(key);
                future = cache.get(key);
                if(future == null || !future.channel().isOpen())
                {
                    Thread.sleep(sleepTime);
                    send(host, port, data, retryNum - 1, sleepTime);
                    return;
                }
            }
            ChannelFuture cf = future.channel().writeAndFlush(data);
            cf.addListener(new FutureListener1<Tuple5<String,Integer,T,Integer,Long>>(new Tuple5<String, Integer, T, Integer, Long>(host, port, data, retryNum - 1,sleepTime))
            {
                @Override
                void operationComplete(ChannelFuture future, Tuple5<String, Integer, T, Integer, Long> tup) throws Exception
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
                        cache.invalidate(tup.getT1());
                        if(tup.getT4() > 0)
                        {
                            logger.error("消息发送失败【{}】将重试", future.cause().getMessage());
                            send(tup.getT1(), tup.getT2(), tup.getT3(), tup.getT4(), tup.getT5());
                        }
                        else
                        {
                            logger.error("无法发送消息到{}:{}",tup.getT1(),tup.getT2());
                        }
                    }
                }
            });
        }
        catch (Exception e)
        {
            if(retryNum > 0)
            {
                logger.error(String.format("发送消息到%s:%d失败,将重试",host,port),e);
                send(host, port, data, retryNum - 1, sleepTime);
            }
            else
            {
                logger.error(String.format("无法发送消息到%s:%d",host,port),e);
            }
        }
    }
    @Override
    protected void shutDown() throws Exception
    {
        logger.info("客户端开始停止");
        executor.shutdown();
        workerGroup.shutdownGracefully();
        logger.info("客户端停止完毕");
    }

    public void setConnectionTimeOut(int connectionTimeOut)
    {
        this.connectionTimeOut = connectionTimeOut;
    }

    public void setRetryNum(int retryNum)
    {
        this.retryNum = retryNum;
    }

    public void setSleepWaitTime(long sleepWaitTime)
    {
        this.sleepWaitTime = sleepWaitTime;
    }

    private abstract class FutureListener1<T> implements ChannelFutureListener
    {
        private T t;

        public FutureListener1(T t)
        {
            this.t = t;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception
        {
            operationComplete(future, t);
        }

        abstract void operationComplete(ChannelFuture future, T t) throws Exception;
    }

    private abstract class Runnable3<T1, T2, T3> implements Runnable
    {
        private T1 t1;
        private T2 t2;
        private T3 t3;

        public Runnable3(T1 t1, T2 t2, T3 t3)
        {
            this.t1 = t1;
            this.t2 = t2;
            this.t3 = t3;
        }

        @Override
        public void run()
        {
            run(t1, t2, t3);
        }

        abstract void run(T1 t1, T2 t2, T3 t3);
    }
}
