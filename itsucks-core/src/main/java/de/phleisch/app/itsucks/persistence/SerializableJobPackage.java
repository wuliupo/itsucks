/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.03.2007
 */

package de.phleisch.app.itsucks.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.job.Job;

/**
 * This class is a simple serializable list for jobs and job filters.
 * 
 * @author olli
 *
 */
public class SerializableJobPackage implements Serializable {

	private static final long serialVersionUID = -8024689120690796652L;

	private List<Job> mJobs;
	private List<JobFilter> mFilters;
	private Map<String, Object> mContextParameter;
	private SerializableDispatcherConfiguration mDispatcherConfiguration;
	
	public SerializableJobPackage() {
		mJobs = new ArrayList<Job>();
		mFilters = new ArrayList<JobFilter>();
		mContextParameter = new HashMap<String, Object>();
	}
	
	public void addJob(Job pJob) {
		mJobs.add(pJob);
	}
	
	public void removeJob(Job pJob) {
		mJobs.remove(pJob);
	}
	
	public void addFilter(JobFilter pJobFilter) {
		mFilters.add(pJobFilter);
	}
	
	public void removeFilter(JobFilter pJobFilter) {
		mFilters.remove(pJobFilter);
	}

	public List<JobFilter> getFilters() {
		return mFilters;
	}

	public void setFilters(List<JobFilter> pFilterList) {
		mFilters = pFilterList;
	}

	public List<Job> getJobs() {
		return mJobs;
	}

	public void setJobs(List<Job> pJobs) {
		mJobs = pJobs;
	}

	public Map<String, Object> getContextParameter() {
		return mContextParameter;
	}
	
	public Object getContextParameter(String pKey) {
		return mContextParameter.get(pKey);
	}
	
	public void putContextParameter(String pKey, Object pValue) {
		mContextParameter.put(pKey, pValue);
	}

	public void setContextParameter(Map<String, Object> pContextParameter) {
		mContextParameter = pContextParameter;
	}

	public SerializableDispatcherConfiguration getDispatcherConfiguration() {
		return mDispatcherConfiguration;
	}

	public void setDispatcherConfiguration(
			SerializableDispatcherConfiguration pDispatcherConfiguration) {
		mDispatcherConfiguration = pDispatcherConfiguration;
	}
	
	public Job getFirstJob() {
		if(mJobs != null && mJobs.size() > 0) {
			return mJobs.get(0);
		} else {
			return null;
		}
	}
	
	public JobFilter getFilterByType(Class<? extends JobFilter> pFilterType) {
	
		for (JobFilter jobFilter : mFilters) {
			if(pFilterType.isAssignableFrom(jobFilter.getClass())) {
				return jobFilter;
			}
		}
		
		return null;
	}
}
