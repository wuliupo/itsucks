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

import de.phleisch.app.itsucks.filter.JobFilter;

/**
 * The job manager filters the incoming jobs and manages the internal job list. 
 * 
 * @author olli
 *
 */
public class JobManager {
	
	private static Log mLog = LogFactory.getLog(JobManager.class);

	private JobList mJobList;
	private List<JobFilter> mJobFilter;
	
	public JobManager() {
		super();
		
		mJobFilter = new ArrayList<JobFilter>();
	}

	private void addJobUnfiltered(Job pJob) {
		if(pJob == null) return;
		
		pJob.setJobManager(this);
		mJobList.addJob(pJob);
	}
	
	public void addJob(Job pJob) {
		Job job = pJob;

		if(!job.isIgnoreFilter()) {
			job = filterJob(job);
		}
		
		addJobUnfiltered(job);
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

	public List<JobFilter> getJobFilter() {
		return mJobFilter;
	}
	
}
