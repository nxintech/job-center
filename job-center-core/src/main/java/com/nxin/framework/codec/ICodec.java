package com.nxin.framework.codec;

/**
 * Created by petzold on 2015/10/22.
 */
public interface ICodec
{
    <T> byte[] encode(T obj);
    <T> T decode(byte[] data, Class<T> clazz);
}
