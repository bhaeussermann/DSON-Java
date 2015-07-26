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

package org.dogeon.dson.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.dogeon.dson.Shibe;
import org.dogeon.dson.makesense.MakeSenseException;
import org.dogeon.dson.model.DogeList;
import org.dogeon.dson.model.DogeThing;
import org.junit.Test;

public class TestMakeSense 
{
    @Test
    public void testBad()
    {
        try
        {
            Shibe.makeSense("such such many");
            fail("Should throw exception.");
        }
        catch (RuntimeException e)
        {
            assertTrue(e.getCause() instanceof MakeSenseException);
            assertEquals("Expected wow; was THING_BEGIN.", e.getCause().getMessage());
        }
        catch (Exception e)
        {
            fail("Wrong exception.");
        }
    }
    
    @Test
    public void testSimple()
    {
        String text = (String)Shibe.makeSense("\"some text\"");
        assertEquals("some text", text);
        
        long number = (Long)Shibe.makeSense("123");
        assertEquals(83, number);
        
        assertNull(Shibe.makeSense("empty"));
    }
    
	@Test
	public void testThing()
	{
		DogeThing result = (DogeThing)Shibe.makeSense(
		        "such \"rating\" is -1, \"storyCount\" is 2, \"windowCount\" is 7, \"area\" is 7.5, \"address\" is \"123 \\\"fizz-buzz\\\"\", "
		        + "\"employees\" is so so many many, "
		        + "\"rooms\" is so many. "
		        + "\"visitors\" is so "
		            + "such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"account\" is -15.3 wow and "
		            + "such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"account\" is 42 very 3 wow also "
		            + "such wow "
		        + "many wow");
		
		assertEquals(-1l, result.get("rating"));
		assertEquals(2l, result.get("storyCount"));
		assertEquals(7l, result.get("windowCount"));
		assertEquals(7.625, result.get("area"));
		assertEquals("123 \"fizz-buzz\"", result.get("address"));
		DogeList employees = (DogeList)result.get("employees");
		assertEquals(1, employees.size());
		assertEquals(0, ((DogeList)employees.get(0)).size());
		DogeList rooms = (DogeList)result.get("rooms");
		assertEquals(0, rooms.size());
		DogeList visitors = (DogeList)result.get("visitors");
		assertEquals(3, visitors.size());
		
		DogeThing visitor = (DogeThing)visitors.get(0);
		assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.get("checkinDate"));
		assertEquals(-13.375, visitor.get("account"));
		DogeThing animalInfo = (DogeThing)visitor.get("animalInfo");
		assertEquals("inu", animalInfo.get("shiba"));
		assertEquals(true, animalInfo.get("doge"));
		
		visitor = (DogeThing)visitors.get(1);
		assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.get("checkinDate"));
		assertEquals(17408.0, visitor.get("account"));
		animalInfo = (DogeThing)visitor.get("animalInfo");
		assertEquals(null, animalInfo.get("shiba"));
		assertEquals(false, animalInfo.get("doge"));
		
		visitor = (DogeThing)visitors.get(2);
		assertEquals(0, visitor.keySet().size());
	}
	
	@Test
	public void testInstantiateThing() throws MakeSenseException
	{
		DogeHotel result = (DogeHotel)Shibe.makeSense(
		        "such \"rating\" is -1, \"storyCount\" is 2, \"windowCount\" is 7, \"area\" is 7.5, \"address\" is \"123 \\\"fizz-buzz\\\"\", "
		        + "\"qwer\" is such \"a\" is 0, \"b\" is so 1 many wow, "
		        + "\"employees\" is so so many many, "
		        + "\"rooms\" is so many. "
		        + "\"visitors\" is so "
		            + "such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"accounts\" is so -15.3 many wow and "
		            + "such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"accounts\" is so 42 very 3 also 0 many wow also "
		            + "such wow "
	            + "many wow", 
		        DogeHotel.class);
		
		assertEquals(-1, result.rating);
		assertEquals(2, result.storyCount);
		assertEquals(7, result.windowCount);
		assertEquals(7.625, result.getArea(), 0.001);
		assertEquals("123 \"fizz-buzz\"", result.getAddress());
		assertEquals(1, result.employees.size());
		assertEquals(0, result.employees.get(0).length);
		assertEquals(0, result.getRooms().size());
		assertEquals(3, result.getVisitors().size());
		
		Visitor visitor = result.getVisitors().get(0);
		assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
		assertEquals(1, visitor.accounts.length);
		assertEquals(-13.375, (Object)visitor.accounts[0]);
		assertEquals("inu", visitor.getAnimalInfo().getShiba());
		assertEquals(true, visitor.getAnimalInfo().isDoge());
		
		visitor = result.getVisitors().get(1);
		assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
		assertEquals(2, visitor.accounts.length);
		assertEquals(17408.0, (Object)visitor.accounts[0]);
		assertEquals(0.0, (Object)visitor.accounts[1]);
		assertNull(visitor.getAnimalInfo().getShiba());
		assertEquals(false, visitor.getAnimalInfo().isDoge());
		
		visitor = result.getVisitors().get(2);
		assertNull(visitor.checkinDate);
		assertNull(visitor.accounts);
	}
	
	@Test
	public void testInstantiateList() throws MakeSenseException
	{
	    @SuppressWarnings("unchecked")
        List<Visitor> visitors = (List<Visitor>)Shibe.makeSense("so such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"accounts\" is so -15.3 many wow and such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"accounts\" is so 42 very 3 also 0 many wow also such wow many", 
                LinkedList.class, Visitor.class);
	    
	    assertEquals(3, visitors.size());
	    
	    Visitor visitor = visitors.get(0);
        assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
        assertEquals(1, visitor.accounts.length);
        assertEquals(-13.375, (Object)visitor.accounts[0]);
        assertEquals("inu", visitor.getAnimalInfo().getShiba());
        assertEquals(true, visitor.getAnimalInfo().isDoge());
        
        visitor = visitors.get(1);
        assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
        assertEquals(2, visitor.accounts.length);
        assertEquals(17408.0, (Object)visitor.accounts[0]);
        assertEquals(0.0, (Object)visitor.accounts[1]);
        assertNull(visitor.getAnimalInfo().getShiba());
        assertEquals(false, visitor.getAnimalInfo().isDoge());
        
        visitor = visitors.get(2);
        assertNull(visitor.checkinDate);
        assertNull(visitor.accounts);
	}
	
	@Test
	public void testInstantiateArray() throws MakeSenseException
	{
	    Visitor[] visitors = (Visitor[])Shibe.makeSense("so such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"accounts\" is so -15.3 many wow and such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"accounts\" is so 42 very 3 also 0 many wow also such wow many",
	            Visitor[].class);
	    
	    assertEquals(3, visitors.length);
	    
	    Visitor visitor = visitors[0];
        assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
        assertEquals(1, visitor.accounts.length);
        assertEquals(-13.375, (Object)visitor.accounts[0]);
        assertEquals("inu", visitor.getAnimalInfo().getShiba());
        assertEquals(true, visitor.getAnimalInfo().isDoge());
        
        visitor = visitors[1];
        assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
        assertEquals(2, visitor.accounts.length);
        assertEquals(17408.0, (Object)visitor.accounts[0]);
        assertEquals(0.0, (Object)visitor.accounts[1]);
        assertNull(visitor.getAnimalInfo().getShiba());
        assertEquals(false, visitor.getAnimalInfo().isDoge());
        
        visitor = visitors[2];
        assertNull(visitor.checkinDate);
        assertNull(visitor.accounts);
	}
	
	
	public static class DogeHotel
	{
	    public byte rating;
	    public short storyCount;
	    public int windowCount;
	    private float area;
		private String address;
        public List<AnimalInfo[]> employees;
		private List<Room> rooms;
		private List<Visitor> visitors = new ArrayList<Visitor>();
		
		public float getArea()
		{
		    return area;
		}
		
		public void setArea(float newArea)
		{
		    area = newArea;
		}
		
		public String getAddress()
		{
			return address;
		}
		
		public void setAddress(String newAddress)
		{
			address = newAddress;
		}
		
		public List<Room> getRooms()
		{
			return rooms;
		}
		
		public void setRooms(List<Room> newRooms)
		{
			rooms = newRooms;
		}
		
		public List<Visitor> getVisitors()
		{
			return visitors;
		}
	}
	
	public static class Room {}
	
	public static class Visitor 
	{
		public Date checkinDate;
		public double[] accounts;
		private AnimalInfo animalInfo = new AnimalInfo();
		
		public AnimalInfo getAnimalInfo()
		{
			return animalInfo;
		}
	}
	
	public static class AnimalInfo
	{
		private String shiba;
		private boolean doge;
		
		public String getShiba()
		{
			return shiba;
		}
		
		public void setShiba(String newShiba)
		{
			shiba = newShiba;
		}
		
		public boolean isDoge()
		{
			return doge;
		}
		
		public void setDoge(boolean flag)
		{
			doge = flag;
		}
	}
}
