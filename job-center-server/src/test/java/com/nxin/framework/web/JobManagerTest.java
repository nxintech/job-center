package com.nxin.framework.web;

import com.nxin.framework.core.JobManager;
import org.junit.Test;

/**
 * Created by petzold on 2016/1/16.
 */
public class JobManagerTest
{
    @Test
    public void test1() throws InterruptedException
    {
        JobManager jobManager = new JobManager();
        jobManager.startAsync();
        Thread.sleep(2000000L);
    }
}
