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
import de.phleisch.app.itsucks.core.impl.DispatcherThread;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;
import de.phleisch.app.itsucks.persistence.util.DispatcherBuilder;

public class DispatcherHelper {

	private static Log mLog = LogFactory.getLog(DispatcherHelper.class);
	
	public DispatcherHelper() {
	}

	public DispatcherThread createDispatcher(SerializableJobPackage pJobList) {
		DispatcherThread dispatcher = (DispatcherThread) SpringContextSingelton
			.getApplicationContext().getBean("DispatcherThread");

		DispatcherBuilder.buildDispatcherFromJobPackage(dispatcher, pJobList);
		
		return dispatcher;
	}
	
	public void startDispatcher(DispatcherThread dispatcher) {
		
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
	
	public void stopDispatcher(DispatcherThread pDispatcher) {
		
		pDispatcher.stop();
		try {
			pDispatcher.join();
		} catch (InterruptedException e) {
			mLog.error(e, e);
		}

	}
	
}
