package com.nxin.framework.core;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by petzold on 2016/2/1.
 */
public class IOTest
{
    @Test
    public void test1() throws MalformedURLException
    {
        URL url = URI.create("file:/D:/test.txt").toURL();
        System.out.println(url);
    }
    @Test
    public void test2()
    {
        URL url = Thread.currentThread().getContextClassLoader().getResource("beans.xml");
        System.out.println(url);
    }
}
