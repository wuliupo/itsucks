/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *  
 * $Id$
 * Created on 03.03.2006
 */ 

package de.phleisch.app.itsucks.job.impl;

import java.beans.PropertyChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.JobManagerConfiguration;
import de.phleisch.app.itsucks.job.event.JobChangedEvent;
import de.phleisch.app.itsucks.job.event.JobEvent;

public class CleanJobManagerImpl extends FilterJobManagerImpl {
	
	private static Log mLog = LogFactory.getLog(CleanJobManagerImpl.class);
	
	private EventObserver mJobListObserver;

	public CleanJobManagerImpl() {
		super();
		mJobListObserver = new JobListObserver();
	}
	
	@Override
	public void setJobList(JobList pJobList) {
		if(getJobList() != null) {
			getJobList().unregisterObserver(mJobListObserver);
		}
		
		super.setJobList(pJobList);
		
		if(getJobList() != null) {
			//register to new job list
			getJobList().registerObserver(mJobListObserver);
		}
	}	

	private class JobListObserver implements EventObserver {

		public void processEvent(Event pEvent) {

			if (pEvent.equals(JobList.EVENT_JOB_ADDED)) {
				JobEvent jobAddedEvent = (JobEvent) pEvent;
				
				if(jobAddedEvent.getJob().getState() == Job.STATE_IGNORED) {
					
					if(getConfiguration().isDropIgnoredJobs()) {
					
						mLog.debug("Remove ignored job: " + jobAddedEvent.getJob());
						
						//remove this job from the list
						removeJob(jobAddedEvent.getJob());
					}
				}
			}
			
			if (pEvent.equals(JobList.EVENT_JOB_CHANGED)) {
				JobChangedEvent changeEvent = (JobChangedEvent) pEvent;
				PropertyChangeEvent propertyChangeEvent = changeEvent.getPropertyChangeEvent();
				
				if (Job.JOB_STATE_PROPERTY.equals(propertyChangeEvent.getPropertyName())) {
					
					if((Integer)propertyChangeEvent.getNewValue() >= Job.STATE_CLOSED) {
					
						if(getConfiguration().isDropFinishedJobs()) {
							mLog.debug("Remove finished job: " + changeEvent.getJob());
							
							//remove this job from the list
							removeJob(changeEvent.getJob());
						}
					}
				}
			}
		}
	}
	
	private JobManagerConfiguration getConfiguration() {
		
		JobManagerConfiguration jobManagerConfiguration = (JobManagerConfiguration) getContext()
			.getContextParameter(JobManagerConfiguration.CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION);
		
		//use default 
		if(jobManagerConfiguration == null) {
			jobManagerConfiguration = new JobManagerConfiguration();
		}
		
		return jobManagerConfiguration;
	}

}
