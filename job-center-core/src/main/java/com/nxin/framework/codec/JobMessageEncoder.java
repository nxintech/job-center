package com.nxin.framework.codec;

import com.google.common.base.Charsets;
import com.nxin.codec.ICodec;
import com.nxin.framework.message.JobMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by petzold on 2015/12/29.
 */
public class JobMessageEncoder extends MessageToByteEncoder<JobMessage>
{
    private ICodec codec;

    public JobMessageEncoder(ICodec codec)
    {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, JobMessage msg, ByteBuf out) throws Exception
    {
        byte[] type = msg.getMessageType().getBytes(Charsets.UTF_8);
        byte[] message = codec.encode(msg.getMessage());
        out.writeInt(12 + type.length + message.length);
        out.writeInt(msg.getType());
        out.writeInt(type.length);
        out.writeInt(message.length);
        out.writeBytes(type, 0, type.length);
        out.writeBytes(message, 0 ,message.length);
    }
}
