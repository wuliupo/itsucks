package de.phleisch.app.itsucks.configuration.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

public class PropertyFileConfigurationImplTest extends TestCase {
	
	public void testConfiguration() throws IOException {
		
		PropertyFileConfigurationImpl configuration = new PropertyFileConfigurationImpl();
		
		File tempFile = File.createTempFile("junit_property_file_test", ".property");
		tempFile.delete();
		tempFile.deleteOnExit();
		configuration.setSaveConfigurationPath(tempFile.getAbsolutePath());
		configuration.setDefaultConfigurationPath("/propertyFileConfigurationTest.properties");

		assertTrue(configuration.getValue("unit").equals("test"));
		
		configuration.setComment("Junit Test");
		configuration.setValue("Test", "Value");
		configuration.setValue("Date", new Date().toString());
		
		assertTrue(configuration.getValue("Test").equals("Value"));
		configuration.save();
		
		PropertyFileConfigurationImpl configuration2 = new PropertyFileConfigurationImpl();
		configuration2.setSaveConfigurationPath(tempFile.getAbsolutePath());
		
		assertTrue(configuration2.getValue("Test").equals("Value"));
		assertTrue(configuration2.getValue("unit").equals("test"));
		
	}

}
