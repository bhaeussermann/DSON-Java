package org.dogeon.dson.makesense;

import java.io.IOException;
import java.io.Reader;

public class WordParser
{
    private VeryWordFind wordFinder;
    
    public WordParser(Reader reader) throws IOException, MakeSenseException
    {
        wordFinder = new VeryWordFind(reader);
    }
    
    public WordParser(String dson) throws IOException, MakeSenseException
    {
        wordFinder = new VeryWordFind(dson);
    }
    
    public void parse(ThingVisitor visitor) throws IOException, MakeSenseException
    {
        parseDson(visitor);
    }
    
    private void parseDson(ThingVisitor visitor) throws IOException, MakeSenseException
    {
        switch (wordFinder.peekWord().getWordType())
        {
            
        }
    }
}
