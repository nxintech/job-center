package com.nxin.framework.core;

import com.github.rholder.retry.*;
import com.google.common.base.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.nxin.framework.domain.ConnState;
import com.nxin.framework.domain.Tuple2;
import com.nxin.framework.domain.Tuple3;
import com.nxin.framework.functions.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by petzold on 2015/12/17.
 */
public class ZkServiceRegister extends AbstractIdleService implements IServiceRegister<CuratorFramework>
{
    private CuratorFramework framework;
    private String servers;
    private String namespace;
    private int maxRetry = 5;
    private int minSleep = 100;
    private int maxSleep = 800;
    private int sessionTimeOut = 600000;
    private int connectionTimeOut = 15000;
    private long syncPeriod = 180000;
    private Timer timer;
    private Tuple3<Set<String>,String,Integer> jobWorkers;
    private Tuple2<String,Integer> jobServer;
    private List<IStateListener<ConnState>> listeners = new ArrayList<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder().retryIfResult(Predicates.<Boolean>equalTo(false)).retryIfRuntimeException().withWaitStrategy(WaitStrategies.fixedWait(500, TimeUnit.MILLISECONDS)).withStopStrategy(StopStrategies.stopAfterAttempt(3)).build();
    private Logger logger = LoggerFactory.getLogger(ZkServiceRegister.class);
    protected void startUp() throws Exception
    {
        logger.info("服务注册中心开始启动");
        framework = CuratorFrameworkFactory.builder().connectString(servers).namespace(namespace).retryPolicy(new ExponentialBackoffRetry(minSleep,maxRetry,maxSleep)).sessionTimeoutMs(sessionTimeOut).connectionTimeoutMs(connectionTimeOut).build();
        framework.start();
        framework.blockUntilConnected();
        framework.getConnectionStateListenable().addListener(new ConnectionStateListener()
        {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState)
            {
                if(newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED)
                {
                    sync();
                }
                ConnState state = getConnState(newState);
                if(state != null)
                {
                    for (IStateListener<ConnState> listener : listeners)
                    {
                        listener.onStateChanged(state);
                    }
                }
            }
        });
        timer = new Timer(true);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                sync();
            }
        },syncPeriod,syncPeriod);
    }

    private ConnState getConnState(ConnectionState connectionState)
    {
        ConnState connState;
        switch (connectionState)
        {
            case CONNECTED:
                connState = ConnState.CONNECTED;
                break;
            case LOST:
                connState =  ConnState.LOST;
                break;
            case RECONNECTED:
                connState =  ConnState.RECONNECTED;
                break;
            default:
                connState = null;
                break;
        }
        return connState;
    }

    @Override
    public CuratorFramework getObejct()
    {
        return framework;
    }

    @Override
    public void exec(Action1<CuratorFramework> action)
    {
        action.call(framework);
    }

    @Override
    public <T1> void exec(Action2<CuratorFramework, T1> action, T1 t1)
    {
        action.call(framework, t1);
    }

    @Override
    public <T1, T2> void exec(Action3<CuratorFramework, T1, T2> action, T1 t1, T2 t2)
    {
        action.call(framework, t1, t2);
    }

    @Override
    public <T1, R> R excute(Func2<CuratorFramework, T1, R> fun, T1 t1)
    {
        return fun.call(framework, t1);
    }

    @Override
    public <T1, T2, R> R excute(Func3<CuratorFramework, T1, T2, R> fun, T1 t1, T2 t2)
    {
        return fun.call(framework, t1, t2);
    }

    @Override
    public void registerJobWorkers(Set<String> jobNames, String ip, int port)
    {
        jobWorkers = new Tuple3<>(jobNames, ip, port);
        String pt = String.valueOf(port);
        for (String jobName : jobNames)
        {
            register("jobWorkers", ZKPaths.makePath(jobName, ip), pt);
        }
    }

    @Override
    public void registerJobServer(String ip, Integer port)
    {
        jobServer = new Tuple2<>(ip, port);
        String pt = String.valueOf(port);
        register("jobServers","/"+ip, pt);
    }

    @Override
    public boolean selectLeader(String name)
    {
        try
        {
            String path = String.format("/select-leader-%s", name);
            if(framework.checkExists().forPath(path) != null)
            {
                return false;
            }
            framework.create().withMode(CreateMode.EPHEMERAL).forPath(path, "master".getBytes(Charsets.UTF_8));
            return true;
        }
        catch (KeeperException.NodeExistsException e)
        {
            return false;
        }
        catch (Exception e)
        {
            logger.error("选择主节点时出错",e);
            return false;
        }
    }

    @Override
    public void removeLeader(String name)
    {
        try
        {
            String path = String.format("/select-leader-%s", name);
            if(framework.checkExists().forPath(path) != null)
            {
                framework.delete().forPath(path);
            }
        }
        catch (KeeperException.NoNodeException e)
        {
            logger.info("节点已经被清理");
        }
        catch (Exception e)
        {
            logger.error("清理上次任务主节点失败",e);
        }
    }

    @Override
    public List<Tuple2<String, Integer>> findJobServers()
    {
        List<Tuple2<String,String>> ls = getChildAndData("jobServers","/");
        return Lists.transform(ls, new Function<Tuple2<String, String>, Tuple2<String, Integer>>()
        {
            @Override
            public Tuple2<String, Integer> apply(Tuple2<String, String> tup)
            {
                return new Tuple2<>(tup.getT1(), Integer.parseInt(tup.getT2()));
            }
        });
    }

    private List<Tuple2<String,String>> getChildAndData(String nameSpace,final String path)
    {
        try
        {
            CuratorFramework curator = framework.usingNamespace(nameSpace);
            String pth = ZKPaths.makePath("",path);
            if(curator.checkExists().forPath(pth) == null)
            {
                return new ArrayList<Tuple2<String,String>>();
            }
            List<String> children = curator.getChildren().forPath(pth);
            return ListAdapter.adapt(children).collectWith(new Function2<String, CuratorFramework, Tuple2<String, String>>()
            {
                @Override
                public Tuple2<String, String> value(String s, CuratorFramework curatorFramework)
                {
                    String p = ZKPaths.makePath(path, s);
                    try
                    {
                        String data = new String(curatorFramework.getData().forPath(p), Charsets.UTF_8);
                        return new Tuple2<>(s, data);
                    } catch (Exception e)
                    {
                        logger.error(String.format("读取节点【%s】信息失败", p, s), e);
                        throw new RuntimeException(String.format("读取节点【%s】信息失败", p, s), e);
                    }
                }
            }, curator);
        } catch (Exception e)
        {
            logger.error(String.format("获取节点【/%s/%s】子节点失败",nameSpace,path),e);
            return new ArrayList<Tuple2<String, String>>();
        }
    }

    @Override
    public List<Tuple2<String, Integer>> findJobWorkers(String name)
    {
        List<Tuple2<String,String>> ls = getChildAndData("jobWorkers",name);
        return ListAdapter.adapt(ls).collect(new com.gs.collections.api.block.function.Function<Tuple2<String, String>, Tuple2<String, Integer>>()
        {
            @Override
            public Tuple2<String, Integer> valueOf(Tuple2<String, String> tup)
            {
                return new Tuple2<>(tup.getT1(), Integer.parseInt(tup.getT2()));
            }
        });
    }

    @Override
    public void addListener(IStateListener<ConnState> listener)
    {
        listeners.add(listener);
    }

    @Override
    protected void shutDown() throws Exception
    {
        logger.info("Zookeeper注册中心开始停止");
        listeners.clear();
        executorService.shutdown();
        Thread.sleep(500L);
        CloseableUtils.closeQuietly(framework);
        logger.info("Zookeeper注册中心停止完毕");
    }
    private void register(String nameSpace, String path, String data)
    {
        executorService.submit(new Runnable3<String, String, String>(nameSpace, path, data)
        {
            @Override
            void run(String nameSpace, String path, String data)
            {
                try
                {
                    retryer.call(new Callable3<String,String,byte[],Boolean>(nameSpace, path, data.getBytes(Charsets.UTF_8))
                    {
                        @Override
                        Boolean call(String nameSpace, String path, byte[] data) throws Exception
                        {
                            if(framework.usingNamespace(nameSpace).checkExists().forPath(path) != null)
                            {
                                return true;
                            }
                            else
                            {
                                framework.usingNamespace(nameSpace).create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data);
                                Thread.sleep(500L);
                                if(framework.usingNamespace(nameSpace).checkExists().forPath(path) != null)
                                {
                                    return true;
                                }
                                return false;
                            }
                        }
                    });
                } catch (ExecutionException e)
                {
                    logger.error("设置zk失败", e);
                } catch (RetryException e)
                {
                    logger.error("重新尝试设置zk失败", e);
                }
            }
        });
    }
    private void sync()
    {
        if(jobServer != null)
        {
            register("jobServers","/"+jobServer.getT1(),String.valueOf(jobServer.getT2()));
        }
        if(jobWorkers != null)
        {
            String port = String.valueOf(jobWorkers.getT3());
            for (String jobName : jobWorkers.getT1())
            {
                register("jobWorkers", ZKPaths.makePath(jobName, jobWorkers.getT2()), port);
            }
        }
    }

    public void setServers(String servers)
    {
        this.servers = servers;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public void setMaxRetry(int maxRetry)
    {
        this.maxRetry = maxRetry;
    }

    public void setMinSleep(int minSleep)
    {
        this.minSleep = minSleep;
    }

    public void setMaxSleep(int maxSleep)
    {
        this.maxSleep = maxSleep;
    }

    public void setSessionTimeOut(int sessionTimeOut)
    {
        this.sessionTimeOut = sessionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut)
    {
        this.connectionTimeOut = connectionTimeOut;
    }

    public void setSyncPeriod(long syncPeriod)
    {
        this.syncPeriod = syncPeriod;
    }

    private abstract class Callable3<T1,T2,T3,R> implements Callable<R>
    {
        private T1 t1;
        private T2 t2;
        private T3 t3;

        public Callable3(T1 t1, T2 t2, T3 t3)
        {
            this.t1 = t1;
            this.t2 = t2;
            this.t3 = t3;
        }

        @Override
        public R call() throws Exception
        {
            return call(t1, t2, t3);
        }

        abstract R call(T1 t1, T2 t2, T3 t3) throws Exception;
    }

    private abstract class Runnable3<T1,T2,T3> implements Runnable
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
