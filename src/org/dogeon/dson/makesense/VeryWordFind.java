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

package org.dogeon.dson.makesense;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import org.dogeon.dson.Words;
import org.dogeon.dson.makesense.Word.WordType;

public class VeryWordFind
{
    private static final char NULL_CHAR = (char)0;
    
    private enum TokenType { SYMBOL, WORD, QUALIFIED, NUMBER }
    
    private Reader reader;
    private char nextChar = NULL_CHAR;
    private boolean foundEndOfStream = false;
    
    private Word nextWord;
    
    public VeryWordFind(Reader reader)
    {
        this.reader = reader;
    }
    
    public VeryWordFind(String dson)
    {
        this(new StringReader(dson));
    }
    
    public Word peekWord() throws IOException, MakeSenseException
    {
        return getNextWord();
    }
    
    public Word nextWord() throws IOException, MakeSenseException
    {
        Word word = getNextWord();
        findNextWord();
        return word;
    }
    
    private Word getNextWord() throws IOException, MakeSenseException
    {
        if (nextChar == NULL_CHAR)
            findNextWord();
        return nextWord;
    }
    
    private void findNextWord() throws IOException, MakeSenseException
    {
        nextWord = getWordLeaveTrailingWhitespace();
        if (!foundEndOfStream)
        {
            while ((Character.isWhitespace(nextChar)) && (readNextChar())) ;
        }
    }
    
    private Word getWordLeaveTrailingWhitespace() throws IOException, MakeSenseException
    {
        if ((foundEndOfStream) || ((nextChar == NULL_CHAR) && (!readNextChar())))
            return null;
        StringBuilder wordBuilder = new StringBuilder();
        wordBuilder.append(nextChar);
        
        TokenType tokenType;
        if (nextChar == '"')
            tokenType = TokenType.QUALIFIED;
        else if (Character.isLetter(nextChar))
            tokenType = TokenType.WORD;
        else if ((Character.isDigit(nextChar)) || (nextChar == '-') || (nextChar == '+'))
            tokenType = TokenType.NUMBER;
        else
        {
            char symbol = nextChar;
            readNextChar();
            return makeWord(TokenType.SYMBOL, "" + symbol);
        }
        
        READ_CHAR:
        while (readNextChar())
        {
            if ((Character.isWhitespace(nextChar)) && (tokenType != TokenType.QUALIFIED))
                break;
            switch (tokenType)
            {
                case WORD : {
                    if (!Character.isLetter(nextChar))
                        return makeWord(tokenType, wordBuilder.toString());
                    break;
                }
                case QUALIFIED : {
                    if (nextChar == '"')
                    {
                        wordBuilder.append(nextChar);
                        readNextChar();
                        return makeWord(tokenType, wordBuilder.toString());
                    }
                    else if (nextChar == '\\')
                    {
                        readNextChar();
                        if (!Words.isEscapableChar(nextChar))
                            throw new MakeSenseException("Invalid escape sequence: \\" + nextChar + '.');
                        wordBuilder.append(Words.getSpecialChar(nextChar));
                        continue READ_CHAR;
                    }
                    break;
                }
                case NUMBER : {
                    if ((!Character.isDigit(nextChar)) && (nextChar != '.'))
                        return makeWord(tokenType, wordBuilder.toString());
                    break;
                }
                default : throw new MakeSenseException("Unhandled token type: " + tokenType + '.');
            }
            wordBuilder.append(nextChar);
        }
        return makeWord(tokenType, wordBuilder.toString());
    }
    
    private boolean readNextChar() throws IOException
    {
        int c = reader.read();
        if (c == -1)
            foundEndOfStream = true;
        else
            nextChar = (char)c;
        return !foundEndOfStream;
    }
    
    private static Word makeWord(TokenType tokenType, String content) throws MakeSenseException
    {
        switch (tokenType)
        {
            case SYMBOL : {
                if (Words.isType(Words.MEMBER_SEPARATORS, content))
                    return new Word(WordType.MEMBER_SEPARATOR);
                throw new MakeSenseException("Unknown symbol: " + content);
            }
            case WORD : {
                if (Words.isType(Words.THING_BEGIN, content))
                    return new Word(WordType.THING_BEGIN);
                if (Words.isType(Words.THING_END, content))
                    return new Word(WordType.THING_END);
                if (Words.isType(Words.VALUE_SEPARATOR, content))
                    return new Word(WordType.VALUE_SEPARATOR);
                if (Words.isType(Words.LIST_BEGIN, content))
                    return new Word(WordType.LIST_BEGIN);
                if (Words.isType(Words.LIST_END, content))
                    return new Word(WordType.LIST_END);
                if (Words.isType(Words.ITEM_SEPARATORS, content))
                    return new Word(WordType.ITEM_SEPARATOR);
                if (Words.isType(Words.VERY, content))
                    return new Word(WordType.VERY);
                if (Words.isType(Words.YES_VALUE, content))
                    return new Word(WordType.VALUE, true);
                if (Words.isType(Words.NO_VALUE, content))
                    return new Word(WordType.VALUE, false);
                if (Words.isType(Words.EMPTY_VALUE, content))
                    return new Word(WordType.VALUE, null);
                throw new MakeSenseException("Unknown word: " + content + '.');
            }
            case QUALIFIED : {
                if (content.charAt(content.length() - 1) != '"')
                    throw new MakeSenseException("Unexpected end of string.");
                content = content.substring(1, content.length() - 1);
                Object value;
                try
                {
                    value = Words.DATE_FORMAT.parse(content);
                }
                catch (ParseException exc)
                {
                    value = content;
                }
                return new Word(WordType.VALUE, value);
            }
            case NUMBER : {
                int startIdx = Character.isDigit(content.charAt(0)) ? 0 : 1;
                int dotIdx = content.indexOf('.');
                long l = Long.parseLong(dotIdx == -1 ? content.substring(startIdx) : content.substring(startIdx, dotIdx), 8);
                if (dotIdx == -1)
                    return new Word(WordType.VALUE, content.charAt(0) == '-' ? -l : l);
                double d = l;
                double factor = 1;
                for (int i=dotIdx + 1; i<content.length(); i++)
                {
                    factor/=8;
                    d+=((int)content.charAt(i) - '0') * factor;
                }
                return new Word(WordType.VALUE, content.charAt(0) == '-' ? -d : d);
            }
        }
        return null;
    }
}
