package com.nxin.framework.core;

import com.nxin.framework.codec.JobRequestEncoder;

/**
 * Created by petzold on 2016/1/22.
 */
public class JobRequestRpcHelper extends RpcHelper<JobRequestEncoder>
{
    @Override
    protected JobRequestEncoder newEncoder()
    {
        return new JobRequestEncoder();
    }
}
