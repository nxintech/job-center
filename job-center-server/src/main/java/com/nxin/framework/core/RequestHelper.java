package com.nxin.framework.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.nxin.collection.MapAdapter;
import com.nxin.functions.Func2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by petzold on 2014/8/16.
 */
public class RequestHelper
{
    private HttpClientBuilder builder;
    private Logger logger = LoggerFactory.getLogger(getClass());
    public RequestHelper(HttpClientConnectionManager connectionManager)
    {
        this.builder = HttpClients.custom().setConnectionManager(connectionManager);
    }

    public String req(HttpRequestBase request)
    {
        CloseableHttpClient httpClient = builder.build();
        try
        {
            return httpClient.execute(request,(HttpResponse resp)->
            {
                if (resp.getStatusLine().getStatusCode() == 200)
                {
                    HttpEntity entity = resp.getEntity();
                    String content = EntityUtils.toString(entity, Charsets.UTF_8);
                    EntityUtils.consume(entity);
                    return content;
                }
                else
                {
                    HttpEntity entity = resp.getEntity();
                    String content = EntityUtils.toString(entity, Charsets.UTF_8);
                    EntityUtils.consume(entity);
                    logger.error(String.format("WEB请求失败:URL[%s],Method[%s],状态[%s],返回[%s]",request.getURI(),request.getMethod(),resp.getStatusLine().getStatusCode(),content));
                    return null;
                }
            });
        }
        catch (IOException e)
        {
            logger.error(String.format("WEB请求失败:URL[%s],Method[%s]",request.getURI(),request.getMethod()),e);
            return null;
        }
    }
    public String req(String url)
    {
        HttpGet httpGet = new HttpGet(url);
        return req(httpGet);
    }
    public String req(String url,HttpEntity entity)
    {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        return req(httpPost);
    }
    public String req(String url,String data)
    {
        HttpEntity entity = EntityBuilder.create().setContentEncoding("UTF-8").setText(data).build();
        return req(url,entity);
    }
    public String req(String url,byte[] data)
    {
        HttpEntity httpEntity = EntityBuilder.create().setBinary(data).build();
        return req(url,httpEntity);
    }
    public String req(String url,InputStream stream)
    {
        HttpEntity httpEntity = EntityBuilder.create().setStream(stream).build();
        return req(url,httpEntity);
    }
    public String req(String url,File file)
    {
        HttpEntity httpEntity = EntityBuilder.create().setFile(file).build();
        return req(url,httpEntity);
    }
    public String req(String url,Map<String,String> parameter)
    {
        EntityBuilder entityBuilder = EntityBuilder.create().setContentEncoding("UTF-8");
        entityBuilder.setParameters(MapAdapter.transform(new Func2<String, String, NameValuePair>()
        {
            @Override
            public NameValuePair call(String key, String val)
            {
                return new BasicNameValuePair(key, val);
            }
        }, parameter));
        HttpEntity httpEntity = entityBuilder.build();
        return req(url,httpEntity);
    }
    public JSONObject reqJSON(String url)
    {
        String data = req(url);
        return JSON.parseObject(data);
    }
    public JSONObject reqJSON(String url,HttpEntity entity)
    {
        String data = req(url, entity);
        return JSON.parseObject(data);
    }
    public JSONObject reqJSON(String url,String data)
    {
        String ret = req(url,data);
        return JSON.parseObject(ret);
    }
    public JSONObject reqJSON(String url,byte[] data)
    {
        String ret = req(url,data);
        return JSON.parseObject(ret);
    }
    public JSONObject reqJSON(String url,InputStream stream)
    {
        String data = req(url,stream);
        return JSON.parseObject(data);
    }
    public JSONObject reqJSON(String url,File file)
    {
        String data = req(url,file);
        return JSON.parseObject(data);
    }
    public JSONObject reqJSON(String url,Map<String,String> parameter)
    {
        String data = req(url,parameter);
        return JSON.parseObject(data);
    }
}
