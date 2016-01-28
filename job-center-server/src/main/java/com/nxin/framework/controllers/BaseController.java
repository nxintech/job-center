package com.nxin.framework.controllers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nxin.framework.domain.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Created by petzold on 2016/1/3.
 */
public abstract class BaseController
{
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    abstract String getError(int code);
    private LoadingCache<Integer,String> cache = CacheBuilder.newBuilder().build(new CacheLoader<Integer, String>()
    {
        @Override
        public String load(Integer key) throws Exception
        {
            return getError(key);
        }
    });
    protected <T> ActionResult<T> fail(int code)
    {
        try
        {
            return ActionResult.New(code,cache.get(code));
        } catch (ExecutionException e)
        {
            return ActionResult.New(code,e.getMessage());
        }
    }
}
