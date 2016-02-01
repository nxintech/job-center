package com.nxin.framework.domain;

/**
 * Created by petzold on 2014/11/19.
 */
public class Tuple3<T1,T2,T3> extends Tuple2<T1,T2>
{
    private T3 t3;

    public Tuple3()
    {

    }

    public Tuple3(T1 t1, T2 t2, T3 t3)
    {
        super(t1, t2);
        this.t3 = t3;
    }

    public T3 getT3()
    {
        return t3;
    }

    public void setT3(T3 t3)
    {
        this.t3 = t3;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean eq = super.equals(o);
        if(eq)
        {
            Tuple3<T1,T2,T3> tuple = (Tuple3<T1,T2,T3>)o;
            if (t3 != null ? !t3.equals(tuple.t3) : tuple.t3 != null)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
