package de.phleisch.app.itsucks.plugin;

import de.phleisch.app.itsucks.plugin.google.GoogleImageParseProcessor;

public class GoogleImagesPlugin implements Plugin {

	@Override
	public String getName() {
		return "Google Images Plugin";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public void configure(PluginConfigurator pConfiguration) {
		pConfiguration.addDataProcessor(90, GoogleImageParseProcessor.class);
	}

}
