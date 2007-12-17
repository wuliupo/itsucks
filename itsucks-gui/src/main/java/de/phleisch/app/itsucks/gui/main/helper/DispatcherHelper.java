/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 16.12.2007
 */

package de.phleisch.app.itsucks.gui.main.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.core.impl.DispatcherList;
import de.phleisch.app.itsucks.core.impl.DispatcherThread;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.persistence.SerializableDispatcherConfiguration;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

public class DispatcherHelper {

	private static Log mLog = LogFactory.getLog(DispatcherHelper.class);
	
	private DispatcherList mDispatcherList;

	public DispatcherHelper(DispatcherList pDispatcherList) {
		mDispatcherList = pDispatcherList;
	}

	public void startDispatcher(SerializableJobList pJobList) {
		
		DispatcherThread dispatcher = (DispatcherThread) SpringContextSingelton
			.getApplicationContext().getBean("DispatcherThread");

		if (dispatcher == null) {
			throw new RuntimeException("Can't instatiate dispatcher!");
		}
		
		//set the name of the dispatcher
		dispatcher.setName(pJobList.getJobs().get(0).getName());
		
		//add the dispatcher to the list, the panel will be added by the event
		mDispatcherList.addDispatcher(dispatcher);
		
		//apply dispatcher configuration
		SerializableDispatcherConfiguration dispatcherConfiguration = pJobList
				.getDispatcherConfiguration();
		if (dispatcherConfiguration != null) {
			Integer dispatchDelay = dispatcherConfiguration.getDispatchDelay();
			if (dispatchDelay != null) {
				dispatcher.setDispatchDelay(dispatchDelay);
			}
		
			Integer workerThreads = dispatcherConfiguration.getWorkerThreads();
			if (workerThreads != null) {
				dispatcher.getWorkerPool().setSize(workerThreads);
			}
		}
		
		//configure dispatcher
		dispatcher.addJobFilter(pJobList.getFilters());
		for (Job job : pJobList.getJobs()) {
			dispatcher.addJob(job);
		}
		
		//add all context parameter
		dispatcher.getContext().putAllContextParameter(
				pJobList.getContextParameter());
		
		// start dispatcher thread
		try {
			dispatcher.processJobs();
		
		} catch (Exception e) {
			mLog.error("Error starting dispatcher thread", e);
		}
		
		// wait till dispatcher is starting
		for (int i = 0; i < 10 && !dispatcher.isRunning(); i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	public void stopDispatcher(DispatcherThread pDispatcher, int pDispatcherId) {
		
		pDispatcher.stop();
		try {
			pDispatcher.join();
		} catch (InterruptedException e) {
			mLog.error(e, e);
		}

		mDispatcherList.removeDispatcherById(pDispatcherId);

		//inform the gc that it would be a great opportunity to get some memory back
		System.gc();
		
	}
	
}
