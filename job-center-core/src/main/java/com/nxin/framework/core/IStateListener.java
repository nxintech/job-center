package com.nxin.framework.core;

/**
 * Created by petzold on 2016/5/13.
 */
public interface IStateListener<T>
{
    void onStateChanged(T state);
}
