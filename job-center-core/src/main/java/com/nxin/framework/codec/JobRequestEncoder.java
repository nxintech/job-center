package com.nxin.framework.codec;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.nxin.framework.message.JobRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by petzold on 2015/12/15.
 */
public class JobRequestEncoder extends MessageToByteEncoder<JobRequest>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, JobRequest request, ByteBuf out) throws Exception
    {
        byte[] id = getBytes(request.getId());
        byte[] name = getBytes(request.getJobName());
        byte[] extra = getBytes(request.getExtra());
        int len = 13 + id.length + name.length + extra.length;
        if(request.isSharding())
        {
            len += 4 + 4*request.getShardingItems().size();
        }
        out.writeInt(len);
        out.writeInt(id.length);
        out.writeInt(name.length);
        out.writeInt(extra.length);
        out.writeByte(request.isSharding()?1:0);
        out.writeBytes(id,0,id.length);
        out.writeBytes(name,0,name.length);
        if(request.isSharding())
        {
            out.writeInt(request.getShardingItems().size());
            for (int i : request.getShardingItems())
            {
                out.writeInt(i);
            }
        }
        if(extra.length > 0)
        {
            out.writeBytes(extra, 0, extra.length);
        }
    }

    private byte[] getBytes(String str)
    {
        if(Strings.isNullOrEmpty(str))
        {
            return new byte[0];
        }
        return str.getBytes(Charsets.UTF_8);
    }
}
