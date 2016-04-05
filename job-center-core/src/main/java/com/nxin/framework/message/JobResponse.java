package com.nxin.framework.message;

/**
 * Created by petzold on 2015/12/15.
 */
public class JobResponse
{
    private String id;
    private int status;
    private String error;

    public JobResponse()
    {
    }

    public JobResponse(String id, int status, String error)
    {
        this.id = id;
        this.status = status;
        this.error = error;
    }

    public String getId()
    {
        return id;
    }

    public int getStatus()
    {
        return status;
    }

    public String getError()
    {
        return error;
    }
}
