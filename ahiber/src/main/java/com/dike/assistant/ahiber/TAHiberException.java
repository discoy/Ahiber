package com.dike.assistant.ahiber;

public class TAHiberException extends Exception
{

    private static final long serialVersionUID = 1L;

    public TAHiberException(String detailMessage)
    {
        super(detailMessage);
    }

    public TAHiberException()
    {
        super();
    }

    public TAHiberException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public TAHiberException(Throwable throwable)
    {
        super(throwable);
    }


}
