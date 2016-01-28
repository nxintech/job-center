package com.nxin.framework.core;

import com.nxin.codec.ICodec;
import com.nxin.framework.codec.JobMessageEncoder;

/**
 * Created by petzold on 2016/1/22.
 */
public class JobMessageRpcHelper extends RpcHelper<JobMessageEncoder>
{
    private ICodec codec;
    @Override
    protected JobMessageEncoder newEncoder()
    {
        return new JobMessageEncoder(codec);
    }

    public void setCodec(ICodec codec)
    {
        this.codec = codec;
    }
}
