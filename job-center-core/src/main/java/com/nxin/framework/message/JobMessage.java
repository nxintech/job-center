package com.nxin.framework.message;

/**
 * Created by petzold on 2015/12/29.
 */
public class JobMessage
{
    private int type;
    private String messageType;
    private Object message;

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public Object getMessage()
    {
        return message;
    }

    public void setMessage(Object message)
    {
        this.message = message;
    }
}
