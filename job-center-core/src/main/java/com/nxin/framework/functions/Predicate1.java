package com.nxin.framework.functions;

/**
 * Created by petzold on 2015/11/2.
 */
public interface Predicate1<T> extends Predicate
{
    boolean accept(T t);
}
