package com.nxin.framework.loader;

import java.io.File;
import java.net.URL;

/**
 * Created by petzold on 2015/10/26.
 */
public class ClasspathResourceLoader extends AbstractResourceLoader
{
    private String root;
    @Override
    public File getFile(String name)
    {
        String path = concat(root, name);
        if(path.startsWith("/"))
        {
            path = path.substring(1);
        }
        URL url = getClassLoader().getResource(path);
        File file = new File(url.getPath());
        if(!file.exists())
        {
            logger.warn("文件【{}】不存在", path);
            return null;
        }
        return file;
    }

    private ClassLoader getClassLoader()
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(loader == null)
        {
            loader = ClasspathResourceLoader.class.getClassLoader();
        }
        if(loader == null)
        {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }

    public void setRoot(String root)
    {
        this.root = root;
    }
}
