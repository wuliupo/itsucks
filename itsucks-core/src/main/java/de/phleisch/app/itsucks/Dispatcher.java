/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.09.2007
 */

package de.phleisch.app.itsucks;

import java.util.List;

import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.filter.JobFilter;

public interface Dispatcher {

	/**
	 * Start processing the jobs in the job list and 
	 * delegate it to free worker threads.
	 * Returns when all jobs are done.
	 * @throws Exception
	 */
	public abstract void processJobs() throws Exception;

	public abstract boolean isRunning();
	
	/**
	 * Set the flag to stop the dispatcher
	 */
	public abstract void stop();

	/**
	 * Pause assigning new jobs to working threads.
	 */
	public abstract void pause();

	/**
	 * Resume assigning new jobs to working threads.
	 */
	public abstract void unpause();

	public abstract boolean isPaused();

	public abstract JobManager getJobManager();

	public abstract WorkerPool getWorkerPool();

	public abstract EventDispatcher getEventManager();
	
	public abstract Context getContext();

	/**
	 * Gets the waiting time between starting two jobs. 
	 * @param pDispatchDelay
	 */
	public abstract int getDispatchDelay();

	/**
	 * Sets the waiting time between starting two jobs. 
	 * @param pDispatchDelay
	 */
	public abstract void setDispatchDelay(int pDispatchDelay);

	public abstract void addJob(Job pJob);

	public abstract void addJobFilter(JobFilter pJobFilter);

	public abstract void addJobFilter(List<JobFilter> pJobFilter);

}