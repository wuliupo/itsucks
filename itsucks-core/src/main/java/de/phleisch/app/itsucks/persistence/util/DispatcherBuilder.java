/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 09.11.2009
 */

package de.phleisch.app.itsucks.persistence.util;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.persistence.SerializableDispatcherConfiguration;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

public class DispatcherBuilder {

	public static void buildDispatcherFromJobPackage(
			Dispatcher pDispatcher, SerializableJobPackage pJobList) {

		//set the name of the dispatcher
		pDispatcher.setName(pJobList.getJobs().get(0).getName());
		
		//apply dispatcher configuration
		SerializableDispatcherConfiguration dispatcherConfiguration = pJobList
				.getDispatcherConfiguration();
		if (dispatcherConfiguration != null) {
			Integer dispatchDelay = dispatcherConfiguration.getDispatchDelay();
			if (dispatchDelay != null) {
				pDispatcher.setDispatchDelay(dispatchDelay);
			}
		
			Integer workerThreads = dispatcherConfiguration.getWorkerThreads();
			if (workerThreads != null) {
				pDispatcher.getWorkerPool().setSize(workerThreads);
			}
		}
		
		//add all context parameter
		pDispatcher.getContext().putAllContextParameter(
				pJobList.getContextParameter());
		
		//configure dispatcher
		pDispatcher.addJobFilter(pJobList.getFilters());
		for (Job job : pJobList.getJobs()) {
			pDispatcher.addJob(job);
		}

	}
	
}
