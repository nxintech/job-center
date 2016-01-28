package com.nxin.framework.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by petzold on 2015/12/29.
 */
public class IocTest
{
    private ApplicationContext applicationContext;
    @Before
    public void init()
    {
        applicationContext = new ClassPathXmlApplicationContext("beans.xml");
    }
    @Test
    public void test1()
    {
        Bar bar = applicationContext.getBean("bar",Bar.class);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(bar, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,false);
        Assert.assertNotNull(bar.getFoo());
    }
    @Test
    public void test2()
    {
        RpcHelper helper = applicationContext.getBean("rpcHelper",RpcHelper.class);
        helper.send("172.16.200.106",7732,"ss");
    }
}
