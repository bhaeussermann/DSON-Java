package org.dogeon.dson;

import java.util.Random;

public class Words
{
    private static final Random generator = new Random();
    
    public static final String THING_BEGIN = "such", THING_END = "wow";
    public static final String VALUE_SEPARATOR = "is";
    public static final String[] MEMBER_SEPARATORS = new String[] { ",", ".", "!", "?" };
    public static final String LIST_BEGIN = "so", LIST_END = "many";
    public static final String[] THING_SEPARATORS = new String[] { "also", "and" };
    public static final String YES_VALUE = "yes", NO_VALUE = "no", EMPTY_VALUE = "empty";
    
    public static String choose(String[] tokens)
    {
        return tokens[generator.nextInt(tokens.length)];
    }
    
    public static boolean suchTokenIsWord(String token)
    {
        return Character.isLetter(token.charAt(0));
    }
}
