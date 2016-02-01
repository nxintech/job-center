package com.nxin.framework.domain;

/**
 * Created by petzold on 2014/11/19.
 */
public class Tuple6<T1,T2,T3,T4,T5,T6> extends Tuple5<T1,T2,T3,T4,T5>
{
    private T6 t6;

    public Tuple6()
    {

    }

    public Tuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6)
    {
        super(t1, t2, t3, t4, t5);
        this.t6 = t6;
    }

    public T6 getT6()
    {
        return t6;
    }

    public void setT6(T6 t6)
    {
        this.t6 = t6;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean eq = super.equals(o);
        if(eq)
        {
            Tuple6<T1,T2,T3,T4,T5,T6> tuple = (Tuple6<T1,T2,T3,T4,T5,T6>)o;
            if (t6 != null ? !t6.equals(tuple.t6) : tuple.t6 != null)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
