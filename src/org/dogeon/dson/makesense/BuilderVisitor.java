package org.dogeon.dson.makesense;

import java.util.Stack;

import org.dogeon.dson.model.DogeList;
import org.dogeon.dson.model.DogeThing;

public class BuilderVisitor implements ThingVisitor
{
	private Stack<BuilderContext> contextStack = new Stack<BuilderContext>();
	
	public BuilderVisitor()
	{
		contextStack.push(new RootBuilderContext());
	}
	
	public Object getBuiltValue()
	{
		return ((RootBuilderContext)contextStack.peek()).getBuilt();
	}
	
	public void visitValue(Object value) 
	{
		contextStack.peek().putValue(value);
	}

	public void visitMember(String name) 
	{
		((ThingBuilderContext)contextStack.peek()).setCurrentMemberName(name);
	}

	public void visitSuchComposite() 
	{
		BuilderContext context = contextStack.peek();
		BuilderContext newContext = new ThingBuilderContext();
		context.putValue(newContext.getBuilt());
		contextStack.push(newContext);
	}

	public void visitCompositeWow() 
	{
		contextStack.pop();
	}

	public void visitSuchList() 
	{
		BuilderContext context = contextStack.peek();
		BuilderContext newContext = new ListBuilderContext();
		context.putValue(newContext.getBuilt());
		contextStack.push(newContext);
	}

	public void visitListWow() 
	{
		contextStack.pop();
	}
	
	
	private interface BuilderContext
	{
		public Object getBuilt();
		public void putValue(Object value);
	}
	
	private static class RootBuilderContext implements BuilderContext
	{
		private Object value;

		public Object getBuilt()
		{
			return value;
		}
		
		public void putValue(Object value) 
		{
			this.value = value;
		}
	}
	
	private static class ThingBuilderContext implements BuilderContext
	{
		private DogeThing thing = new DogeThing();
		private String currentMemberName;
		
		public void setCurrentMemberName(String newCurrentMemberName)
		{
			currentMemberName = newCurrentMemberName;
		}

		public Object getBuilt()
		{
			return thing;
		}
		
		public void putValue(Object value) 
		{
			thing.put(currentMemberName, value);
		}
	}
	
	private static class ListBuilderContext implements BuilderContext
	{
		private DogeList list = new DogeList();
		
		public Object getBuilt()
		{
			return list;
		}
		
		public void putValue(Object value) 
		{
			list.add(value);
		}
	}
}
