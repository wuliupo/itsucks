/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 08.04.2007
 */

package de.phleisch.app.itsucks.core;

import de.phleisch.app.itsucks.job.Job;

/**
 * The worker pool manages a pool of worker threads.
 * Every worker can be assigned one or multiple jobs. This assignment controls the Worker Pool
 * with the <code>dispatchJob</code> method.
 * 
 * The worker pool is used by the dispatcher.  
 * 
 * @author olli
 */
public interface WorkerPool {

	public abstract void initialize();

	public abstract void shutdown();

	public abstract void setSize(int pSize);

	public abstract WorkerThread getFreeWorker();

	public abstract void returnWorker(WorkerThread pWorker);

	public abstract WorkerThread waitForFreeWorker()
			throws InterruptedException;
	
	public abstract void dispatchJob(Job job) 
			throws InterruptedException;

	public abstract int getBusyWorkerCount();

	public abstract void abortBusyWorker();

}