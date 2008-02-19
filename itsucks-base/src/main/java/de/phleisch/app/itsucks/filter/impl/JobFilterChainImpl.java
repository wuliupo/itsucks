/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.11.2007
 */

package de.phleisch.app.itsucks.filter.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.JobFilterChain;
import de.phleisch.app.itsucks.job.Job;

public class JobFilterChainImpl implements JobFilterChain {

	private static Log mLog = LogFactory.getLog(JobFilterChainImpl.class);
	
	private List<JobFilter> mJobFilter;
	private EventContext mContext;
	
	public JobFilterChainImpl() {
		
		mJobFilter = new ArrayList<JobFilter>();
	}

	public synchronized Job filterJob(Job pJob) {
		
		Job job = pJob;
		
		if(job.getState() != Job.STATE_OPEN) {
			throw new IllegalArgumentException("Job is not in state 'open'");
		}
		
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
		
		
		//overwrite the state which could be changed by the filters
		if(job.isIgnoreFilter()) {
			job.setState(Job.STATE_OPEN);
		}
		
		return job;
	}

	public void addJobFilter(JobFilter pJobFilter) {
		mJobFilter.add(pJobFilter);
		pJobFilter.setContext(mContext);
	}
	
	public void addJobFilter(List<JobFilter> pJobFilter) {
		for (JobFilter jobFilter : pJobFilter) {
			addJobFilter(jobFilter);
		}
	}

	public boolean removeJobFilter(JobFilter pJobFilter) {
		return mJobFilter.remove(pJobFilter);
	}
	
	public List<JobFilter> getJobFilter() {
		return mJobFilter;
	}

	public EventContext getContext() {
		return mContext;
	}

	public void setContext(EventContext pContext) {
		mContext = pContext;
	}

	
}
