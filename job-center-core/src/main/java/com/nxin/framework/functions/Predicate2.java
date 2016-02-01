package com.nxin.framework.functions;

/**
 * Created by petzold on 2015/11/2.
 */
public interface Predicate2<T1, T2> extends Predicate
{
    boolean accept(T1 t1, T2 t2);
}
