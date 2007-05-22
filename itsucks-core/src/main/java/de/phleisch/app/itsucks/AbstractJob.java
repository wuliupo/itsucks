/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: Job.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.phleisch.app.itsucks.event.CoreEvents;
import de.phleisch.app.itsucks.event.JobEvent;

/**
 * A job is a single task to be done.
 * This is the abstract class for the basic functioniality.
 * 
 * @author olli
 *
 */
public abstract class AbstractJob extends Observable implements Serializable, Job {

	private int mId = -1;
	
	private String mName;
	private Map<String, JobParameter> mParameter;
	
	public AbstractJob() {
		mParameter = new HashMap<String, JobParameter>();
	}
	
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

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#run()
	 */
	public abstract void run() throws Exception;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#isClosed()
	 */
	public boolean isClosed() {
		return mState >= STATE_CLOSED;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#getJobManager()
	 */
	public JobManager getJobManager() {
		return mJobManager;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#setJobManager(de.phleisch.app.itsucks.JobManager)
	 */
	public void setJobManager(JobManager pJobManager) {
		mJobManager = pJobManager;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#isIgnoreFilter()
	 */
	public boolean isIgnoreFilter() {
		return mIgnoreFilter;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#setIgnoreFilter(boolean)
	 */
	public void setIgnoreFilter(boolean pIgnoreFilter) {
		mIgnoreFilter = pIgnoreFilter;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#getState()
	 */
	public int getState() {
		return mState;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#setState(int)
	 */
	public void setState(int pState) {
		if(pState == mState) return;
		
		synchronized (this) {
			mState = pState;
			this.setChanged();
			this.afterChange();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#getPriority()
	 */
	public int getPriority() {
		return mPriority;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#setPriority(int)
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
		
		sendEvent(new JobEvent(CoreEvents.EVENT_JOB_CHANGED, this));
		
	}

	private void sendEvent(JobEvent pEvent) {
		if(mJobManager == null) return;
		
		mJobManager.getEventManager().fireEvent(pEvent);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#getId()
	 */
	public int getId() {
		return mId;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#setId(int)
	 */
	public void setId(int pJobId) {
		if(mId != -1) 
			throw new IllegalStateException("Second change of the job id is not allowed. " +
					"(This leads to problems with the ordering in the job list.)");
		
		mId = pJobId;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#getName()
	 */
	public String getName() {
		return mName;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#setName(java.lang.String)
	 */
	public void setName(String pName) {
		mName = pName;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.Job#addParameter(de.phleisch.app.itsucks.JobParameter)
	 */
	public void addParameter(JobParameter pParameter) {
		if(pParameter != null) { 
			mParameter.put(pParameter.getKey(), pParameter);
		}
	}
	
	/**
	 * Returns the parameter under the given key or null.
	 * @param pKey
	 * @return
	 */
	public JobParameter getParameter(String pKey) {
		return mParameter.get(pKey);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.Job#getJobParameterList()
	 */
	public List<JobParameter> getParameterList() {
		return new ArrayList<JobParameter>(mParameter.values());
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJob#abort()
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
