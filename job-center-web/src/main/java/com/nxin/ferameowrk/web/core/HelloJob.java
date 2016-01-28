package com.nxin.ferameowrk.web.core;

import com.alibaba.fastjson.JSON;
import com.nxin.framework.client.IJob;
import com.nxin.framework.message.JobRequest;
import com.nxin.framework.message.JobResponse;

/**
 * Created by petzold on 2016/1/22.
 */
public class HelloJob implements IJob
{
    @Override
    public JobResponse exec(JobRequest request)
    {
        System.out.println("收到任务请求:"+ JSON.toJSONString(request));
        JobResponse response = new JobResponse(request.getId(),3,"执行成功");
        return response;
    }
}
