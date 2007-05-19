/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.04.2007
 */

package de.phleisch.app.itsucks;

import java.util.Observer;

public interface Job {

	public final static int STATE_OPEN = 1;

	public final static int STATE_ASSIGNED = 2;

	public final static int STATE_IN_PROGRESS = 3;

	// every state over 50 is closed
	public final static int STATE_CLOSED = 50;

	public final static int STATE_IGNORED = 60;

	public final static int STATE_ALREADY_PROCESSED = 61;
	
	public final static int STATE_FAILED = 70;
	
	public final static int STATE_FINISHED = 71;

	public final static int STATE_ERROR = 99;

	//All notifications which can occur when observing this class
	/**
	 * This type of notification will be send if something in the Job changes.
	 * That could be: progress change, state change, priority change 
	 */
	public final static Integer NOTIFICATION_CHANGE = 100;

	/**
	 * This type of notification will be send if the progress has changed.
	 */
	public final static Integer NOTIFICATION_PROGRESS = 110;

	//the possible priority values 
	public final static int MAX_PRIORITY = 999;

	public final static int MIN_PRIORITY = 0;

	/**
	 * Starts the execution of the job.
	 * It returns when the job is finished.
	 *
	 */
	public abstract void run() throws Exception;

	/**
	 * @return true when job is closed.
	 */
	public abstract boolean isClosed();

	/**
	 * @return the job manager the job is managed by.
	 */
	public abstract JobManager getJobManager();

	/**
	 * Sets the jobManager for this job.
	 * @param pJobManager
	 */
	public abstract void setJobManager(JobManager pJobManager);

	/**
	 * @return true when the filter should not be applied for this job.
	 */
	public abstract boolean isIgnoreFilter();

	/**
	 * When set to true, the JobFilter will not filter out this job. Handy for
	 * manual added jobs.
	 */
	public abstract void setIgnoreFilter(boolean pIgnoreFilter);

	/**
	 * @return the current state of the Job. Check out the STATE_* constants for possible
	 * values.
	 */
	public abstract int getState();

	/**
	 * Sets the current state of the job.
	 * The observers will be notificated from this change.
	 * @param pState
	 */
	public abstract void setState(int pState);

	/**
	 * The higher the number, the higher the priority, max is 999, min is 0
	 * Default is 500
	 * @return the current priority for this job.
	 */
	public abstract int getPriority();

	/**
	 * The higher the number, the higher the priority, max is 999, min is 0
	 * Default is 500
	 * 
	 * @param pPriority
	 */
	public abstract void setPriority(int pPriority);

	/**
	 * @return the unique id of this job.
	 */
	public abstract int getId();

	/**
	 * Sets the id of this job.
	 * Do this only when the job is not added to a job list!
	 * @param pJobId
	 */
	public abstract void setId(int pJobId);

	public abstract String getName();

	public abstract void setName(String pName);

	/**
	 * Aborts the job when running.
	 */
	public abstract void abort();

	
	public void addObserver(Observer o);
	public void deleteObserver(Observer o);
	
}