/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *  
 * $Id$
 * Created on 03.03.2006
 */ 

package de.phleisch.app.itsucks.job.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.EventSource;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.event.job.JobAddedEvent;
import de.phleisch.app.itsucks.event.job.JobEvent;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.JobFilterChain;
import de.phleisch.app.itsucks.job.Context;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.JobManager;

/**
 * The job manager filters the incoming jobs and manages the internal job list. 
 * 
 * The job manager is used by the dispatcher.  
 * 
 * @author olli
 *
 */
public class FilterJobManagerImpl implements JobManager, EventSource {
	
	private static Log mLog = LogFactory.getLog(FilterJobManagerImpl.class);

	private JobList mJobList;
	private Context mContext;
	
	private EventDispatcher mEventDispatcher;
	private JobFilterChain mJobFilterChain;
	
	public FilterJobManagerImpl() {
		super();
	}
	
	private void addJobUnfiltered(Job pJob) {
		if(pJob == null) return;
		
		pJob.setJobManager(this);
		pJob.setContext(mContext);
		
		// This event must be fired before adding the job to the list.
		// When adding the job, the job list changes the state and produces
		// an STATE_CHANGE event. 
		JobAddedEvent event = 
			new JobAddedEvent(CoreEvents.EVENT_JOBMANAGER_JOB_ADDED, pJob);
		event.setInitialState(pJob.getState());
		mEventDispatcher.fireEvent(event);
		
		mJobList.addJob(pJob);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#addJob(de.phleisch.app.itsucks.job.Job)
	 */
	public void addJob(Job pJob) {
		if(pJob == null) return;
		
		Job job = pJob;
		
		//apply the configured filter to this job
		job = mJobFilterChain.filterJob(job);
		
		//check if the job isn't already processed.
		if(job.getState() != Job.STATE_ALREADY_PROCESSED) {
			addJobUnfiltered(job);
			
		} else {
			//do not accept this job
			mLog.debug("Rejected job because it is already processed: " + job);
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#removeJob(de.phleisch.app.itsucks.job.Job)
	 */
	public boolean removeJob(Job pJob) {
		boolean result = mJobList.removeJob(pJob);
		
		if(result) {
			mEventDispatcher.fireEvent(
				new JobEvent(CoreEvents.EVENT_JOBMANAGER_JOB_REMOVED, pJob));
		}
		
		return result;
	}
	

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#getNextOpenJob()
	 */
	public Job getNextOpenJob() {
		return mJobList.getNextOpenJob();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#getJobList()
	 */
	public JobList getJobList() {
		return mJobList;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#setJobList(de.phleisch.app.itsucks.job.JobList)
	 */
	public void setJobList(JobList pJobList) {
		mJobList = pJobList;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#getEventDispatcher()
	 */
	public EventDispatcher getEventDispatcher() {
		return mEventDispatcher;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#setEventDispatcher(de.phleisch.app.itsucks.event.EventDispatcher)
	 */
	public void setEventDispatcher(EventDispatcher pEventManager) {
		mEventDispatcher = pEventManager;
		mContext.setEventDispatcher(mEventDispatcher);
		mJobFilterChain.setEventDispatcher(mEventDispatcher);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#getContext()
	 */
	public Context getContext() {
		return mContext;
	}

	public JobFilterChain getJobFilterChain() {
		return mJobFilterChain;
	}

	public void setJobFilterChain(JobFilterChain pJobFilterChain) {
		mJobFilterChain = pJobFilterChain;
	}

	public void setContext(Context pContext) {
		mContext = pContext;
	}

	
	public void addJobFilter(JobFilter pJobFilter) {
		mJobFilterChain.addJobFilter(pJobFilter);
	}

	public void addJobFilter(List<JobFilter> pJobFilter) {
		mJobFilterChain.addJobFilter(pJobFilter);
	}

	public boolean removeJobFilter(JobFilter pJobFilter) {
		return mJobFilterChain.removeJobFilter(pJobFilter);
	}

}
