package org.dogeon.dson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.dogeon.dson.makesense.BuilderVisitor;
import org.dogeon.dson.makesense.MakeSenseException;
import org.dogeon.dson.makesense.WordParser;
import org.dogeon.dson.speaking.VerySpeakWow;
import org.dogeon.dson.util.ThingUtil;

public class Shibe
{
    public static String speak(Object obj)
    {
        VerySpeakWow speak = new VerySpeakWow();
        ThingUtil.walkThing(obj, speak);
        return speak.getSpeak();
    }
    
    public static Object makeSense(String dson) throws IOException, MakeSenseException
    {
    	return makeSense(new StringReader(dson));
    }
    
    public static Object makeSense(Reader reader) throws IOException, MakeSenseException
    {
    	BuilderVisitor builder = new BuilderVisitor();
    	WordParser parser = new WordParser(reader);
    	parser.parse(builder);
    	return builder.getBuiltValue();
    }
}
