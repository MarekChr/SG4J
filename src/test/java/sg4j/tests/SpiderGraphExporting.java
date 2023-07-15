package sg4j.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import sg4j.core.SpiderGraph;
import sg4j.core.SpiderGraphValueName;
import sg4j.exceptions.InvalidImageProperties;

class SpiderGraphExporting
{
	private static String resourcesPath;
	
	@BeforeAll
	static void savingImages() throws IOException, URISyntaxException
	{
		resourcesPath = Paths.get(SpiderGraphExporting.class.getResource("").toURI()).toString();
		
		SpiderGraph sg1 = new SpiderGraph();
		sg1.items().add("TestItem1");
		sg1.items().add("TestItem2");
		sg1.items().add("TestItem3");

		sg1.values().add(new SpiderGraphValueName("TestValue1", 10.0, Color.BLACK));
		sg1.values().add(new SpiderGraphValueName("TestValue2", 3.0, Color.BLUE));
		sg1.values().add(new SpiderGraphValueName("TestValue3", 2.0, Color.RED));
		sg1.setSlices(7);
		sg1.setFontSize(40);
		SpiderGraph sg2 = new SpiderGraph();
		sg2.items().add("TestItem1");
		sg2.items().add("TestItem2");
		sg2.items().add("TestItem3");

		sg2.values().add(new SpiderGraphValueName("TestValue1", 10.0, Color.BLACK));
		sg2.values().add(new SpiderGraphValueName("TestValue2", 3.0, Color.BLUE));
		sg2.values().add(new SpiderGraphValueName("TestValue3", 2.0, Color.RED));
		sg2.setSlices(7);
		sg2.setFontSize(40);
		
		sg1.save(resourcesPath + "testPic1");
		sg2.save(resourcesPath + "testPic2");

		assertTrue(Files.exists(Paths.get(resourcesPath + "testPic1.png").toAbsolutePath()));
		assertTrue(Files.exists(Paths.get(resourcesPath + "testPic2.png").toAbsolutePath()));
	}

	@Test
	void imageIntegrity()
	{
		File pic1 = new File(resourcesPath + "testPic1");
		File pic2 = new File(resourcesPath + "testPic2");

		assertEquals(pic1.length(), pic2.length());
	}

	@Test
	void imagePropertiesSavingExceptions()
	{
		SpiderGraph sg1 = new SpiderGraph();

		assertThrows(InvalidImageProperties.class, () -> sg1.save(resourcesPath + "testPicException", 400, "png"));
	}

	@AfterAll
	static void imagesCleanup()
	{
		File pic1 = new File(resourcesPath + "testPic1.png");
		File pic2 = new File(resourcesPath + "testPic2.png");
		File testpicException = new File(resourcesPath + "testPicException.png");
		if (testpicException.exists())
		{
			assertTrue(testpicException.delete());
		}
		assertTrue(pic1.delete());
		assertTrue(pic2.delete());
	}
}
