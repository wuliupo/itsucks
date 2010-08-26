/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.08.2010
 */

package de.phleisch.app.itsucks;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;

import de.phleisch.app.itsucks.plugin.Plugin;
import de.phleisch.app.itsucks.plugin.PluginConfigurator;
import de.phleisch.app.itsucks.util.GuiceUtils;

public class PluginModule extends AbstractModule {

	private static final Log mLog = LogFactory.getLog(PluginModule.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		
		Properties properties = GuiceUtils.loadProperties("plugin_settings.properties", binder());
		String packagePath = properties.getProperty("plugins.packagePath");
		mLog.info("Load plugins from package: " + packagePath);
		
		try {
			Iterable<Class<?>> classes = getClasses(packagePath);
			for (Class<?> clazz : classes) {
				if(clazz.isAssignableFrom(Plugin.class) && !clazz.isInterface()) {
					loadPlugin((Class<Plugin>) clazz);
				}
			}
		} catch (Exception e) {
			mLog.error("Error loading plugin from package path: " + packagePath, e);
			binder().addError(e);
		}
	}

	
	private void loadPlugin(Class<Plugin> pClazz) {
		try {
			Plugin plugin = (Plugin)pClazz.newInstance();
			initPlugin(plugin);
		} catch (Exception e) {
			mLog.error("Error instantiating plugin: " + pClazz.getName(), e);
			binder().addError(e);
		}
	}


	private void initPlugin(Plugin pPlugin) {
		mLog.info("Load plugin: " + pPlugin.getName() + " " + pPlugin.getVersion());
		
		PluginConfigurator configuration = new PluginConfigurator(binder());
		pPlugin.configure(configuration);
	}


	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Iterable<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException
	{
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements())
	    {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    for (File directory : dirs)
	    {
	        classes.addAll(findClasses(directory, packageName));
	    }

	    return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException
	{
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    if (!directory.exists())
	    {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files)
	    {
	        if (file.isDirectory())
	        {
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        }
	        else if (file.getName().endsWith(".class"))
	        {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}

	
}
