package com.nxin.framework.domain;

/**
 * Created by petzold on 2014/11/19.
 */
public class Tuple4<T1,T2,T3,T4> extends Tuple3<T1,T2,T3>
{
    private T4 t4;

    public Tuple4()
    {
    }

    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4)
    {
        super(t1, t2, t3);
        this.t4 = t4;
    }

    public T4 getT4()
    {
        return t4;
    }

    public void setT4(T4 t4)
    {
        this.t4 = t4;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean eq = super.equals(o);
        if(eq)
        {
            Tuple4<T1,T2,T3,T4> tuple = (Tuple4<T1,T2,T3,T4>)o;
            if (t4 != null ? !t4.equals(tuple.t4) : tuple.t4 != null)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
