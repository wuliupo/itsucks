/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *  
 * $Id$
 * Created on 03.03.2006
 */ 

package de.phleisch.app.itsucks.job.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.EventSource;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.JobFilterChain;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.JobManager;
import de.phleisch.app.itsucks.job.event.JobAddedEvent;
import de.phleisch.app.itsucks.job.event.JobChangedEvent;
import de.phleisch.app.itsucks.job.event.JobEvent;

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
	private EventContext mGroupContext;
	
	private EventDispatcher mEventDispatcher;
	private JobFilterChain mJobFilterChain;
	
	private JobListEventObserver mJobListEventObserver;
	
	public FilterJobManagerImpl() {
		super();
		
		mJobListEventObserver = new JobListEventObserver();
	}
	
	private void addJobUnfiltered(Job pJob) {
		if(pJob == null) return;
		
		pJob.setJobManager(this);
		pJob.setGroupContext(mGroupContext);
		
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
		
		//notify listeners
		mEventDispatcher.fireEvent(
				new JobEvent(CoreEvents.EVENT_JOBMANAGER_JOB_FILTERED, pJob));
		
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
		} else {
			throw new IllegalArgumentException("Job not known.");
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
	@Inject
	public void setJobList(JobList pJobList) {
		
		if(mJobList != null) {
			//deregister from old job list
			mJobList.unregisterObserver(mJobListEventObserver);
		}
		
		mJobList = pJobList;
		
		if(mJobList != null) {
			//register to new job list
			pJobList.registerObserver(mJobListEventObserver);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#getEventDispatcher()
	 */
	public EventDispatcher getEventDispatcher() {
		return mEventDispatcher;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.impl.JobManager#getContext()
	 */
	public EventContext getContext() {
		return mGroupContext;
	}

	public JobFilterChain getJobFilterChain() {
		return mJobFilterChain;
	}

	@Inject
	public void setJobFilterChain(JobFilterChain pJobFilterChain) {
		mJobFilterChain = pJobFilterChain;
	}

	@Inject
	public void setContext(EventContext pContext) {
		mGroupContext = pContext;
		mEventDispatcher = mGroupContext.getEventDispatcher();
	}
	
	@PostConstruct
	public void postConstruct() {
		//propagate group context to filter chain
		mJobFilterChain.setContext(mGroupContext);
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

	private class JobListEventObserver implements EventObserver {
	
		public void processEvent(Event pEvent) {

			// If the job list sends an job changed event, convert it into
			// an job manager event and send it to the event dispatcher
			if (pEvent.equals(JobList.EVENT_JOB_CHANGED)) {

				processJobChangedEvent(pEvent);
			} 
		}

	}
	
	protected void processJobChangedEvent(Event pEvent) {
		JobChangedEvent changeEvent = (JobChangedEvent) pEvent;

		JobChangedEvent newEvent = new JobChangedEvent(
				CoreEvents.EVENT_JOBMANAGER_JOB_CHANGED, 
				changeEvent.getJob());
		newEvent.setPropertyChangeEvent(changeEvent.getPropertyChangeEvent());
		
		mEventDispatcher.fireEvent(newEvent);
	}
	
}
