package org.dogeon.dson.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.dogeon.dson.makesense.MakeSenseException;
import org.dogeon.dson.makesense.VeryWordFind;
import org.dogeon.dson.makesense.Word;
import org.dogeon.dson.makesense.Word.WordType;
import org.junit.Test;

public class TestWordFind
{
    @Test
    public void test() throws IOException, MakeSenseException
    {
        VeryWordFind finder = new VeryWordFind("such \"foo\" is  so \"bar\" also -15.3 and \"fizz - \\\"buzz\\\"\" many, \"doge\" is yes.\"many\" is 42very3 wow");
        assertNextWord(WordType.THING_BEGIN, finder);
        assertNextWord(WordType.VALUE, "foo", finder);
        assertNextWord(WordType.VALUE_SEPARATOR, finder);
        assertNextWord(WordType.LIST_BEGIN, finder);
        assertNextWord(WordType.VALUE, "bar", finder);
        assertNextWord(WordType.ITEM_SEPARATOR, finder);
        assertNextWord(WordType.VALUE, -13.375, finder);
        assertNextWord(WordType.ITEM_SEPARATOR, finder);
        assertNextWord(WordType.VALUE, "fizz - \"buzz\"", finder);
        assertNextWord(WordType.LIST_END, finder);
        assertNextWord(WordType.MEMBER_SEPARATOR, finder);
        assertNextWord(WordType.VALUE, "doge", finder);
        assertNextWord(WordType.VALUE_SEPARATOR, finder);
        assertNextWord(WordType.VALUE, true, finder);
        assertNextWord(WordType.MEMBER_SEPARATOR, finder);
        assertNextWord(WordType.VALUE, "many", finder);
        assertNextWord(WordType.VALUE_SEPARATOR, finder);
        assertNextWord(WordType.VALUE, 34l, finder);
        assertNextWord(WordType.VERY, finder);
        assertNextWord(WordType.VALUE, 3l, finder);
        assertNextWord(WordType.THING_END, finder);
        assertNull(finder.peekWord());
        assertNull(finder.nextWord());
    }
    
    private void assertNextWord(WordType expectedWordType, VeryWordFind finder) throws IOException, MakeSenseException
    {
        assertNextWord(expectedWordType, null, finder);
    }
    
    private void assertNextWord(WordType expectedWordType, Object expectedWordValue, VeryWordFind finder) throws IOException, MakeSenseException
    {
        assertWord(expectedWordType, expectedWordValue, finder.peekWord());
        assertWord(expectedWordType, expectedWordValue, finder.nextWord());
    }
    
    private void assertWord(WordType expectedWordType, Object expectedWordValue, Word word)
    {
        assertEquals(expectedWordType, word.getWordType());
        if (expectedWordValue != null)
            assertEquals(expectedWordValue, word.getWordValue());
    }
}
