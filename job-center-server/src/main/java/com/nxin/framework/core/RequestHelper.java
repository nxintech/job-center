package com.nxin.framework.core;

import com.google.common.base.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        return req(url, new StringEntity(data,Charsets.UTF_8));
    }
    public String req(String url,byte[] data)
    {
        return req(url, new ByteArrayEntity(data));
    }
    public String req(String url,Map<String,String> parameter)
    {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>(parameter.size());
        parameter.forEach((k, v) -> pairs.add(new BasicNameValuePair(k, v)));
        return req(url, new UrlEncodedFormEntity(pairs,Charsets.UTF_8));
    }
}
