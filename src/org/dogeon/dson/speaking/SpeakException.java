package org.dogeon.dson.speaking;

public class SpeakException extends Exception
{
    private static final long serialVersionUID = 3699195476133266723L;

    public SpeakException(String message)
    {
        super(message);
    }
    
    public SpeakException(Exception innerException)
    {
        super(innerException);
    }
}
