package de.phleisch.app.itsucks.configuration;

public interface ApplicationConfiguration {

	public void setValue(String pName, String pValue);
	
	public String getValue(String pName);
	
	public String getValue(String pName, String pDefaultValue);
	
	public void save();
	
}
