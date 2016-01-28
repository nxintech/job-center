package com.nxin.framework.core;

import com.google.common.base.Charsets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by petzold on 2015/12/28.
 */
public class ZkTest
{
    private CuratorFramework framework;
    @Before
    public void init() throws Exception
    {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString("172.16.200.97:2181").namespace("zk-test").retryPolicy(new ExponentialBackoffRetry(30,5));
        framework = builder.build();
        framework.start();
        framework.blockUntilConnected();
    }
    @Test
    public void test1() throws Exception
    {
        framework.create().withMode(CreateMode.EPHEMERAL).forPath("/aa", "hello".getBytes(Charsets.UTF_8));
        framework.create().withMode(CreateMode.EPHEMERAL).forPath("/aa", "world".getBytes(Charsets.UTF_8));
        String data = new String(framework.getData().forPath("aa"),Charsets.UTF_8);
        System.out.println(data);
    }
    @Test
    public void test2()
    {
        Pattern pattern = Pattern.compile("https?://%s(:%d)?/[a-zA-Z0-9/]+");
        Assert.assertTrue(pattern.matcher("http://%s/ds").matches());
    }
    @Test
    public void test3()
    {

    }
}
