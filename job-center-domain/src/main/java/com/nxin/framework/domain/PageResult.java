package com.nxin.framework.domain;

import java.util.List;

/**
 * Created by petzold on 2014/6/30.
 */
public class PageResult<T> extends ActionResult<List<T>>
{
    private int count;

    public static <T> PageResult<T> New(List<T> data,int count)
    {
        PageResult<T> result = new PageResult<T>();
        result.setCode(0);
        result.setData(data);
        result.setCount(count);
        return result;
    }

    public static <T> PageResult<T> New(String errorMessage, int code)
    {
        PageResult<T> result = new PageResult<T>();
        result.setCode(code);
        result.setError(errorMessage);
        return result;
    }
    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}
