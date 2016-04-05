package com.nxin.framework.loader;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;

/**
 * Created by petzold on 2015/10/26.
 */
public abstract class AbstractResourceLoader implements IResourceLoader
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected String normalize(final String originPath)
    {
        String path = originPath;
        if (path == null)
        {
            return null;
        }
        if (path.indexOf('\\') != -1)
        {
            path = path.replace('\\', '/');
        }
        while (true)
        {
            int pos = path.indexOf("./");
            if (pos == 0)
            {
                path = path.substring(2);
                continue;
            }
            if (pos == -1 && !path.contains("//"))
            {
                return path;
            }
            break;
        }
        boolean absolute = path.startsWith("/");
        boolean dir = path.endsWith("/");
        String[] elements = path.split("/");
        LinkedList<String> list = new LinkedList<String>();
        for (String e : elements)
        {
            if ("..".equals(e))
            {
                if (list.isEmpty() || "..".equals(list.getLast()))
                {
                    list.add(e);
                } else
                {
                    list.removeLast();
                }
            } else if (!".".equals(e) && !e.isEmpty())
            {
                list.add(e);
            }
        }
        StringBuilder sb = new StringBuilder(path.length());
        if (absolute)
        {
            sb.append('/');
        }
        int count = 0, last = list.size() - 1;
        for (String e : list)
        {
            sb.append(e);
            if (count++ < last)
            {
                sb.append('/');
            }
        }
        if (dir && list.size() > 0)
        {
            sb.append('/');
        }
        path = sb.toString();
        if (path.startsWith("/.."))
        {
            throw new IllegalStateException("invalid path: " + originPath);
        }
        return path;
    }

    protected String concat(String parent, String child)
    {
        if (parent == null)
        {
            return normalize(child);
        }
        if (child == null)
        {
            return normalize(parent);
        }
        return normalize(parent + '/' + child);
    }

    public abstract File getFile(String name);

    public String getContent(String name)
    {
        return getContent(name, Charsets.UTF_8);
    }

    public String getContent(String name, Charset charset)
    {
        try
        {
            File file = getFile(name);
            if(file != null)
            {
                return Files.toString(file,charset);
            }
            return null;
        } catch (IOException e)
        {
            logger.error(String.format("打开文件【%s】失败", name), e);
            return null;
        }
    }
}
