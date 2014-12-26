package org.dogeon.dson.makesense;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InstantiatorVisitor implements ThingVisitor
{
	private Stack<InstantiatorContext> contextStack = new Stack<InstantiatorContext>();
	
	public InstantiatorVisitor(Class<?> c)
	{
	    this(c, null);
	}
	
	public InstantiatorVisitor(Class<?> c, Class<?> componentClass)
	{
		contextStack.push(new RootContext(c, componentClass));
	}
	
	public Object getBuiltValue()
	{
		return ((RootContext)contextStack.peek()).getBuilt();
	}
	
	public void visitValue(Object value) 
	{
		contextStack.peek().putValue(value);
	}

	public boolean visitMember(String name) 
	{
		return ((ThingContext)contextStack.peek()).setCurrentMemberName(name);
	}

	public void visitSuchComposite() throws MakeSenseException 
	{
		InstantiatorContext context = contextStack.peek();
		Object componentInstance = context.getComponentInstance();
		InstantiatorContext newContext = componentInstance == null ? new ThingContext(context.getComponentClass()) : new ThingContext(componentInstance);
		context.putValue(newContext.getBuilt());
		contextStack.push(newContext);
	}

	public void visitCompositeWow() 
	{
		contextStack.pop();
	}

	public void visitSuchList() throws MakeSenseException 
	{
		InstantiatorContext context = contextStack.peek();
		Object componentInstance = context.getComponentInstance();
		Class<?> componentClassOfComponent = context.getComponentClassComponentClass();
		InstantiatorContext newContext = componentInstance == null ? new ListContext(context.getComponentClass(), componentClassOfComponent) : new ListContext(componentInstance, componentClassOfComponent);
		if ((componentInstance != null) || (!context.getComponentClass().isArray()))
		    context.putValue(newContext.getBuilt());
		contextStack.push(newContext);
	}

	public void visitListWow() 
	{
		InstantiatorContext listContext = contextStack.pop();
		
		InstantiatorContext context = contextStack.peek();
		Class<?> componentClass = context.getComponentClass();
		if ((componentClass != null) && (componentClass.isArray()))
		    context.putValue(listContext.getBuilt());
	}
	
	
	private interface InstantiatorContext
	{
		public Object getBuilt();
		public Object getComponentInstance();
		public Class<?> getComponentClass();
		public Class<?> getComponentClassComponentClass();
		public void putValue(Object value);
	}
	
	
	private static class RootContext implements InstantiatorContext
	{
		private Class<?> c, componentClass;
		private Object value;

		public RootContext(Class<?> c, Class<?> componentClass)
		{
			this.c = c;
			this.componentClass = componentClass;
		}
		
		public Object getBuilt()
		{
			return value;
		}
		
		public Object getComponentInstance()
		{
			return null;
		}
		
		public Class<?> getComponentClass()
		{
			return c;
		}
		
		public Class<?> getComponentClassComponentClass()
		{
			return componentClass;
		}
		
		public void putValue(Object value) 
		{
			this.value = value;
		}
	}
	
	
	private static class ThingContext implements InstantiatorContext
	{
		private Class<?> c;
		private Object thing;
		private Field currentField;
		private Method currentGetterMethod, currentSetterMethod;
		
		public ThingContext(Object thing)
		{
			this.thing = thing;
			c = thing.getClass();
		}
		
		public ThingContext(Class<?> c) throws MakeSenseException
		{
			this.c = c;
			try 
			{
				thing = createInstance(c);
			} 
			catch (InstantiationException e) 
			{
				throw new MakeSenseException(e);
			}
		}
		
		public boolean setCurrentMemberName(String memberName)
		{
		    currentField = null;
			currentGetterMethod = currentSetterMethod = null;
			String setterMethodName = "set" + Character.toUpperCase(memberName.charAt(0)) + memberName.substring(1);
			for (Method nextMethod : c.getMethods())
				if (((nextMethod.getModifiers() & Method.PUBLIC) == Method.PUBLIC) && (nextMethod.getName().equals(setterMethodName)))
				{
					currentSetterMethod = nextMethod;
					return true;
				}
			
			for (Field nextField : c.getFields())
			    if (((nextField.getModifiers() & Method.PUBLIC) == Method.PUBLIC) && (nextField.getName().equals(memberName)))
	            {
			        currentField = nextField;
			        return true;
	            }
			
			String getterMethodName = "get" + Character.toUpperCase(memberName.charAt(0)) + memberName.substring(1);
			try 
			{
				currentGetterMethod = c.getMethod(getterMethodName);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				return false;
			}
			return true;
		}

		public Object getBuilt()
		{
			return thing;
		}
		
		public Object getComponentInstance()
		{
			try 
			{
			    if (currentGetterMethod != null)
			        return currentGetterMethod.invoke(thing);
			    return currentField == null ? null : currentField.get(thing);
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				throw new RuntimeException(e);
			}
		}
		
		public Class<?> getComponentClass()
		{
		    if (currentGetterMethod != null)
		        return currentGetterMethod.getReturnType();
		    if (currentField != null)
		        return currentField.getType();
			return currentSetterMethod.getParameterTypes()[0];
		}
		
		public Class<?> getComponentClassComponentClass()
		{
		    Class<?> componentClass = getComponentClass(); 
			if (componentClass.isArray())
			    return componentClass.getComponentType();
			
		    ParameterizedType type;
		    if (currentGetterMethod != null)
		        type = (ParameterizedType)currentGetterMethod.getGenericReturnType();
		    else if (currentField != null)
		        type = (ParameterizedType)currentField.getGenericType();
		    else
		        type = (ParameterizedType)currentSetterMethod.getGenericParameterTypes()[0];
		    return (Class<?>)type.getActualTypeArguments()[0];
		}
		
		public void putValue(Object value) 
		{
		    if (getComponentClass().isArray())
		    {
		        @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>)value;
		        Object array = Array.newInstance(getComponentClassComponentClass(), list.size());
		        int i = 0;
		        for (Object nextItem : list)
		            Array.set(array, i++, nextItem);
		        value = array;
		    }
		    
			try
			{
				if (currentSetterMethod != null)
					currentSetterMethod.invoke(thing, value);
				else if (currentField != null)
				    currentField.set(thing, value);
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	
	private static class ListContext implements InstantiatorContext
	{
		private Class<?> listClass, componentClass;
		private Object list;
		private Method adderMethod;
		
		public ListContext(Object list, Class<?> componentClass) throws MakeSenseException
		{
			this.list = list;
			listClass = list.getClass();
			this.componentClass = componentClass;
			try
			{
				adderMethod = listClass.getMethod("add", Object.class);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				throw new MakeSenseException(e);
			}
		}
		
		public ListContext(Class<?> listClass, Class<?> componentClass) throws MakeSenseException
		{
			this(makeListInstance(listClass), componentClass);
		}
		
		private static Object makeListInstance(Class<?> listClass) throws MakeSenseException
		{
			try 
			{
				return (listClass.isArray()) || (listClass == List.class) ? createInstance(ArrayList.class) : createInstance(listClass);
			} 
			catch (InstantiationException e) 
			{
				throw new MakeSenseException(e);
			}
		}
		
		public Object getBuilt()
		{
			return list;
		}
		
		public Object getComponentInstance()
		{
			return null;
		}
		
		public Class<?> getComponentClass()
		{
			return componentClass;
		}
		
		public Class<?> getComponentClassComponentClass()
		{
			return null;
		}
		
		public void putValue(Object value) 
		{
			try 
			{
				adderMethod.invoke(list, value);
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	
	private static Object createInstance(Class<?> c) throws InstantiationException
	{
		try 
		{
			return c.getConstructor().newInstance();
		} 
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) 
		{
			throw new InstantiationException("Error instantiating class " + c + ": " + e.getMessage());
		}
		catch (NoSuchMethodException e)
		{
			throw new InstantiationException("Error instantiating class " + c + ": no public default constructor.");
		}	
	}
}
