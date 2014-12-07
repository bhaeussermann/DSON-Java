package org.dogeon.dson;

import java.util.ArrayList;

import org.dogeon.dson.util.Pair;

public class Words
{
    public static final String THING_BEGIN = "such", THING_END = "wow";
    public static final String VALUE_SEPARATOR = "is";
    public static final String[] MEMBER_SEPARATORS = new String[] { ",", ".", "!", "?" };
    public static final String LIST_BEGIN = "so", LIST_END = "many";
    public static final String[] ITEM_SEPARATORS = new String[] { "also", "and" };
    public static final String YES_VALUE = "yes", NO_VALUE = "no", EMPTY_VALUE = "empty";
    
    private static final ArrayList<Pair<String, String>> QUALIFY_REPLACEMENTS = new ArrayList<Pair<String, String>>(8); 
    
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
    }
    
    public static String qualifyString(String s)
    {
        for (Pair<String, String> nextReplacement : QUALIFY_REPLACEMENTS)
            s = s.replace(nextReplacement.first ,nextReplacement.second);
        return '"' + s + '"';
    }
}
