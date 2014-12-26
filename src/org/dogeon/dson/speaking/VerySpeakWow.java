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

package org.dogeon.dson.speaking;

import java.util.Date;

import org.dogeon.dson.Words;
import org.dogeon.dson.util.ThingVisitor;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class VerySpeakWow implements ThingVisitor
{
    private static final double LOG8 = Math.log(8);
    private static final double MAX_FRACTION_DIGITS = 10;
    private static final double EXPONENT_UPPER_THRESHOLD = Math.pow(8, MAX_FRACTION_DIGITS), EXPONENT_LOWER_THRESHOLD = Math.pow(8, -MAX_FRACTION_DIGITS);
    
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
            if ((absValue >= EXPONENT_UPPER_THRESHOLD) || (absValue <= EXPONENT_LOWER_THRESHOLD))
            {
                double exponent = Math.floor(Math.log(absValue) / LOG8);
                String octalDigits = fractionToOctal(absValue / Math.pow(8, exponent + 1));
                addToken((doubleValue < 0 ? "-" : "") + octalDigits.charAt(0) + '.' + (octalDigits.length() == 1 ? '0' : octalDigits.substring(1)));
                addToken(Words.VERY[0]);
                addToken((exponent < 0 ? "-" : "") + String.format("%1$o", Math.abs((int)exponent)));
            }
            else
                addToken((doubleValue < 0 ? "-" : "") + String.format("%1$o.%2$s", (long)absValue, fractionToOctal(absValue - (long)absValue)));
        }
        else if (valueClass == Boolean.class)
            addToken((Boolean)value ? Words.YES_VALUE : Words.NO_VALUE);
        else if (valueClass == Date.class)
            addToken(Words.qualifyString(Words.DATE_FORMAT.format((Date)value)));
        else if ((valueClass == String.class) || (valueClass == Character.class))
            addToken(Words.qualifyString(value.toString()));
        else if (valueClass == byte[].class)
            addToken(Words.qualifyString(Base64.encode((byte[])value)));
        else
            addToken(Words.qualifyString(value.toString()));
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
        return (Character.isLetterOrDigit(c)) || (c == '-') || (c == '+') || (c == '"'); 
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
        while ((fraction > 0) && (s.length() < MAX_FRACTION_DIGITS));
        return s.toString();
    }
}
