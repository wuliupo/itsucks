/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.04.2007
 */

package de.phleisch.app.itsucks.job;

import java.beans.PropertyChangeListener;
import java.util.List;

import de.phleisch.app.itsucks.context.Context;


/**
 * A job is a single task to be done.
 * 
 * @author olli
 *
 */
public interface Job {

	/**
	 * The job is open and waits to be assigned.
	 */
	public final static int STATE_OPEN = 1;

	/**
	 * The job is assigned to an worker thread.
	 */
	public final static int STATE_ASSIGNED = 2;

	/**
	 * The job processing is in progress.
	 */
	public final static int STATE_IN_PROGRESS = 3;

	// every state over 50 is closed
	/**
	 * The job is closed and not longer in progress.
	 */
	public final static int STATE_CLOSED = 50;

	/**
	 * This job is set to ignored by an filter.
	 */
	public final static int STATE_IGNORED = 60;

	/**
	 * This job was already processed.
	 */
	public final static int STATE_ALREADY_PROCESSED = 61;
	
	/**
	 * The job processing failed.
	 */
	public final static int STATE_FAILED = 70;
	
	/**
	 * The job finished without error.
	 */
	public final static int STATE_FINISHED = 71;

	/**
	 * The job processing has been aborted by an error.
	 */
	public final static int STATE_ERROR = 99;

	//All notifications which can occur when observing this class
	/**
	 * Constant used to determine when the priority property has changed.
	 * The old value in the PropertyChangeEvent will be the old priority
	 * and the new value will be the new priority.
	 * 
	 * @see #getPriority
	 * @see #addPropertyChangeListener
	 */
	public static final String JOB_PRIORITY_PROPERTY = "Priority";

	/**
	 * Constant used to determine when the state property has changed.
	 * The old value in the PropertyChangeEvent will be the old state
	 * and the new value will be the new state.
	 * 
	 * @see #getState
	 * @see #addPropertyChangeListener
	 */
	public static final String JOB_STATE_PROPERTY = "State";

	//the possible priority values 
	/**
	 * The possible maximum priority for an job.
	 */
	public final static int MAX_PRIORITY = 999;

	/**
	 * The possible minimum priority for an job.
	 */
	public final static int MIN_PRIORITY = 0;

	/**
	 * Starts the execution of the job.
	 * It returns when the job is finished.
	 *
	 */
	public abstract void run() throws Exception;

	/**
	 * Returns true when the job is closed.
	 * 
	 * @return true when job is closed.
	 */
	public abstract boolean isClosed();

	/**
	 * The job manager the job is managed by.
	 * 
	 * @return the job manager the job is managed by.
	 */
	public abstract JobManager getJobManager();

	/**
	 * Sets the jobManager for this job.
	 * @param pJobManager
	 */
	public abstract void setJobManager(JobManager pJobManager);

	/**
	 * The context for this job group.
	 * 
	 * @return the context the job belongs.
	 */
	public abstract Context getGroupContext();

	/**
	 * Sets the context for this job group.
	 * 
	 * @param pContext the context the job belongs.
	 */
	public abstract void setGroupContext(Context pContext);
	
	/**
	 * Returns true when the filter should not be applied for this job. 
	 * 
	 * @return 
	 */
	public abstract boolean isIgnoreFilter();

	/**
	 * When set to true, the JobFilter will not filter out this job. Handy for
	 * manual added jobs.
	 */
	public abstract void setIgnoreFilter(boolean pIgnoreFilter);

	/**
	 * Returns the current state of the Job. Check out the STATE_* constants for possible
	 * values.
	 * 
	 * @return the current state of the Job.
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
	 * Returns the unique id of this job.
	 * @return the unique id of this job.
	 */
	public abstract int getId();

	/**
	 * Sets the id of this job.
	 * Do this only when the job is not added to a job list!
	 * @param pJobId
	 */
	public abstract void setId(int pJobId);

	/**
	 * Gets the name of the job
	 * @return
	 */
	public abstract String getName();

	/**
	 * Sets the name of the job
	 * @param pName
	 */
	public abstract void setName(String pName);

	/**
	 * Add a parameter to the job
	 * @param pParameter
	 */
	public abstract void addParameter(JobParameter pParameter);
	
	/**
	 * Returns the parameter under the given key or null.
	 * @param pKey
	 * @return
	 */
	public abstract JobParameter getParameter(String pKey);
	
	/**
	 * Get a list of all job parameter
	 * @return
	 */
	public abstract List<JobParameter> getParameterList();
	
	/**
	 * Aborts the job when running.
	 */
	public abstract void abort();
	
    /**
     * Adds a PropertyChangeListener to the listener list.
     * The listener is registered for all Accessible properties and will
     * be called when those properties change.
     *
     * @see #JOB_PRIORITY_PROPERTY
     * @see #JOB_STATE_PROPERTY
     *
     * @param pListener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener pListener);

    /**
     * Removes a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param pListener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener pListener);
}