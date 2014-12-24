package org.dogeon.dson.makesense;

import java.io.IOException;
import java.io.Reader;

import org.dogeon.dson.Words;
import org.dogeon.dson.makesense.Word.WordType;

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
	        case VALUE : parseValue(visitor); break; 
	        case THING_BEGIN : parseThing(visitor); break;
	        case LIST_BEGIN : parseList(visitor); break;
	        default : throw new MakeSenseException("Unexpected " + wordFinder.peekWord());
        }
    }
    
    private void parseValue(ThingVisitor visitor) throws IOException, MakeSenseException
    {
    	Object value = wordFinder.nextWord().getWordValue();
    	if ((wordFinder.peekWord() != null) && (wordFinder.peekWord().getWordType() == WordType.VERY))
    	{
    		wordFinder.nextWord();
    		Object veryValue = wordFinder.nextWord().getWordValue();
    		value = Math.pow(value instanceof Long ? (Long)value : (Double)value, veryValue instanceof Long ? (Long)veryValue : (Double)veryValue);
    	}
    	visitor.visitValue(value);
    }
    
    private void parseThing(ThingVisitor visitor) throws IOException, MakeSenseException
    {
    	visitor.visitSuchComposite();
    	wordFinder.nextWord();
    	if (wordFinder.peekWord().getWordType() == WordType.VALUE)
    		parseMemberList(visitor);
    	if (wordFinder.peekWord().getWordType() != WordType.THING_END)
    		throw new MakeSenseException("Expected " + Words.THING_END + "; was " + wordFinder.peekWord() + '.');
    	wordFinder.nextWord();
    	visitor.visitCompositeWow();
    }
    
    private void parseMemberList(ThingVisitor visitor) throws IOException, MakeSenseException
    {
    	while (true)
    	{
    		boolean doVisitMember;
	    	Object memberNameValue = wordFinder.nextWord().getWordValue();
	    	if (memberNameValue instanceof String)
	    		doVisitMember = visitor.visitMember((String)memberNameValue);
	    	else
	    		throw new MakeSenseException("Member name must be a string. Value was " + memberNameValue + '.');
	    	if (wordFinder.peekWord().getWordType() != WordType.VALUE_SEPARATOR)
	    		throw new MakeSenseException("Expected " + Words.VALUE_SEPARATOR + "; was " + wordFinder.peekWord() + '.');
	    	wordFinder.nextWord();
	    	parseDson(doVisitMember ? visitor : new DummyVisitor());
	    	
	    	if (wordFinder.peekWord().getWordType() != WordType.MEMBER_SEPARATOR)
	    		return;
    		wordFinder.nextWord();
    	}
    }
    
    private void parseList(ThingVisitor visitor) throws IOException, MakeSenseException
    {
    	visitor.visitSuchList();
    	wordFinder.nextWord();
    	if (wordFinder.peekWord().getWordType() != WordType.LIST_END)
    	{
    		parseElements(visitor);
    		if (wordFinder.peekWord().getWordType() != WordType.LIST_END)
    			throw new MakeSenseException("Expected " + Words.LIST_END + ": was " + wordFinder.peekWord() + '.');
    	}
    	wordFinder.nextWord();
    	visitor.visitListWow();
    }
    
    private void parseElements(ThingVisitor visitor) throws IOException, MakeSenseException
    {
    	while (true)
    	{
    		parseDson(visitor);
    		if (wordFinder.peekWord().getWordType() != WordType.ITEM_SEPARATOR)
	    		return;
    		wordFinder.nextWord();
    	}
    }
    
    
    private static class DummyVisitor implements ThingVisitor
    {
		public void visitValue(Object value) {}

		public boolean visitMember(String name) 
		{
			return true;
		}

		public void visitSuchComposite() {}
		public void visitCompositeWow() {}
		public void visitSuchList() {}
		public void visitListWow() {}
    }
}
