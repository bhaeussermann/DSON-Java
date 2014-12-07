package org.dogeon.dson.speaking;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dogeon.dson.Words;
import org.dogeon.dson.util.ThingVisitor;

public class VerySpeakWow implements ThingVisitor
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
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
        Class<?> valueClass = value.getClass();
        if ((valueClass == Byte.class) || (valueClass == Short.class) || (valueClass == Integer.class) || (valueClass == Long.class))
        {
            long longValue = ((Number)value).longValue();
            addToken((longValue < 0 ? "-" : "") + String.format("%1$o", Math.abs(longValue)));
        }
        else if ((valueClass == Float.class) || (valueClass == Double.class))
        {
            double doubleValue = ((Number)value).doubleValue(); 
            double absValue = Math.abs(doubleValue);
            addToken((doubleValue < 0 ? "-" : "") + String.format("%1$o.%2$s", (long)absValue, fractionToOctal(absValue - (long)absValue)));
        }
        else if (valueClass == Boolean.class)
            addToken((Boolean)value ? Words.YES_VALUE : Words.NO_VALUE);
        else if (valueClass == Date.class)
            addToken(Words.qualifyString(dateFormat.format((Date)value)));
        else if ((valueClass == String.class) || (valueClass == Character.class))
            addToken(Words.qualifyString(value.toString()));
        else
            addToken(value.toString());
    }

    public void visitMember(String name, int index)
    {
        if (index > 0)
            addToken(Words.MEMBER_SEPARATORS[(index - 1) % 4]);
        addToken('"' + name + '"');
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
    
    public void visitItem(int index)
    {
        if (index > 0)
            addToken(Words.ITEM_SEPARATORS[index == 1 ? 0 : 1]);
    }
    
    
    private void addToken(String token)
    {
        if ((result.length() != 0) && 
                (isSeparatableCharacter(token.charAt(0))) && (isSeparatableCharacter(result.charAt(result.length() - 1))))
        {
            result.append(' ');
        }
        result.append(token);
    }
    
    private static boolean isSeparatableCharacter(char c)
    {
        return (Character.isLetterOrDigit(c)) || (c == '"'); 
    }
    
    private static String fractionToOctal(double fraction)
    {
        StringBuilder s = new StringBuilder();
        do
        {
            fraction*=8;
            int digit = (int)fraction;
            s.append(digit);
            fraction-=digit;
        }
        while ((fraction > 0) && (s.length() < 10));
        return s.toString();
    }
}
