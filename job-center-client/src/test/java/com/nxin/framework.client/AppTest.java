package com.nxin.framework.client;

import org.junit.Test;

/**
 * Created by petzold on 2016/4/12.
 */
public class AppTest
{
    @Test
    public void test1()
    {
        RpcJobWorker worker = new RpcJobWorker();
        worker.setPort(2211);
        worker.startAsync();
    }
}
