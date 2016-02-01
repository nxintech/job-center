package com.nxin.framework.loader;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by petzold on 2015/10/26.
 */
public interface IResourceLoader
{
    String getContent(String name);
    String getContent(String name, Charset charset);
    File getFile(String name);
}
