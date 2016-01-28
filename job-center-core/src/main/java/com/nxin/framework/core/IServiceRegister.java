package com.nxin.framework.core;

import com.nxin.domain.Tuple2;
import com.nxin.functions.*;
import java.util.List;
import java.util.Set;

/**
 * Created by petzold on 2015/12/17.
 */
public interface IServiceRegister<T>
{
    T getObejct();
    void exec(Action1<T> action);
    <T1> void exec(Action2<T,T1> action,T1 t1);
    <T1,T2> void exec(Action3<T,T1,T2> action,T1 t1,T2 t2);
    <T1,R> R excute(Func2<T,T1,R> fun,T1 t1);
    <T1,T2,R> R excute(Func3<T,T1,T2,R> fun,T1 t1,T2 t2);
    void registerJobWorkers(Set<String> jobNames,String ip,int port);
    void registerJobServer(String ip,Integer port);
    boolean selectLeader(String name);
    void removeLeader(String name);
    List<Tuple2<String,Integer>> findJobServers();
    List<Tuple2<String,Integer>> findJobWorkers(String name);
    <T1,T2> void onReconnected(Action3<T,T1,T2> action,T1 t1,T2 t2);
}
