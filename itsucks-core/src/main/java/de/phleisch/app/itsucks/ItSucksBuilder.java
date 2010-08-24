/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 21.08.2010
 */

package de.phleisch.app.itsucks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.core.impl.DispatcherThread;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

/**
 * Central class to build download jobs and job dispatcher.
 * @author olli
 *
 */
public class ItSucksBuilder {

	@SuppressWarnings("unused")
	private static Log mLog = LogFactory.getLog(ItSucksBuilder.class);
	
	protected Injector mInjector;

	private DownloadJobFactory mJobFactory;
	
	public ItSucksBuilder() {
		initialize();
	}
	
	public ItSucksBuilder(Injector pInjector) {
		initialize(pInjector);
	}
	
	protected void initialize() {
	    initialize(Guice.createInjector(new ItSucksModule()));
	}
	
	protected void initialize(Injector pInjector) {
	    mInjector = pInjector;
	    mJobFactory = getInstance(DownloadJobFactory.class);
	}

	/**
	 * Builds a dispatcher which runs in the same thread as the caller method (blocking).
	 * @return
	 */
	public Dispatcher buildDispatcher() {
		return mInjector.getInstance(Dispatcher.class);
	}
	
	/**
	 * Builds a dispatcher which runs in an own thread (non-blocking).  
	 * @return
	 */
	public DispatcherThread buildDispatcherThread() {
		return mInjector.getInstance(DispatcherThread.class);
	}

	/**
	 * Creates a new download job which can be added to a dispatcher.
	 * @return
	 */
	public UrlDownloadJob createDownloadJob() {
		return mJobFactory.createDownloadJob();
	}

	/**
	 * Returns the used guice injector.
	 * @return
	 */
	public Injector getInjector() {
		return mInjector;
	}

	/**
	 * Delegate to guice injector.
	 */
	public <T> T getInstance(Class<T> pArg0) {
		return mInjector.getInstance(pArg0);
	}
	
}
