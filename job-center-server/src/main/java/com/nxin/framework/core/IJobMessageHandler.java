package com.nxin.framework.core;

/**
 * Created by petzold on 2016/1/3.
 */
public interface IJobMessageHandler<T>
{
    void hand(T data);
}
