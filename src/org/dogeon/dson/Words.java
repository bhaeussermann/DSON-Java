package org.dogeon.dson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.dogeon.dson.util.Pair;

public class Words
{
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    public static final String THING_BEGIN = "such", THING_END = "wow";
    public static final String VALUE_SEPARATOR = "is";
    public static final String[] MEMBER_SEPARATORS = new String[] { ",", ".", "!", "?" };
    public static final String LIST_BEGIN = "so", LIST_END = "many";
    public static final String[] ITEM_SEPARATORS = new String[] { "also", "and" };
    public static final String YES_VALUE = "yes", NO_VALUE = "no", EMPTY_VALUE = "empty";
    public static final String[] VERY = new String[] { "very", "VERY" };
    
    private static final ArrayList<Pair<String, String>> QUALIFY_REPLACEMENTS = new ArrayList<Pair<String, String>>(8);
    private static final HashMap<Character, Character> ESCAPE_CHAR_MAP = new HashMap<Character, Character>(8);
    
    static
    {
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\\", "\\\\"));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\"", "\\\""));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("/", "\\/"));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\b", "\\b"));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\f", "\\f"));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\n", "\\n"));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\r", "\\r"));
        QUALIFY_REPLACEMENTS.add(new Pair<String, String>("\t", "\\t"));
        
        ESCAPE_CHAR_MAP.put('\\', '\\');
        ESCAPE_CHAR_MAP.put('"', '"');
        ESCAPE_CHAR_MAP.put('/', '/');
        ESCAPE_CHAR_MAP.put('b', '\b');
        ESCAPE_CHAR_MAP.put('f', '\f');
        ESCAPE_CHAR_MAP.put('n', '\n');
        ESCAPE_CHAR_MAP.put('r', '\r');
        ESCAPE_CHAR_MAP.put('t', '\t');
    }
    
    public static boolean isType(String wordType, String word)
    {
        return wordType.equals(word);
    }
    
    public static boolean isType(String[] wordType, String word)
    {
        for (String next : wordType)
            if (next.equals(word))
                return true;
        return false;
    }
    
    public static boolean isEscapableChar(char c)
    {
        return ESCAPE_CHAR_MAP.containsKey(c);
    }
    
    public static char getSpecialChar(char escapeChar)
    {
        return ESCAPE_CHAR_MAP.get(escapeChar);
    }
    
    public static String qualifyString(String s)
    {
        for (Pair<String, String> nextReplacement : QUALIFY_REPLACEMENTS)
            s = s.replace(nextReplacement.first ,nextReplacement.second);
        return '"' + s + '"';
    }
}
