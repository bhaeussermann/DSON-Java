package org.dogeon.dson.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

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
        assertSpeak("15", 13);
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
    
    
    private void assertSpeak(String expected, Object value)
    {
        String s = Shibe.speak(value);
        assertEquals(expected, s);
    }
}
