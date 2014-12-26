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

package org.dogeon.dson.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class ThingUtil
{
    public static void walkThing(Object thing, ThingVisitor visitor)
    {
        if (thing == null)
            visitor.visitEmptySad();
        else
        {
            Class<?> suchClass = thing.getClass();
            if ((thing instanceof Number) || (suchClass == Boolean.class) || (suchClass == Character.class) || (suchClass == String.class) || (suchClass == Date.class) || (suchClass == byte[].class))
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
            else if (thing instanceof Map)
            {
                Map<?, ?> m = (Map<?, ?>)thing;
                visitor.visitSuchComposite();
                String[] memberNames = m.keySet().toArray(new String[0]);
                Arrays.sort(memberNames);
                for (int i=0; i<memberNames.length; i++)
                {
                    visitor.visitMember(memberNames[i], i);
                    walkThing(m.get(memberNames[i]), visitor);
                }
                visitor.visitCompositeWow();
            }
            else
            {
                visitor.visitSuchComposite();
                int i = 0;
                Method[] methods = suchClass.getMethods();
                Arrays.sort(methods, new MethodNameComparator());
                for (Method nextMethod : methods)
                {
                    String methodName = nextMethod.getName();
                    if ((((methodName.startsWith("get")) && (!methodName.equals("getClass")) && (nextMethod.getReturnType() != Void.class)) || 
                            ((methodName.startsWith("is")) && (nextMethod.getReturnType() == boolean.class))) && 
                            (nextMethod.getParameterTypes().length == 0) && ((nextMethod.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC))
                    {
                        visitor.visitMember(getMethodSubjectName(nextMethod), i++);
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
                    }
                }
                visitor.visitCompositeWow();
            }
        }
    }
    
    
    private static String getMethodSubjectName(Method method)
    {
        String subjectName;
        String methodName = method.getName(); 
        if (methodName.startsWith("get"))
            subjectName = methodName.substring(3);
        else if (methodName.startsWith("is"))
            subjectName = methodName.substring(2);
        else
            subjectName = methodName;
        
        if ((subjectName.length() > 1) && (Character.isLowerCase(subjectName.charAt(1))))
            subjectName = Character.toLowerCase(subjectName.charAt(0)) + subjectName.substring(1);
        return subjectName;
    }
    
    private static class MethodNameComparator implements Comparator<Method>
    {
        public int compare(Method o1, Method o2)
        {
            return getMethodSubjectName(o1).compareTo(getMethodSubjectName(o2));
        }
    }
}
