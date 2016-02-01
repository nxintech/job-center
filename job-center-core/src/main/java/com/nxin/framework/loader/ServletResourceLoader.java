package com.nxin.framework.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by petzold on 2015/10/26.
 */
public class ServletResourceLoader extends AbstractResourceLoader
{
    private String root;
    private String _path;
    public void init()
    {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String webRoot = path.substring(0, path.indexOf("WEB-INF"));
        _path = concat(webRoot,root);
    }
    @Override
    public File getFile(String name)
    {
        String path = concat(_path, name);
        File file = new File(path).getAbsoluteFile();
        if(!file.exists())
        {
            logger.warn("文件【{}】不存在",path);
            return null;
        }
        return file;
    }

    public void setRoot(String root)
    {
        this.root = root;
    }
}
