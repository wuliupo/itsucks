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
import de.phleisch.app.itsucks.event.EventManager;
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
	
	private EventManager mEventManager;
	
	public JobManager() {
		super();
		
		mJobFilter = new ArrayList<JobFilter>();
	}
	
	private void addJobUnfiltered(Job pJob) {
		if(pJob == null) return;
		
		pJob.setJobManager(this);
		mJobList.addJob(pJob);
		
		mEventManager.fireEvent(
			new JobEvent(CoreEvents.EVENT_JOBMANAGER_JOB_ADDED, pJob));
	}
	
	public void addJob(Job pJob) {
		if(pJob == null) return;
		
		Job job = pJob;
		job.setJobManager(this);
		
		if(!job.isIgnoreFilter()) {
			job = filterJob(job);
			
			mEventManager.fireEvent(
					new JobEvent(CoreEvents.EVENT_JOBMANAGER_JOB_FILTERED, pJob));
		}
		
		addJobUnfiltered(job);
	}

	public boolean removeJob(Job pJob) {
		boolean result = mJobList.removeJob(pJob);
		
		if(result) {
			mEventManager.fireEvent(
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
	
	public EventManager getEventManager() {
		return mEventManager;
	}

	public void setEventManager(EventManager pEventManager) {
		mEventManager = pEventManager;
	}
	
}
