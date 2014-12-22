package org.dogeon.dson.makesense;

public class MakeSenseException extends Exception
{
    private static final long serialVersionUID = 7708700000656819232L;

    public MakeSenseException(String message)
    {
        super(message);
    }
    
    public MakeSenseException(Exception inner)
    {
    	super(inner);
    }
}
