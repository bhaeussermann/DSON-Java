package org.dogeon.dson.util;

public class VisitationException extends Exception
{
    private static final long serialVersionUID = -8513566489473947430L;

    public VisitationException(String message)
    {
        super(message);
    }
    
    public VisitationException(Exception innerException)
    {
        super(innerException);
    }
}
