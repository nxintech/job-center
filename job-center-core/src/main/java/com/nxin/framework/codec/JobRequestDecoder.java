package com.nxin.framework.codec;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.nxin.framework.message.JobRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by petzold on 2015/12/15.
 */
public class JobRequestDecoder extends ByteToMessageDecoder
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
        int idLength = in.readInt();
        int nameLength = in.readInt();
        int extraLength = in.readInt();
        boolean sharding = ((int)in.readByte()) == 1;
        String id = readString(in, idLength);
        String name = readString(in, nameLength);
        if(sharding)
        {
            int sz = in.readInt();
            List<Integer> items = Lists.newArrayListWithCapacity(sz);
            for (int i=0;i<sz;i++)
            {
                items.add(in.readInt());
            }
            if(extraLength > 0)
            {
                out.add(new JobRequest(id,name,sharding,items,readString(in,extraLength)));
            }
            else
            {
                out.add(new JobRequest(id,name,sharding,items,null));
            }
        }
        else
        {
            if(extraLength > 0)
            {
                out.add(new JobRequest(id,name,sharding,null,readString(in,extraLength)));
            }
            else
            {
                out.add(new JobRequest(id,name,sharding,null,null));
            }
        }
    }
    private String readString(ByteBuf in,int length)
    {
        byte[] data = new byte[length];
        in.readBytes(data);
        return new String(data, Charsets.UTF_8);
    }
}
