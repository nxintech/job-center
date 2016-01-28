package com.nxin.framework.core;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.collection.ListAdapter;
import com.nxin.domain.Tuple2;
import com.nxin.functions.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private Logger logger = LoggerFactory.getLogger(ZkServiceRegister.class);
    protected void startUp() throws Exception
    {
        logger.info("服务注册中心开始启动");
        framework = CuratorFrameworkFactory.builder().connectString(servers).namespace(namespace).retryPolicy(new ExponentialBackoffRetry(minSleep,maxRetry,maxSleep)).sessionTimeoutMs(sessionTimeOut).connectionTimeoutMs(connectionTimeOut).build();
        framework.start();
        framework.blockUntilConnected();
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
    public void registerJobWorkers(final Set<String> jobNames, final String ip, final int port)
    {
        final Action3<Set<String>,String,Integer> action = new Action3<Set<String>, String, Integer>()
        {
            @Override
            public void call(Set<String> jobNames, String ip, Integer port)
            {
                try
                {
                    for (String name : jobNames)
                    {
                        String path = ZKPaths.makePath(name,ip);
                        if(framework.usingNamespace("jobWorkers").checkExists().forPath(path) != null)
                        {
                            framework.usingNamespace("jobWorkers").setData().forPath(path, String.valueOf(port).getBytes(Charsets.UTF_8));
                        }
                        else
                        {
                            framework.usingNamespace("jobWorkers").create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,String.valueOf(port).getBytes(Charsets.UTF_8));
                        }
                    }
                } catch (Exception e)
                {
                    logger.error(String.format("注册任务【%s】失败,IP【%s】",ip,port),e);
                }
            }
        };
        action.call(jobNames, ip, port);
        framework.getConnectionStateListenable().addListener(new ConnectionStateListener()
        {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState)
            {
                if (newState == ConnectionState.RECONNECTED)
                {
                    action.call(jobNames, ip, port);
                }
            }
        });
    }

    @Override
    public void registerJobServer(final String ip, final Integer port)
    {
        final Action2<String,Integer> action = new Action2<String, Integer>()
        {
            @Override
            public void call(String ip, Integer port)
            {
                try
                {
                    if(framework.usingNamespace("jobServers").checkExists().forPath("/"+ip) != null)
                    {
                        framework.usingNamespace("jobServers").setData().forPath("/"+ip,String.valueOf(port).getBytes(Charsets.UTF_8));
                    }
                    else
                    {
                        framework.usingNamespace("jobServers").create().withMode(CreateMode.EPHEMERAL).forPath("/"+ip,String.valueOf(port).getBytes(Charsets.UTF_8));
                    }
                }
                catch (Exception e)
                {
                    logger.error("注册JobServer失败",e);
                }
            }
        };
        action.call(ip, port);
        framework.getConnectionStateListenable().addListener(new ConnectionStateListener()
        {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState)
            {
                if (newState == ConnectionState.RECONNECTED)
                {
                    action.call(ip, port);
                }
            }
        });
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
        return ListAdapter.transform(new Func1<Tuple2<String, String>, Tuple2<String, Integer>>()
        {
            @Override
            public Tuple2<String, Integer> call(Tuple2<String, String> tup)
            {
                return new Tuple2<String, Integer>(tup.getT1(), Integer.parseInt(tup.getT2()));
            }
        }, ls);
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
            return ListAdapter.transformWith(new Func2<String, CuratorFramework, Tuple2<String, String>>()
            {
                @Override
                public Tuple2<String, String> call(String s, CuratorFramework curatorFramework)
                {
                    String p = ZKPaths.makePath(path,s);
                    try
                    {
                        String data = new String(curatorFramework.getData().forPath(p),Charsets.UTF_8);
                        return new Tuple2<String, String>(s,data);
                    } catch (Exception e)
                    {
                        logger.error(String.format("读取节点【%s】信息失败",p,s),e);
                        throw new RuntimeException(String.format("读取节点【%s】信息失败",p,s),e);
                    }
                }
            }, children, curator);
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
        return ListAdapter.transform(new Func1<Tuple2<String, String>, Tuple2<String, Integer>>()
        {
            @Override
            public Tuple2<String, Integer> call(Tuple2<String, String> tup)
            {
                return new Tuple2<String, Integer>(tup.getT1(), Integer.parseInt(tup.getT2()));
            }
        }, ls);
    }

    @Override
    public <T1,T2> void onReconnected(final Action3<CuratorFramework,T1,T2> action,final T1 t1,final T2 t2)
    {
        framework.getConnectionStateListenable().addListener(new ConnectionStateListener()
        {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState)
            {
                if (newState == ConnectionState.RECONNECTED)
                {
                    action.call(client, t1, t2);
                }
            }
        });
    }

    @Override
    protected void shutDown() throws Exception
    {
        logger.info("Zookeeper注册中心开始停止");
        Thread.sleep(500L);
        CloseableUtils.closeQuietly(framework);
        logger.info("Zookeeper注册中心停止完毕");
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
}
