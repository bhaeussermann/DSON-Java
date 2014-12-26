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

package org.dogeon.dson.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.dogeon.dson.Shibe;
import org.junit.Test;

public class TestSpeak
{
    @Test
    public void testSimpleValues()
    {
        assertSpeak("\"some\\n\\\"text\\\"\"", "some\n\"text\"");
        assertSpeak("\"a\"", 'a');
        assertSpeak("yes", true);
        assertSpeak("no", false);
        
        assertSpeak("42", 34);
        assertSpeak("-15", -13);
        assertSpeak("42.0", 34d);
        assertSpeak("15.3", 13.375);
        assertSpeak("-15.3", -13.375);
        assertSpeak("3.0 very 13", 0.375 * Math.pow(8, 12));
        assertSpeak("3.1 very 13", 0.390625 * Math.pow(8, 12));
        assertSpeak("-3.1 very 13", -0.390625 * Math.pow(8, 12));
        assertSpeak("3.1 very -15", 0.390625 * Math.pow(8, -12));
        
        GregorianCalendar calendar = new GregorianCalendar(2014, 11, 6, 21, 5, 1);
        assertSpeak("\"2014-12-06T21:05:01.000Z\"", calendar.getTime());
        
        assertSpeak("\"AQIDBAUGBwgJCg==\"", new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        
        assertSpeak("empty", null);
    }
    
    @Test
    public void testList()
    {
        Object[] items = new Object[] { "text", 12, false, null };
        assertSpeak("so \"text\" also 14 and no and empty many", items);
        
        ArrayList<Object> list = new ArrayList<Object>();
        for (Object nextItem : items)
            list.add(nextItem);
        assertSpeak("so \"text\" also 14 and no and empty many", list);
    }
    
    @Test
    public void testThing()
    {
        assertSpeak("such \"info\" is such \"and\" is \"also\",\"doge\" is yes.\"many\" is \"wow\"!\"shiba\" is \"inu\"?\"so\" is \"many\",\"such\" is empty wow wow", new Animal(new AnimalInfo("inu", true)));
        
        HashMap<String, Object> inner = new HashMap<String, Object>();
        inner.put("shiba", "inu");
        inner.put("doge", true);
        inner.put("such", null);
        HashMap<String, Object> thing = new HashMap<String, Object>();
        thing.put("info", inner);
        assertSpeak("such \"info\" is such \"doge\" is yes,\"shiba\" is \"inu\".\"such\" is empty wow wow", thing);
    }
    
    
    private void assertSpeak(String expected, Object value)
    {
        String s = Shibe.speak(value);
        assertEquals(expected, s);
    }
    
    
    public static class Animal
    {
        private AnimalInfo info;
        
        public Animal(AnimalInfo info)
        {
            this.info = info;
        }
        
        public AnimalInfo getInfo()
        {
            return info;
        }
    }
    
    public static class AnimalInfo
    {
        private String shiba;
        private boolean doge;
        
        public AnimalInfo(String shiba, boolean doge)
        {
            this.shiba = shiba;
            this.doge = doge;
        }
        
        public String getShiba()
        {
            return shiba;
        }
        
        public boolean isDoge()
        {
            return doge;
        }
        
        public String getSuch()
        {
            return null;
        }
        
        public String getSo()
        {
            return "many";
        }
        
        public String getAnd()
        {
            return "also";
        }
        
        public String getMany()
        {
            return "wow";
        }
    }
}
