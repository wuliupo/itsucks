package de.phleisch.app.itsucks.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.Binder;
import com.google.inject.name.Names;

public class GuiceUtils {

	public static Properties loadProperties(String pFilename, Binder binder) {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(pFilename);
		Properties appProperties = new Properties();
		try {
			appProperties.load(stream);
			Names.bindProperties(binder, appProperties);
		} catch (IOException e) {
			// This is the preferred way to tell Guice something went wrong
			binder.addError(e);
		}
		return appProperties;
	}
	
}
