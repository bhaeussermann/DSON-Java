package org.dogeon.dson.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.GregorianCalendar;

import org.dogeon.dson.Shibe;
import org.dogeon.dson.makesense.MakeSenseException;
import org.dogeon.dson.model.DogeList;
import org.dogeon.dson.model.DogeThing;
import org.junit.Test;

public class TestMakeSense 
{
	@Test
	public void test() throws IOException, MakeSenseException
	{
		DogeThing result = (DogeThing)Shibe.makeSense("such \"address\" is \"123 \\\"fizz-buzz\\\"\", \"rooms\" is so many. \"visitors\" is so such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is \"inu\", \"doge\" is yes wow. \"account\" is -15.3 wow and such \"checkinDate\" is \"2014-12-15T16:23:01.000Z\", \"animalInfo\" is such \"shiba\" is empty, \"doge\" is no wow. \"account\" is 42 very 3 wow also such wow many wow");
		
		assertEquals("123 \"fizz-buzz\"", result.get("address"));
		DogeList rooms = (DogeList)result.get("rooms");
		assertEquals(0, rooms.size());
		DogeList visitors = (DogeList)result.get("visitors");
		
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
}
