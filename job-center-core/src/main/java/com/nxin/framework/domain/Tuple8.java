package com.nxin.framework.domain;

/**
 * Created by petzold on 2015/10/26.
 */
public class Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> extends Tuple7<T1,T2,T3,T4,T5,T6,T7>
{
    private T8 t8;
    public Tuple8()
    {
    }

    public Tuple8(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7)
    {
        super(t1, t2, t3, t4, t5, t6, t7);
        this.t8 = t8;
    }

    public T8 getT8()
    {
        return t8;
    }

    public void setT8(T8 t8)
    {
        this.t8 = t8;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean eq = super.equals(o);
        if(eq)
        {
            Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> tuple = (Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>)o;
            if (t8 != null ? !t8.equals(tuple.t8) : tuple.t8 != null)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
