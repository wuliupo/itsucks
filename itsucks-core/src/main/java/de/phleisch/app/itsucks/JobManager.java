/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *  
 * $Id$
 * Created on 03.03.2006
 */ 

package de.phleisch.app.itsucks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.event.CoreEvents;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.JobAddedEvent;
import de.phleisch.app.itsucks.event.JobEvent;
import de.phleisch.app.itsucks.filter.JobFilter;

/**
 * The job manager filters the incoming jobs and manages the internal job list. 
 * 
 * The job manager is used by the dispatcher.  
 * 
 * @author olli
 *
 */
public class JobManager {
	
	private static Log mLog = LogFactory.getLog(JobManager.class);

	private JobList mJobList;
	private List<JobFilter> mJobFilter;
	
	private JobContext mJobContext;
	
	private EventDispatcher mEventDispatcher;
	
	public JobManager() {
		super();
		
		mJobFilter = new ArrayList<JobFilter>();
	}
	
	private void addJobUnfiltered(Job pJob) {
		if(pJob == null) return;
		
		pJob.setJobManager(this);
		
		int initialState = pJob.getState();
		JobAddedEvent event = 
			new JobAddedEvent(CoreEvents.EVENT_JOBMANAGER_JOB_ADDED, pJob);
		event.setInitialState(initialState);
		mEventDispatcher.fireEvent(event);
		
		
		mJobList.addJob(pJob);
	}
	
	public void addJob(Job pJob) {
		if(pJob == null) return;
		
		if(pJob.getState() != Job.STATE_OPEN) {
			throw new IllegalArgumentException("Job is not in state 'open'");
		}
		
		Job job = pJob;
		
		//apply the configured filter to this job
		job = filterJob(job);
		
		//overwrite the state which could be changed by the filters
		if(job.isIgnoreFilter()) {
			job.setState(Job.STATE_OPEN);
		}	
			
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

	public boolean removeJob(Job pJob) {
		boolean result = mJobList.removeJob(pJob);
		
		if(result) {
			mEventDispatcher.fireEvent(
				new JobEvent(CoreEvents.EVENT_JOBMANAGER_JOB_REMOVED, pJob));
		}
		
		return result;
	}
	
	private Job filterJob(Job pJob) {
		
		Job job = pJob;
		
		try {
		
			boolean filterFound = false;
			for (JobFilter filter : mJobFilter) {
				if(filter.supports(job)) {
					filterFound = true;
					job = filter.filter(job);
				}
			}
			
			if(!filterFound) {
				mLog.warn("No suitable filter found for job: " + job);
				job.setState(Job.STATE_ERROR);
			}
		
		} catch (Exception e) {
			mLog.error("Error filtering job", e);
			job.setState(Job.STATE_ERROR);
		}
		
		return job;
	}

	public Job getNextOpenJob() {
		return mJobList.getNextOpenJob();
	}

	public JobList getJobList() {
		return mJobList;
	}

	public void setJobList(JobList pJobList) {
		mJobList = pJobList;
	}

	public void setJobFilter(List<JobFilter> pJobFilter) {
		mJobFilter.addAll(pJobFilter);
	}

	public void addJobFilter(JobFilter pJobFilter) {
		mJobFilter.add(pJobFilter);
	}
	
	public void addJobFilter(List<JobFilter> pJobFilter) {
		mJobFilter.addAll(pJobFilter);
	}

	public boolean removeJobFilter(JobFilter pJobFilter) {
		return mJobFilter.remove(pJobFilter);
	}
	
	public List<JobFilter> getJobFilter() {
		return mJobFilter;
	}
	
	public EventDispatcher getEventDispatcher() {
		return mEventDispatcher;
	}

	public void setEventDispatcher(EventDispatcher pEventManager) {
		mEventDispatcher = pEventManager;
	}
	
}
