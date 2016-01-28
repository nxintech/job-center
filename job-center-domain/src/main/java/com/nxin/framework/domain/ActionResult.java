package com.nxin.framework.domain;

/**
 * Created by petzold on 2014/6/19.
 */
public class ActionResult<T>
{
    private int code;
    private String error;
    private T data;

    public static <T> ActionResult<T> New(T data)
    {
        ActionResult<T> ret = new ActionResult<T>();
        ret.setData(data);
        ret.setCode(0);
        return ret;
    }
    public static <T> ActionResult<T> New(int code,String message)
    {
        ActionResult<T> ret = new ActionResult<T>();
        ret.setCode(code);
        ret.setError(message);
        return ret;
    }
    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
