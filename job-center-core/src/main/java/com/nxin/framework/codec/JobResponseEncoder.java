package com.nxin.framework.codec;

import com.google.common.base.Charsets;
import com.nxin.framework.message.JobResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by petzold on 2015/12/15.
 */
public class JobResponseEncoder extends MessageToByteEncoder<JobResponse>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, JobResponse response, ByteBuf out) throws Exception
    {
        byte[] id = response.getId().getBytes(Charsets.UTF_8);
        byte[] message = getDefByte(response.getError());
        int len = 8 + id.length;
        if(response.getStatus() != 0)
        {
            len += 4 + message.length;
        }
        out.writeInt(len);
        out.writeInt(id.length);
        out.writeInt(response.getStatus());
        out.writeBytes(id,0,id.length);
        if(response.getStatus() != 0)
        {
            out.writeInt(message.length);
            out.writeBytes(message,0,message.length);
        }
    }
    private byte[] getDefByte(String str)
    {
        if (str != null)
        {
            return str.getBytes(Charsets.UTF_8);
        }
        return new byte[0];
    }
}
