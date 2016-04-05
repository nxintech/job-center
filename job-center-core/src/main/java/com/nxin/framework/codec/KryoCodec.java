package com.nxin.framework.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by petzold on 2015/10/22.
 */
public class KryoCodec implements ICodec
{
    private KryoPool pool;
    private List<String> clss;
    private Logger logger = LoggerFactory.getLogger(KryoCodec.class);

    public void startUp()
    {
        pool = new KryoPool.Builder(new KryoFactory()
        {
            @Override
            public Kryo create()
            {
                Kryo kryo = new Kryo();
                if(clss != null && clss.size() > 0)
                {
                    for (int i=0;i<clss.size();i++)
                    {
                        try
                        {
                            Class clazz = Class.forName(clss.get(i));
                            kryo.register(clazz,i);
                        }
                        catch (ClassNotFoundException e)
                        {
                            logger.error("找不到类定义:"+clss.get(i),e);
                        }
                    }
                    kryo.setRegistrationRequired(true);
                }
                kryo.setReferences(false);
                return kryo;
            }
        }).softReferences().build();
    }

    @Override
    public <T> byte[] encode(T obj)
    {
        Output output = new Output(256);
        Kryo kryo = pool.borrow();
        kryo.writeObject(output,obj);
        byte[] data = output.toBytes();
        output.close();
        pool.release(kryo);
        return data;
    }

    @Override
    public <T> T decode(byte[] data, Class<T> clazz)
    {
        Kryo kryo = pool.borrow();
        T t = kryo.readObject(new Input(data),clazz);
        pool.release(kryo);
        return t;
    }

    public void setClss(List<String> clss)
    {
        this.clss = clss;
    }
}
