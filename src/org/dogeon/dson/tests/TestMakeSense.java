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
    public void testSimple() throws MakeSenseException
    {
        String text = (String)Shibe.makeSense("\"some text\"");
        assertEquals("some text", text);
        
        long number = (Long)Shibe.makeSense("123");
        assertEquals(83, number);
        
        assertNull(Shibe.makeSense("empty"));
    }
    
	@Test
	public void testThing() throws MakeSenseException
	{
		DogeThing result = (DogeThing)Shibe.makeSense("such \"address\" is \"123 \\\"fizz-buzz\\\"\", \"rooms\" is so many. \"visitors\" is so such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"account\" is -15.3 wow and such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"account\" is 42 very 3 wow also such wow many wow");
		
		assertEquals("123 \"fizz-buzz\"", result.get("address"));
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
		assertEquals(39304.0, visitor.get("account"));
		animalInfo = (DogeThing)visitor.get("animalInfo");
		assertEquals(null, animalInfo.get("shiba"));
		assertEquals(false, animalInfo.get("doge"));
		
		visitor = (DogeThing)visitors.get(2);
		assertEquals(0, visitor.keySet().size());
	}
	
	@Test
	public void testInstantiateThing() throws MakeSenseException
	{
		DogeHotel result = (DogeHotel)Shibe.makeSense("such \"address\" is \"123 \\\"fizz-buzz\\\"\", \"rooms\" is so many. \"visitors\" is so such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"account\" is -15.3 wow and such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"account\" is 42 very 3 wow also such wow many wow", 
		        DogeHotel.class);
		
		assertEquals("123 \"fizz-buzz\"", result.getAddress());
		assertEquals(0, result.getRooms().size());
		assertEquals(3, result.getVisitors().size());
		
		Visitor visitor = result.getVisitors().get(0);
		assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
		assertEquals(-13.375, (Object)visitor.getAccount());
		assertEquals("inu", visitor.getAnimalInfo().getShiba());
		assertEquals(true, visitor.getAnimalInfo().isDoge());
		
		visitor = result.getVisitors().get(1);
		assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
		assertEquals(39304.0, (Object)visitor.getAccount());
		assertEquals(null, visitor.getAnimalInfo().getShiba());
		assertEquals(false, visitor.getAnimalInfo().isDoge());
		
		visitor = result.getVisitors().get(2);
		assertEquals(null, visitor.checkinDate);
		assertEquals(0.0, (Object)visitor.getAccount());
		assertEquals(null, visitor.getAnimalInfo());
	}
	
	@Test
	public void testInstantiateList() throws MakeSenseException
	{
	    @SuppressWarnings("unchecked")
        List<Visitor> visitors = (List<Visitor>)Shibe.makeSense("so such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"account\" is -15.3 wow and such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"account\" is 42 very 3 wow also such wow many", 
                LinkedList.class, Visitor.class);
	    
	    Visitor visitor = visitors.get(0);
        assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
        assertEquals(-13.375, (Object)visitor.getAccount());
        assertEquals("inu", visitor.getAnimalInfo().getShiba());
        assertEquals(true, visitor.getAnimalInfo().isDoge());
        
        visitor = visitors.get(1);
        assertEquals((new GregorianCalendar(2014, 11, 15, 16, 23, 1)).getTime(), visitor.checkinDate);
        assertEquals(39304.0, (Object)visitor.getAccount());
        assertEquals(null, visitor.getAnimalInfo().getShiba());
        assertEquals(false, visitor.getAnimalInfo().isDoge());
        
        visitor = visitors.get(2);
        assertEquals(null, visitor.checkinDate);
        assertEquals(0.0, (Object)visitor.getAccount());
        assertEquals(null, visitor.getAnimalInfo());
	}
	
	
	public static class DogeHotel
	{
		private String address;
		private List<Room> rooms;
		private List<Visitor> visitors = new ArrayList<Visitor>();
		
		public String getAddress()
		{
			return address;
		}
		
		public void setAddress(String newAddress)
		{
			address = newAddress;
		}
		
		// TODO: support arrays.
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
		// TODO: support public fields.
		private Date checkinDate;
		private double account;
		private AnimalInfo animalInfo;
		
		public Date getCheckinDate()
		{
			return checkinDate;
		}
		
		public void setCheckinDate(Date newCheckinDate)
		{
			checkinDate = newCheckinDate;
		}
		
		public double getAccount()
		{
			return account;
		}
		
		public void setAccount(double newAccount)
		{
			account = newAccount;
		}
		
		public AnimalInfo getAnimalInfo()
		{
			return animalInfo;
		}
		
		public void setAnimalInfo(AnimalInfo newAnimalInfo)
		{
			animalInfo = newAnimalInfo;
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
