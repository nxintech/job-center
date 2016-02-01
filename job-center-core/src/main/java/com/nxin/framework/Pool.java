package com.nxin.framework;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;

/**
 * Created by petzold on 2015-05-03.
 */
public class Pool<T> implements Closeable
{
    private Logger logger = LoggerFactory.getLogger(Pool.class);
    private GenericObjectPool<T> pool;

    @Override
    public void close()
    {
        closeInternalPool();
    }

    public boolean isClosed()
    {
        return this.pool.isClosed();
    }

    public Pool(GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory)
    {
        this.pool = new GenericObjectPool<T>(factory, poolConfig);
    }

    public T getResource()
    {
        try
        {
            return pool.borrowObject();
        } catch (Exception e)
        {
            logger.error("从缓存池获取资源失败",e);
            return null;
        }
    }

    public void returnResourceObject(T resource)
    {
        if (resource == null)
        {
            return;
        }
        try
        {
            pool.returnObject(resource);
        } catch (Exception e)
        {
            logger.error("无法归还资源到缓存池",e);
        }
    }

    public void destroy()
    {
        closeInternalPool();
    }

    protected void returnBrokenResourceObject(T resource)
    {
        try
        {
            pool.invalidateObject(resource);
        } catch (Exception e)
        {
            logger.error("无法归还资源到缓存池",e);
        }
    }

    protected void closeInternalPool()
    {
        try
        {
            pool.close();
        } catch (Exception e)
        {
            logger.error("无法销毁到缓存池",e);
        }
    }
}
