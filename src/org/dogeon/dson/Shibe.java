//Copyright (c) 2014 Bernhard Haeussermann
//
//Permission is hereby granted, free of charge, to any person
//obtaining a copy of this software and associated documentation
//files (the "Software"), to deal in the Software without
//restriction, including without limitation the rights to use,
//copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the
//Software is furnished to do so, subject to the following
//conditions:
//
//The above copyright notice and this permission notice shall be
//included in all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//OTHER DEALINGS IN THE SOFTWARE.

package org.dogeon.dson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.dogeon.dson.makesense.BuilderVisitor;
import org.dogeon.dson.makesense.InstantiatorVisitor;
import org.dogeon.dson.makesense.MakeSenseException;
import org.dogeon.dson.makesense.WordParser;
import org.dogeon.dson.speaking.SpeakException;
import org.dogeon.dson.speaking.VerySpeakWow;
import org.dogeon.dson.util.ThingUtil;
import org.dogeon.dson.util.VisitationException;

public class Shibe
{
    public static String speak(Object obj)
    {
        StringWriter writer = new StringWriter();
        try
        {
            speak(obj, writer);
        } 
        catch (SpeakException e)
        {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
    
    public static void speak(Object obj, Writer writer) throws SpeakException
    {
        VerySpeakWow speak = new VerySpeakWow(writer);
        try
        {
            ThingUtil.walkThing(obj, speak);
        } 
        catch (VisitationException e)
        {
            throw new SpeakException(e);
        }
    }
    
    public static Object makeSense(String dson)
    {
    	try 
    	{
			return makeSense(new StringReader(dson));
		} 
    	catch (IOException e) {}
    	catch (MakeSenseException e)
    	{
    	    throw new RuntimeException(e);
    	}
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
