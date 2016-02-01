package com.nxin.framework.domain;

/**
 * Created by petzold on 2014/11/19.
 */
public class Tuple5<T1,T2,T3,T4,T5> extends Tuple4<T1,T2,T3,T4>
{
    private T5 t5;

    public Tuple5()
    {

    }

    public Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5)
    {
        super(t1, t2, t3, t4);
        this.t5 = t5;
    }

    public T5 getT5()
    {
        return t5;
    }

    public void setT5(T5 t5)
    {
        this.t5 = t5;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean eq = super.equals(o);
        if(eq)
        {
            Tuple5<T1,T2,T3,T4,T5> tuple = (Tuple5<T1,T2,T3,T4,T5>)o;
            if (t5 != null ? !t5.equals(tuple.t5) : tuple.t5 != null)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
