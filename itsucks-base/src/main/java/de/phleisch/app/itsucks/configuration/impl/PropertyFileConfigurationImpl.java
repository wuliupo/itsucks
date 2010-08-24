package de.phleisch.app.itsucks.configuration.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.phleisch.app.itsucks.configuration.ApplicationConfiguration;

public class PropertyFileConfigurationImpl implements ApplicationConfiguration {

	protected Properties mConfiguration = null;
	protected String mSaveConfigurationPath = null;
	protected String mDefaultConfigurationPath = null;
	protected String mComment;
	
	public synchronized String getValue(String pName) {
		
		if(mConfiguration == null) {
			load();
		}
		
		return mConfiguration.getProperty(pName);
	}


	public synchronized String getValue(String pName, String pDefaultValue) {
		String value = getValue(pName);
		if(value == null) {
			value = pDefaultValue;
		}
		
		return value;
	}

	public synchronized void setValue(String pName, String pValue) {
		
		if(mConfiguration == null) {
			load();
		}
		
		mConfiguration.setProperty(pName, pValue);
	}

	protected void load() {
		
		Properties properties = new Properties();
		InputStream in = null;
		try {
			
			//load default configuration
			in = findDefaultConfiguration();
			if(in != null) {
				properties.load(in);
			} else if(mDefaultConfigurationPath != null) {
				throw new RuntimeException("Default configuration not found: " + mDefaultConfigurationPath);
			}
			
			//load configuration
			in = findConfiguration();
			if(in != null) {
				properties.load(in);
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Could not load default configuration.", e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}


		
		this.mConfiguration = properties;
	}
	
	public synchronized void save() {
		
		OutputStream out = null;
		try {
			File output = new File(mSaveConfigurationPath);
			output.getParentFile().mkdirs();
			out = new FileOutputStream(output);
			
			mConfiguration.store(out, mComment);
			
		} catch (IOException e) {
			throw new RuntimeException("Could not store configuration.", e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	

	protected InputStream findDefaultConfiguration() throws IOException,
			FileNotFoundException {
		
		InputStream in = null;
		
		//try classpath
		if(mDefaultConfigurationPath != null && in == null) {
			URL resource = 
				getClass().getResource(mDefaultConfigurationPath);

			if(resource != null) {
				in = resource.openStream();
			}
		}
		
		//try filesystem
		if(mDefaultConfigurationPath != null && in == null) {
			File file = new File(mDefaultConfigurationPath);
			if(file.exists()) {
				in = new FileInputStream(file);
			}
		}
		
		return in;
	}	
	
	protected InputStream findConfiguration() throws IOException,
			FileNotFoundException {
		
		InputStream in = null;
		
		if(mSaveConfigurationPath != null) {
			File file = new File(mSaveConfigurationPath);
			if(file.exists()) {
				in = new FileInputStream(file);
			}
		}
		
		return in;
	}


	public String getSaveConfigurationPath() {
		return mSaveConfigurationPath;
	}

	@Inject
	public void setSaveConfigurationPath(
			@Named("propertyFileConfiguration.saveConfigurationPath") String pSaveConfigurationPath) {
		
		//replace home directory
		pSaveConfigurationPath = 
			pSaveConfigurationPath.replace("$USER_HOME$", System.getProperty("user.home"));
		
		mSaveConfigurationPath = pSaveConfigurationPath;
	}

	public String getDefaultConfigurationPath() {
		return mDefaultConfigurationPath;
	}

	@Inject
	public void setDefaultConfigurationPath(
			@Named("propertyFileConfiguration.defaultConfigurationPath") String pDefaultConfigurationPath) {
		mDefaultConfigurationPath = pDefaultConfigurationPath;
	}

	public String getComment() {
		return mComment;
	}

	@Inject
	public void setComment(@Named("propertyFileConfiguration.comment") String pComment) {
		mComment = pComment;
	}

}
