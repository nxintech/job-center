package com.nxin.framework.core;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.apache.curator.utils.ZKPaths;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by petzold on 2015/12/17.
 */
public class ConsulTest
{
    private ConsulClient consul;
    @Before
    public void init()
    {
        consul = new ConsulClient("localhost");
    }
    @Test
    public void test1()
    {
        consul.setKVValue("hello", "foo");
        Response<GetValue> response = consul.getKVValue("hello");
        System.out.println(response.getValue().getValue());
    }
    @Test
    public void test2()
    {
        Response<List<String>> response = consul.getCatalogDatacenters();
        System.out.println("Datacenters: " + response.getValue());
    }
    @Test
    public void test3()
    {
        NewService newService = new NewService();
        newService.setId("myapp_02");
        newService.setName("myapp");
        newService.setPort(8080);
        consul.agentServiceRegister(newService);
    }
    @Test
    public void test4()
    {
        Response<List<CatalogService>> resp = consul.getCatalogService("myapp", new QueryParams("dc1"));
        System.out.println(resp.getValue().size());
    }
    @Test
    public void test5()
    {
        String result = ZKPaths.makePath("jobWorkers", "myapp", "127.0.0.1");
        System.out.println(result);
    }
    @Test
    public void test6()
    {
        String s = "127.0.0.1:80";
        String[] arr = s.split(":");

    }
    @Test
    public void test7()
    {
        String cl = JobAgent.class.getName();
        System.out.println(cl);
    }
    enum Color {RED,BLUE}
    @Test
    public void test8()
    {
        System.out.println(Color.RED.ordinal()+"---------"+Color.BLUE.ordinal());
    }
}
