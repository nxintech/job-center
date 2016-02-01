package com.nxin.framework.codec;

import com.google.common.collect.Maps;
import com.nxin.framework.Pool;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;

/**
 * Created by petzold on 2015/10/22.
 */
public class ProtoBufferCodec implements ICodec
{
    private Map<Class<?>,Schema<?>> cachedSchema = Maps.newConcurrentMap();
    private Objenesis objenesis = new ObjenesisStd(true);
    private Pool<LinkedBuffer> pool;
    public ProtoBufferCodec()
    {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(Runtime.getRuntime().availableProcessors() * 2);
        pool = new Pool<LinkedBuffer>(config, new BasePooledObjectFactory<LinkedBuffer>()
        {
            @Override
            public LinkedBuffer create() throws Exception
            {
                return LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
            }

            @Override
            public PooledObject<LinkedBuffer> wrap(LinkedBuffer obj)
            {
                return new DefaultPooledObject<LinkedBuffer>(obj);
            }
        });
    }
    private <T> Schema<T> getSchema(Class<T> cls)
    {
        Schema<T> schema = (Schema<T>)cachedSchema.get(cls);
        if(schema == null)
        {
            schema = RuntimeSchema.createFrom(cls);
            if(schema != null)
            {
                cachedSchema.put(cls,schema);
            }
        }
        return schema;
    }

    @Override
    public <T> byte[] encode(T t)
    {
        Class<T> clazz = (Class<T>)t.getClass();
        Schema<T> schema = getSchema(clazz);
        LinkedBuffer buffer = pool.getResource();
        byte[] r = ProtobufIOUtil.toByteArray(t, schema, buffer);
        buffer.clear();
        pool.returnResourceObject(buffer);
        return r;
    }

    @Override
    public <T> T decode(byte[] data, Class<T> clazz)
    {
        T message = objenesis.newInstance(clazz);
        Schema<T> schema = getSchema(clazz);
        ProtobufIOUtil.mergeFrom(data,message,schema);
        return message;
    }
}
