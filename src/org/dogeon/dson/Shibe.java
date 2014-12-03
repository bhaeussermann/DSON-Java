package org.dogeon.dson;

import org.dogeon.dson.speaking.VerySpeakWow;
import org.dogeon.dson.util.ThingUtil;

public class Shibe
{
    public static String speak(Object obj)
    {
        VerySpeakWow speak = new VerySpeakWow();
        ThingUtil.walkThing(obj, speak);
        return speak.getSpeak();
    }
}
