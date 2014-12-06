package org.dogeon.dson.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

public class ThingUtil
{
    public static void walkThing(Object thing, ThingVisitor visitor)
    {
        if (thing == null)
            visitor.visitEmptySad();
        else
        {
            Class<?> suchClass = thing.getClass();
            if ((thing instanceof Number) || (suchClass == Boolean.class) || (suchClass == Character.class) || (suchClass == String.class) || (suchClass == Date.class))
                visitor.visitValue(thing);
            else if (suchClass.isArray()) 
            {
                visitor.visitSuchList();
                for (int i=0; i<Array.getLength(thing); i++)
                {
                    visitor.visitItem(i);
                    walkThing(Array.get(thing, i), visitor);
                }
                visitor.visitListWow();
            }
            else if (Iterable.class.isAssignableFrom(suchClass))
            {
                visitor.visitSuchList();
                int i = 0;
                for (Object nextItem : (Iterable<?>)thing)
                {
                    visitor.visitItem(i++);
                    walkThing(nextItem, visitor);
                }
                visitor.visitListWow();
            }
            else
            {
                visitor.visitSuchComposite();
                boolean isFirstMember = true;
                for (Method nextMethod : suchClass.getMethods())
                {
                    String methodName = nextMethod.getName(); 
                    if ((((methodName.startsWith("get")) && (nextMethod.getReturnType() != Void.class)) || 
                            ((methodName.startsWith("is")) && (nextMethod.getReturnType() == Boolean.class))) && 
                            (nextMethod.getParameterTypes().length == 0) && ((nextMethod.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC))
                    {
                        visitor.visitMember(methodName.startsWith("get") ? methodName.substring(3) : methodName.substring(2), isFirstMember);
                        Object memberValue = null;
                        try
                        {
                            memberValue = nextMethod.invoke(thing);
                        } 
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        walkThing(memberValue, visitor);
                        isFirstMember = false;
                    }
                }
                visitor.visitCompositeWow();
            }
        }
    }
}
