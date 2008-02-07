/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.09.2007
 */

package de.phleisch.app.itsucks.core;

import java.util.List;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobManager;

public interface Dispatcher {

	/**
	 * Sets a name for the dispatcher.
	 * @param pName
	 */
	public abstract void setName(String pName);
	
	/**
	 * @return The name of the dispatcher.
	 */
	public abstract String getName();
	
	/**
	 * Start processing the jobs in the job list and 
	 * delegate it to free worker threads.
	 * Returns when all jobs are done.
	 * @throws Exception
	 */
	public abstract void processJobs();

	/**
	 * @return true when the dispatcher is running.
	 */
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

	/**
	 * @return true when the dispatcher is in paused.
	 */
	public abstract boolean isPaused();

	/**
	 * Returns the job manager instance.
	 * @return
	 */
	public abstract JobManager getJobManager();

	/**
	 * Returns the worker pool instance.
	 * @return
	 */
	public abstract WorkerPool getWorkerPool();

	/**
	 * Returns the event manager instance.
	 * @return
	 */
	public abstract EventDispatcher getEventManager();
	
	/**
	 * Returns the context for this dispatcher and all included jobs.
	 * @return
	 */
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

	/**
	 * Adds an single job to the dispatcher.
	 * The job must be in state STATE_OPEN.
	 * 
	 * @param pJob
	 */
	public abstract void addJob(Job pJob);

	/**
	 * Adds an additional job filter.
	 * @param pJobFilter
	 */
	public abstract void addJobFilter(JobFilter pJobFilter);

	/**
	 * Adds an list of job filter.
	 * @param pJobFilter
	 */
	public abstract void addJobFilter(List<JobFilter> pJobFilter);

}