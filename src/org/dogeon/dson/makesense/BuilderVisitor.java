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

	public boolean visitMember(String name) 
	{
		((ThingBuilderContext)contextStack.peek()).setCurrentMemberName(name);
		return true;
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
