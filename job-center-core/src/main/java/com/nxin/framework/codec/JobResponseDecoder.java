package com.nxin.framework.codec;

import com.google.common.base.Charsets;
import com.nxin.framework.message.JobResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by petzold on 2015/12/15.
 */
public class JobResponseDecoder extends ByteToMessageDecoder
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        if(in.readableBytes() < 12)
        {
            return;
        }
        in.markReaderIndex();
        int capacity = in.readInt();
        if(capacity < 0)
        {
            ctx.close();
        }
        if(in.readableBytes() < capacity)
        {
            in.resetReaderIndex();
            return;
        }
        int idLen = in.readInt();
        int status = in.readInt();
        String id = readString(in, idLen);
        if(status != 0)
        {
            int errLen = in.readInt();
            String err = readString(in, errLen);
            out.add(new JobResponse(id,status,err));
        }
        else
        {
            out.add(new JobResponse(id,status,null));
        }
    }
    private String readString(ByteBuf in,int length)
    {
        byte[] data = new byte[length];
        in.readBytes(data);
        return new String(data, Charsets.UTF_8);
    }
}
