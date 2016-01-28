package com.nxin.framework.codec;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nxin.codec.ICodec;
import com.nxin.framework.message.JobMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by petzold on 2015/12/29.
 */
public class JobMessageDecoder extends ByteToMessageDecoder
{
    private ICodec codec;
    private LoadingCache<String, Class> cache = CacheBuilder.newBuilder().softValues().build(new CacheLoader<String, Class>()
    {
        @Override
        public Class load(String key) throws Exception
        {
            return Class.forName(key);
        }
    });

    public JobMessageDecoder(ICodec codec)
    {
        this.codec = codec;
    }

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
        int type = in.readInt();
        int typeLen = in.readInt();
        int len = in.readInt();
        String responseType = readString(in, typeLen);
        Object msg = codec.decode(readData(in, len), cache.get(responseType));
        JobMessage message = new JobMessage();
        message.setType(type);
        message.setMessageType(responseType);
        message.setMessage(msg);
        out.add(message);
    }
    private byte[] readData(ByteBuf in,int length)
    {
        byte[] data = new byte[length];
        in.readBytes(data);
        return data;
    }
    private String readString(ByteBuf in,int length)
    {
        return new String(readData(in, length), Charsets.UTF_8);
    }
}
