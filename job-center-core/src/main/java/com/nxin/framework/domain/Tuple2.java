package com.nxin.framework.domain;

/**
 * Created by petzold on 2014/11/19.
 */
public class Tuple2<T1,T2>
{
    private T1 t1;
    private T2 t2;

    public Tuple2()
    {
    }

    public Tuple2(T1 t1, T2 t2)
    {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1()
    {
        return t1;
    }

    public void setT1(T1 t1)
    {
        this.t1 = t1;
    }

    public T2 getT2()
    {
        return t2;
    }

    public void setT2(T2 t2)
    {
        this.t2 = t2;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }
        Tuple2<T1,T2> tuple = (Tuple2<T1,T2>)o;
        if (t1 != null ? !t1.equals(tuple.t1) : tuple.t1 != null)
        {
            return false;
        }
        if (t2 != null ? !t2.equals(tuple.t2) : tuple.t2 != null)
        {
            return false;
        }
        return true;
    }
}
