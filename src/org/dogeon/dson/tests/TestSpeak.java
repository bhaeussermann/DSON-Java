package org.dogeon.dson.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.dogeon.dson.Shibe;
import org.dogeon.dson.model.DogeList;
import org.dogeon.dson.model.DogeThing;
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
        assertSpeak("15.3", 13.375);
        assertSpeak("-15.3", -13.375);
        GregorianCalendar calendar = new GregorianCalendar(2014, 11, 6, 21, 5, 1);
        assertSpeak("\"2014-12-06T21:05:01.000Z\"", calendar.getTime());
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
        assertSpeak("such \"info\" is such \"and\" is \"also\",\"doge\" is yes.\"many\" is \"wow\"!\"shiba\" is \"inu\"?\"so\" is \"many\",\"such\" is \"is\" wow wow", new Animal(new AnimalInfo("inu", true)));
        
        HashMap<String, Object> inner = new HashMap<String, Object>();
        inner.put("shiba", "inu");
        inner.put("doge", true);
        HashMap<String, Object> thing = new HashMap<String, Object>();
        thing.put("info", inner);
        assertSpeak("such \"info\" is such \"doge\" is yes,\"shiba\" is \"inu\" wow wow", thing);
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
            return "is";
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
