package sg4j.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import sg4j.core.SpiderGraph;
import sg4j.exceptions.*;

class SpiderGraphDataManipulation
{
	@Test
	void addingRemovingSpiderGraph()
	{
		SpiderGraph sg = new SpiderGraph();

		sg.items().add("TestItem1");
		sg.items().add("TestItem2");

		assertNotEquals(null, sg.items().get("TestItem1"));
		assertNotEquals(null, sg.items().get("TestItem2"));

		sg.values().add("TestValue1", Color.RED);
		sg.values().add("TestValue2", Color.RED);

		assertNotEquals(null, sg.values().get("TestValue1"));
		assertNotEquals(null, sg.values().get("TestValue2"));

		assertEquals(2, sg.items().size());
		assertEquals(2, sg.values().size());

		assertTrue(sg.values().remove("TestValue2"));

		assertArrayEquals(new String[] { "TestValue1" }, sg.items().get("TestItem1").getValues().keySet().toArray());
		assertArrayEquals(new String[] { "TestValue1" }, sg.items().get("TestItem2").getValues().keySet().toArray());

		sg.items().add("TestItem3");

		assertArrayEquals(new String[] { "TestValue1" }, sg.items().get("TestItem3").getValues().keySet().toArray());

		sg.values().add("TestValue2", Color.RED);

		assertEquals(2, sg.items().get("TestItem1").getValues().keySet().size());
		assertEquals(2, sg.items().get("TestItem2").getValues().keySet().size());
		assertEquals(2, sg.items().get("TestItem3").getValues().keySet().size());
	}

	@Test
	void addingExceptions()
	{
		SpiderGraph sg = new SpiderGraph();
		sg.items().add("TestItem1");
		sg.values().add("TestValue1", Color.RED);

		assertThrows(IdenticalValueException.class, () -> sg.values().add("TestValue1", Color.RED));
		assertThrows(IdenticalItemException.class, () -> sg.items().add("TestItem1"));
	}
}
