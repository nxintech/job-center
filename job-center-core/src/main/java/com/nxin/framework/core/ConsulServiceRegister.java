package com.nxin.framework.core;

import com.ecwid.consul.v1.ConsistencyMode;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.google.common.util.concurrent.AbstractIdleService;
import com.nxin.collection.ListAdapter;
import com.nxin.domain.Tuple2;
import com.nxin.functions.*;
import java.util.List;
import java.util.Set;

/**
 * Created by petzold on 2015/12/17.
 */
public class ConsulServiceRegister extends AbstractIdleService implements IServiceRegister<ConsulClient>
{
    private ConsulClient consul;
    @Override
    protected void startUp() throws Exception
    {
        consul = new ConsulClient("localhost");
    }

    @Override
    public ConsulClient getObejct()
    {
        return consul;
    }

    @Override
    public void exec(Action1<ConsulClient> action)
    {
        action.call(consul);
    }

    @Override
    public <T1> void exec(Action2<ConsulClient, T1> action, T1 t1)
    {
        action.call(consul,t1);
    }

    @Override
    public <T1, T2> void exec(Action3<ConsulClient, T1, T2> action, T1 t1, T2 t2)
    {
        action.call(consul,t1,t2);
    }

    @Override
    public <T1, R> R excute(Func2<ConsulClient, T1, R> fun, T1 t1)
    {
        return fun.call(consul,t1);
    }

    @Override
    public <T1, T2, R> R excute(Func3<ConsulClient, T1, T2, R> fun, T1 t1, T2 t2)
    {
        return fun.call(consul,t1,t2);
    }

    @Override
    public void registerJobWorkers(Set<String> jobNames, String ip, int port)
    {
        for (String name : jobNames)
        {
            NewService newService = new NewService();
            newService.setId(name);
            newService.setName(name);
            newService.setAddress(ip);
            newService.setPort(port);
            consul.agentServiceRegister(newService);
        }
    }

    @Override
    public void registerJobServer(String ip, Integer port)
    {
        NewService newService = new NewService();
        newService.setId("jobServer");
        newService.setName("jobServer");
        newService.setAddress(ip);
        newService.setPort(port);
        consul.agentServiceRegister(newService);
    }

    @Override
    public boolean selectLeader(String name)
    {
        Response<Boolean> response = consul.setKVValue("select-leader-" + name, "master");
        return response.getValue();
    }

    @Override
    public void removeLeader(String name)
    {
        consul.deleteKVValue("select-leader-"+name);
    }

    @Override
    public List<Tuple2<String, Integer>> findJobServers()
    {
        return findJobWorkers("jobServer");
    }

    @Override
    public List<Tuple2<String, Integer>> findJobWorkers(String name)
    {
        Response<List<CatalogService>> resp = consul.getCatalogService(name, new QueryParams(ConsistencyMode.DEFAULT));
        return ListAdapter.transform(new Func1<CatalogService, Tuple2<String, Integer>>()
        {
            @Override
            public Tuple2<String, Integer> call(CatalogService catalogService)
            {
                return new Tuple2<String, Integer>(catalogService.getAddress(),catalogService.getServicePort());
            }
        }, resp.getValue());
    }

    @Override
    public <T1, T2> void onReconnected(Action3<ConsulClient, T1, T2> action, T1 t1, T2 t2)
    {

    }

    @Override
    protected void shutDown() throws Exception
    {

    }
}
