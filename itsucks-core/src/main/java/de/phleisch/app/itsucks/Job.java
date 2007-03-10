/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: Job.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks;

import java.io.Serializable;
import java.util.Observable;

/**
 * A job is a single task to be done.
 * This is the abstract class for the basic functioniality.
 * 
 * @author olli
 *
 */
public abstract class Job extends Observable implements Serializable {

	public final static int STATE_OPEN = 1;
	public final static int STATE_ASSIGNED = 2;
	public final static int STATE_IN_PROGRESS = 3;

	// every state over 50 is closed
	public final static int STATE_CLOSED = 50;
	public final static int STATE_FINISHED = 51;
	public final static int STATE_IGNORED = 52;
	public final static int STATE_ALREADY_PROCESSED = 53;
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

	private int mId = -1;
	
	private String mName;
	
	/**
	 * The State of the job.
	 */
	private int mState = STATE_OPEN;

	/**
	 * The higher the number, the higher the priority, max is 999, min is 0
	 */
	private int mPriority = 500;

	/**
	 * When set to true, the JobFilter will not filter out this job. Handy for
	 * manual added jobs.
	 */
	private boolean mIgnoreFilter = false;

	protected JobManager mJobManager;

	/**
	 * Starts the execution of the job.
	 * It returns when the job is finished.
	 *
	 */
	public abstract void run() throws Exception;

	/**
	 * Is called to free any resources in the job.
	 */
	public abstract void cleanup();
	
	/**
	 * @return true when job is closed.
	 */
	public boolean isClosed() {
		return mState >= STATE_CLOSED;
	}

	/**
	 * @return the job manager the job is managed by.
	 */
	public JobManager getJobManager() {
		return mJobManager;
	}

	/**
	 * Sets the jobManager for this job.
	 * @param pJobManager
	 */
	public void setJobManager(JobManager pJobManager) {
		mJobManager = pJobManager;
	}

	/**
	 * @return true when the filter should not be applied for this job.
	 */
	public boolean isIgnoreFilter() {
		return mIgnoreFilter;
	}

	/**
	 * When set to true, the JobFilter will not filter out this job. Handy for
	 * manual added jobs.
	 */
	public void setIgnoreFilter(boolean pIgnoreFilter) {
		mIgnoreFilter = pIgnoreFilter;
	}

	/**
	 * @return the current state of the Job. Check out the STATE_* constants for possible
	 * values.
	 */
	public int getState() {
		return mState;
	}

	/**
	 * Sets the current state of the job.
	 * The observers will be notificated from this change.
	 * @param pState
	 */
	public void setState(int pState) {
		if(pState == mState) return;
		
		synchronized (this) {
			mState = pState;
			this.setChanged();
			this.afterChange();
		}
	}
	
	/**
	 * The higher the number, the higher the priority, max is 999, min is 0
	 * Default is 500
	 * @return the current priority for this job.
	 */
	public int getPriority() {
		return mPriority;
	}

	/**
	 * The higher the number, the higher the priority, max is 999, min is 0
	 * Default is 500
	 * 
	 * @param pPriority
	 */
	public void setPriority(int pPriority) {
		if(pPriority == mPriority) return;
		
		if(pPriority < MIN_PRIORITY || pPriority > MAX_PRIORITY) {
			throw new IllegalArgumentException("Invalid priority: " + pPriority);
		}
		
		synchronized (this) {
			mPriority = pPriority;
			this.setChanged();
			this.afterChange();
		}
	}
	
	protected void afterChange() {
		this.notifyObservers(NOTIFICATION_CHANGE);
	}

	/**
	 * @return the unique id of this job.
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Sets the id of this job.
	 * Do this only when the job is not added to a job list!
	 * @param pJobId
	 */
	public void setId(int pJobId) {
		if(mId != -1) 
			throw new IllegalStateException("Second change of the job id is not allowed. " +
					"(This leads to problems with the ordering in the job list.)");
		
		mId = pJobId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String pName) {
		mName = pName;
	}

	/**
	 * Aborts the job when running.
	 */
	public abstract void abort();
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + mId;
		result = PRIME * result + ((mName == null) ? 0 : mName.hashCode());
		return result;
	}

}
