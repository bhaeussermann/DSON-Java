package org.dogeon.dson.speaking;

import org.dogeon.dson.Words;
import org.dogeon.dson.util.ThingVisitor;

public class VerySpeakWow implements ThingVisitor
{
    private StringBuilder result = new StringBuilder();

    public String getSpeak()
    {
        return result.toString();
    }
    
    public void visitEmptySad()
    {
        addToken(Words.EMPTY_VALUE);
    }

    public void visitValue(Object value)
    {
        if (value instanceof Boolean)
            result.append((Boolean)value ? Words.YES_VALUE : Words.NO_VALUE);
        else
            result.append(value instanceof String ? qualify(value.toString()) : value);
    }

    public void visitMember(String name, boolean isFirst)
    {
        if (!isFirst)
            addToken(Words.choose(Words.MEMBER_SEPARATORS));
        result.append(qualify(name));
        addToken(Words.VALUE_SEPARATOR);
    }

    public void visitSuchComposite()
    {
        addToken(Words.THING_BEGIN);
    }

    public void visitCompositeWow()
    {
        addToken(Words.THING_END);
    }

    public void visitSuchList()
    {
        addToken(Words.LIST_BEGIN);
    }

    public void visitListWow()
    {
        addToken(Words.LIST_END);
    }
    
    private void addToken(String token)
    {
        if ((result.length() != 0) && (Words.suchTokenIsWord(token)))
            result.append(' ');
        result.append(token);
    }
    
    
    private static String qualify(String s)
    {
        return '"' + s + '"';
    }
}
