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
        assertWord(WordType.THING_BEGIN, finder.nextWord());
        assertWord(WordType.VALUE, "foo", finder.nextWord());
        assertWord(WordType.VALUE_SEPARATOR, finder.nextWord());
        assertWord(WordType.LIST_BEGIN, finder.nextWord());
        assertWord(WordType.VALUE, "bar", finder.nextWord());
        assertWord(WordType.ITEM_SEPARATOR, finder.nextWord());
        assertWord(WordType.VALUE, -13.375, finder.nextWord());
        assertWord(WordType.ITEM_SEPARATOR, finder.nextWord());
        assertWord(WordType.VALUE, "fizz - \"buzz\"", finder.nextWord());
        assertWord(WordType.LIST_END, finder.nextWord());
        assertWord(WordType.MEMBER_SEPARATOR, finder.nextWord());
        assertWord(WordType.VALUE, "doge", finder.nextWord());
        assertWord(WordType.VALUE_SEPARATOR, finder.nextWord());
        assertWord(WordType.VALUE, true, finder.nextWord());
        assertWord(WordType.MEMBER_SEPARATOR, finder.nextWord());
        assertWord(WordType.VALUE, "many", finder.nextWord());
        assertWord(WordType.VALUE_SEPARATOR, finder.nextWord());
        assertWord(WordType.VALUE, 34l, finder.nextWord());
        assertWord(WordType.VERY, finder.nextWord());
        assertWord(WordType.VALUE, 3l, finder.nextWord());
        assertWord(WordType.THING_END, finder.nextWord());
        assertNull(finder.nextWord());
    }
    
    private void assertWord(WordType expectedWordType, Word word)
    {
        assertEquals(expectedWordType, word.getWordType());
    }
    
    private void assertWord(WordType expectedWordType, Object expectedWordValue, Word word)
    {
        assertEquals(expectedWordType, word.getWordType());
        assertEquals(expectedWordValue, word.getWordValue());
    }
}
