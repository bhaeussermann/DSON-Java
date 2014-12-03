package org.dogeon.dson.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ThingUtil
{
    public static void walkThing(Object thing, ThingVisitor visitor)
    {
        if (thing == null)
            visitor.visitEmptySad();
        else
        {
            Class<?> suchClass = thing.getClass();
            if ((suchClass.isPrimitive()) || (suchClass == String.class))
                visitor.visitValue(thing);
            else if ((suchClass.isArray()) || (Iterable.class.isAssignableFrom(suchClass)))
            {
                visitor.visitSuchList();
                for (Object nextItem : (Iterable<?>)thing)
                    walkThing(nextItem, visitor);
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
