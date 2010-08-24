package de.phleisch.app.itsucks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.core.WorkerPool;
import de.phleisch.app.itsucks.core.impl.DispatcherImpl;
import de.phleisch.app.itsucks.core.impl.DispatcherThread;
import de.phleisch.app.itsucks.core.impl.WorkerPoolImpl;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.impl.AsynchronEventDispatcherImpl;
import de.phleisch.app.itsucks.filter.JobFilterChain;
import de.phleisch.app.itsucks.filter.impl.JobFilterChainImpl;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.JobManager;
import de.phleisch.app.itsucks.job.impl.CleanJobManagerImpl;
import de.phleisch.app.itsucks.job.impl.SimpleJobListImpl;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.impl.DataProcessorManager;

public class BaseModule extends AbstractModule {
	@Override 
	protected void configure() {
		
		//load default properties
		loadProperties(binder());
		
		//base
		bind(Context.class).to(EventContext.class);
		bind(EventDispatcher.class).to(AsynchronEventDispatcherImpl.class);

		//job
		bind(JobFilterChain.class).to(JobFilterChainImpl.class);
		bind(JobManager.class).to(CleanJobManagerImpl.class);
		bind(JobList.class).to(SimpleJobListImpl.class);
		bind(WorkerPool.class).to(WorkerPoolImpl.class);
		
		bind(Dispatcher.class).to(DispatcherImpl.class);
		bind(DispatcherThread.class);

		//init empty processor chain
		@SuppressWarnings("unused")
		MapBinder<Integer, DataProcessor> processorBinder
			= MapBinder.newMapBinder(binder(), Integer.class, DataProcessor.class);
		bind(DataProcessorManager.class);
	}

	private void loadProperties(Binder binder) {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("base_default_settings.properties");
		Properties appProperties = new Properties();
		try {
			appProperties.load(stream);
			Names.bindProperties(binder, appProperties);
		} catch (IOException e) {
			// This is the preferred way to tell Guice something went wrong
			binder.addError(e);
		}
	}

}