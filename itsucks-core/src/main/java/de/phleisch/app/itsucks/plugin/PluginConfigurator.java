/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.08.2010
 */

package de.phleisch.app.itsucks.plugin;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;

import de.phleisch.app.itsucks.processing.DataProcessor;

public class PluginConfigurator {

	private Binder mBinder;
	
	public PluginConfigurator(Binder pBinder) {
		mBinder = pBinder;
	}
	
	public void addDataProcessor(Integer pId, Class<? extends DataProcessor> pDataProcessor) {
		
		MapBinder<Integer, DataProcessor> processorBinder
			= MapBinder.newMapBinder(getBinder(), Integer.class, DataProcessor.class);
		
		processorBinder.addBinding(pId).to(pDataProcessor);
	}
	
	public void addGuiceModule(Module pModule) {
		//TODO
	}
	
	public void addGuiceModuleWithOverwrite(Module pModule) {
		//TODO
	}

	public Binder getBinder() {
		return mBinder;
	}
	
}
