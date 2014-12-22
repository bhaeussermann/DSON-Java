package org.dogeon.dson.makesense;

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
		contextStack.push(new RootContext(c));
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
		ParameterizedType parameterizedType = context.getComponentParameterizedType();
		InstantiatorContext newContext = componentInstance == null ? new ListContext(context.getComponentClass(), parameterizedType) : new ListContext(componentInstance, parameterizedType);
		context.putValue(newContext.getBuilt());
		contextStack.push(newContext);
	}

	public void visitListWow() 
	{
		contextStack.pop();
	}
	
	
	private interface InstantiatorContext
	{
		public Object getBuilt();
		public Object getComponentInstance();
		public Class<?> getComponentClass();
		public ParameterizedType getComponentParameterizedType();
		public void putValue(Object value);
	}
	
	private static class RootContext implements InstantiatorContext
	{
		private Class<?> c;
		private Object value;

		public RootContext(Class<?> c)
		{
			this.c = c;
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
		
		public ParameterizedType getComponentParameterizedType()
		{
			return null;
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
			currentGetterMethod = currentSetterMethod = null;
			String setterMethodName = "set" + Character.toUpperCase(memberName.charAt(0)) + memberName.substring(1);
			for (Method nextMethod : c.getMethods())
				if (((nextMethod.getModifiers() & Method.PUBLIC) == Method.PUBLIC) && (nextMethod.getName().equals(setterMethodName)))
				{
					currentSetterMethod = nextMethod;
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
				return currentGetterMethod == null ? null : currentGetterMethod.invoke(thing);
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				throw new RuntimeException(e);
			}
		}
		
		public Class<?> getComponentClass()
		{
			return currentGetterMethod == null ? currentSetterMethod.getParameterTypes()[0] : currentGetterMethod.getReturnType();
		}
		
		public ParameterizedType getComponentParameterizedType()
		{
			
			return (ParameterizedType)(currentGetterMethod == null ? currentSetterMethod.getGenericParameterTypes()[0] : currentGetterMethod.getGenericReturnType());
		}
		
		public void putValue(Object value) 
		{
			try 
			{
				if (currentSetterMethod != null)
					currentSetterMethod.invoke(thing, value);
			} 
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	private static class ListContext implements InstantiatorContext
	{
		private Class<?> listClass;
		private ParameterizedType listParameterizedType;
		private Object list;
		private Method adderMethod;
		
		public ListContext(Object list, ParameterizedType listParameterizedType) throws MakeSenseException
		{
			this.list = list;
			listClass = list.getClass();
			this.listParameterizedType = listParameterizedType;
			try
			{
				adderMethod = listClass.getMethod("add", Object.class);
			} 
			catch (NoSuchMethodException | SecurityException e) 
			{
				throw new MakeSenseException(e);
			}
		}
		
		public ListContext(Class<?> listClass, ParameterizedType listParameterizedType) throws MakeSenseException
		{
			this(makeListInstance(listClass), listParameterizedType);
		}
		
		private static Object makeListInstance(Class<?> listClass) throws MakeSenseException
		{
			try 
			{
				return listClass == List.class ? createInstance(ArrayList.class) : createInstance(listClass);
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
			return (Class<?>)listParameterizedType.getActualTypeArguments()[0];
		}
		
		public ParameterizedType getComponentParameterizedType()
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