package org.dogeon.dson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.dogeon.dson.makesense.BuilderVisitor;
import org.dogeon.dson.makesense.InstantiatorVisitor;
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
    
    public static Object makeSense(String dson) throws MakeSenseException
    {
    	try 
    	{
			return makeSense(new StringReader(dson));
		} 
    	catch (IOException e) {}
    	return null;
    }
    
    public static Object makeSense(Reader reader) throws IOException, MakeSenseException
    {
    	BuilderVisitor builder = new BuilderVisitor();
    	WordParser parser = new WordParser(reader);
    	parser.parse(builder);
    	return builder.getBuiltValue();
    }
    
    public static Object makeSense(String dson, Class<?> c) throws MakeSenseException
    {
        return makeSense(dson, c, null);
    }
    
    public static Object makeSense(String dson, Class<?> c, Class<?> componentClass) throws MakeSenseException
    {
    	try 
    	{
			return makeSense(new StringReader(dson), c, componentClass);
		} 
    	catch (IOException e) {}
    	return null;
    }
    
    public static Object makeSense(Reader reader, Class<?> c) throws IOException, MakeSenseException
    {
        return makeSense(reader, c, null);
    }
    
    public static Object makeSense(Reader reader, Class<?> c, Class<?> componentClass) throws IOException, MakeSenseException
    {
    	InstantiatorVisitor builder = new InstantiatorVisitor(c, componentClass);
    	WordParser parser = new WordParser(reader);
    	parser.parse(builder);
    	return builder.getBuiltValue();
    }
}
