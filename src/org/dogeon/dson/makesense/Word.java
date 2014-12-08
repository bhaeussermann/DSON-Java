package org.dogeon.dson.makesense;

public class Word
{
    public enum WordType
    {
        THING_BEGIN, THING_END, VALUE, VERY,  
        VALUE_SEPARATOR, MEMBER_SEPARATOR, 
        LIST_BEGIN, LIST_END, ITEM_SEPARATOR
    }
    
    private WordType wordType;
    private Object wordValue;
    
    public Word(WordType wordType)
    {
        this(wordType, null);
    }
    
    public Word(WordType wordType, Object wordValue)
    {
        this.wordType = wordType;
        this.wordValue = wordValue;
    }
    
    public WordType getWordType()
    {
        return wordType;
    }
    
    public Object getWordValue()
    {
        return wordValue;
    }
}
