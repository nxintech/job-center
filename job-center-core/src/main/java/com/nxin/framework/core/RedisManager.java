package com.nxin.framework.core;

import com.google.common.collect.Maps;
import com.nxin.framework.functions.Func1;
import com.nxin.framework.functions.Func2;
import com.nxin.framework.loader.IResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by petzold on 2015/1/27.
 */

public class RedisManager
{
    private JedisPool pool;
    private Logger logger = LoggerFactory.getLogger(RedisManager.class);
    private Map<String,String> hashaMap = Maps.newHashMap();
    private IResourceLoader resourceLoader;

    public String get(String ns, String key)
    {
        try(Jedis jedis=pool.getResource())
        {
            return jedis.get(String.format("%s:%s", ns, key));
        }
        catch (Exception e)
        {
            logger.error(String.format("请求数据失败(ns=%s,key=%s)",ns,key),e);
            return null;
        }
    }

    public <T,R> R get(Func2<Jedis,T,R> fun,T t)
    {
        try(Jedis jedis=pool.getResource())
        {
            return fun.call(jedis, t);
        }
        catch (Exception e)
        {
            logger.error("请求数据失败",e);
            return null;
        }
    }

    public <T> T runScript(Func1<Object, T> transform, String name, List<String> keys, List<String> vals)
    {
        try(Jedis jedis=pool.getResource())
        {
            Object o = executeScript(jedis, name, keys, vals);
            if (o != null)
            {
                return transform.call(o);
            }
            return null;
        }
        catch (Exception e)
        {
            logger.error(String.format("执行脚本[%s]失败",name),e);
            return null;
        }
    }

    public String getSha(Jedis jedis,String name) throws IOException
    {
        if (!hashaMap.containsKey(name))
        {
            String sha = jedis.hget("scripts", name);
            if (sha != null)
            {
                hashaMap.put(name,sha);
            }
            else
            {
                String data = resourceLoader.getContent(name + ".lua");
                sha = jedis.scriptLoad(data);
                jedis.hset("scripts",name,sha);
                hashaMap.put(name,sha);
            }
        }
        return hashaMap.get(name);
    }

    public Object executeScript(Jedis jedis,String scriptName,List<String> keys,List<String> args)
    {
        try
        {
            String sha = getSha(jedis,scriptName);
            return jedis.evalsha(sha, keys, args);
        }
        catch (JedisDataException e)
        {
            if(isWrongSha(e))
            {
                return execCmd(jedis,scriptName,keys,args);
            }
            else
            {
                logger.error(String.format("执行脚本[%s]时出错",scriptName),e);
                return null;
            }
        }
        catch (Exception e)
        {
            logger.error(String.format("执行脚本[%s]时出错",scriptName),e);
            return null;
        }
    }

    private boolean isWrongSha(JedisDataException e)
    {
        return e.getMessage().equals("NOSCRIPT No matching script. Please use EVAL.");
    }
    private Object execCmd(Jedis jedis,String scriptName,List<String> keys,List<String> args)
    {
        try
        {
            String data = resourceLoader.getContent(scriptName + ".lua");
            String sha = jedis.scriptLoad(data);
            jedis.hset("scripts", scriptName, sha);
            hashaMap.put(scriptName,sha);
            return jedis.evalsha(sha,keys,args);
        }
        catch (Exception e)
        {
            logger.error("执行脚本出错",e);
            return null;
        }
    }
    public void setPool(JedisPool pool)
    {
        this.pool = pool;
    }
    public void setResourceLoader(IResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }
}
