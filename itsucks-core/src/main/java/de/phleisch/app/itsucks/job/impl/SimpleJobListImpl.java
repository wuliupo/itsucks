/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details.
 *  
 * $Id: JobList.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks.job.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.impl.SimpleDirectEventSource;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.event.JobChangedEvent;
import de.phleisch.app.itsucks.job.event.JobEvent;


/**
 * Default implementation of the JobList Interface.
 * It uses a combination of an Set and a Map to optimize sorting and accessing jobs.
 * 
 * @author olli
 *
 */
public class SimpleJobListImpl extends SimpleDirectEventSource implements JobList {

	private SortedSet<JobListEntry> mJobList;
	private Map<Job, JobListEntry> mJobBackReference;
	private int mJobIdSequence = 0;
	
	private JobPropertyChangeListener mJobListener;
	
	public SimpleJobListImpl() {
		super();
		
		JobListEntryComparator comparator = new JobListEntryComparator();
		mJobList = new TreeSet<JobListEntry>(comparator);
		mJobBackReference = new HashMap<Job, JobListEntry>();
		mJobListener = new JobPropertyChangeListener();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJobList#addJob(de.phleisch.app.itsucks.Job)
	 */
	public void addJob(Job pJob) {
		synchronized (this) {
			pJob.setId(mJobIdSequence ++);
			pJob.addPropertyChangeListener(mJobListener);
			
			JobListEntry entry = new JobListEntry(pJob);
			
			boolean b = mJobList.add(entry);
			if(!b) {
				throw new IllegalStateException("Job could not be added!");
			}
			mJobBackReference.put(entry.getJob(), entry);
		}
		
		Event event = new JobEvent(EVENT_JOB_ADDED, pJob);
		this.fireEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJobList#removeJob(de.phleisch.app.itsucks.Job)
	 */
	public boolean removeJob(Job pJob) {
		
		if(!mJobBackReference.containsKey(pJob)) return false;
		
		synchronized (this) {
			pJob.removePropertyChangeListener(mJobListener);
		
			JobListEntry entry = new JobListEntry(pJob);
			
			boolean b = mJobList.remove(new JobListEntry(pJob));
			if(!b) {
				throw new IllegalStateException("Job to be removed not found!");
			}
			mJobBackReference.remove(entry.getJob());
		}
		
		Event event = new JobEvent(EVENT_JOB_REMOVED, pJob);
		this.fireEvent(event);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJobList#clear()
	 */
	public void clear() {
		synchronized (this) {
			mJobList.clear();
			mJobBackReference.clear();
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJobList#getNextOpenJob()
	 */
	public Job getNextOpenJob() {

		synchronized (this) {
			
			if(!mJobList.isEmpty()) {
			
				Job job = mJobList.first().getJob();
				if(job.getState() == Job.STATE_OPEN) { 
					return job;
				}
				
			}
			
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.JobList#getContent()
	 */
	public Collection<Job> getContent() {
		
		List<Job> list;
		synchronized (this) {
			list = new ArrayList<Job>(mJobList.size());
			
			for (JobListEntry entry : mJobList) {
				list.add(entry.getJob());
			}
			
		}
		
		return list;
	}
	
	private class JobPropertyChangeListener implements PropertyChangeListener {
		
		/**
		 * Is called when a job in the list has changed.
		 */
		public void propertyChange(PropertyChangeEvent pEvt) {
			
			/**
			 * When a job has changed, the ordering of the list must be refreshed
			 */
			if(Job.JOB_STATE_PROPERTY.equals(pEvt.getPropertyName()) 
					|| Job.JOB_PRIORITY_PROPERTY.equals(pEvt.getPropertyName())) {
				
				Job changedJob = (Job)pEvt.getSource();
				handleJobChanged(changedJob, pEvt);
			}
		}
	}
		
	/**
	 * When a job has changed, the ordering of the list must be refreshed
	 * @param pEvt 
	 */
	protected void handleJobChanged(Job pJob, PropertyChangeEvent pEvt) {
		boolean b;
		
		synchronized (this) {
			
			//search the job in the back reference map to get it's entry object
			JobListEntry entry = mJobBackReference.get(pJob);
			if(entry == null) throw new IllegalStateException("Job not found!");
			
			//remove the job entry, because the position in the list is now wrong
			b = mJobList.remove(entry);
			if(!b) throw new IllegalStateException("Changed Job could not be removed!");
			
			//reset the order key to reflect the changes of the job
			entry.resetOrderKey();
			
			//read the job entry to order it correctly in
			b = mJobList.add(entry);
			if(!b) throw new IllegalStateException("Changed Job could not be added!");
		}
		
		JobChangedEvent event = new JobChangedEvent(EVENT_JOB_CHANGED, pJob);
		event.setPropertyChangeEvent(pEvt);
		this.fireEvent(event);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.JobList#size()
	 */
	public int size() {
		return mJobList.size();
	}

	private static class JobListEntry {
		
		private Job mJob = null;
		private Integer mOrderKey = null; // the cached order key
		
		public JobListEntry(Job pJob) {
			mJob = pJob;
		}

		protected int generateOrderKey() {
			return mJob.getState() * 1000 + (Job.MAX_PRIORITY - mJob.getPriority());
		}
	
		protected void resetOrderKey() {
			mOrderKey = null;
		}
		
		protected Integer getOrderKey() {
			if(mOrderKey == null) {
				mOrderKey = generateOrderKey();
			}
			
			return mOrderKey;
		}
		
		protected Job getJob() {
			return mJob;
		}
		
	}

	private static class JobListEntryComparator 
		implements Comparator<JobListEntry>, Serializable {

		private static final long serialVersionUID = 4661938276207607980L;

		/**
		 * Compares one job entry to another job entry. Used by the JobList to order
		 * jobs by state and priority.
		 * @param pO1
		 * @param pO2
		 * @return 1/0/-1
		 */
		public int compare(JobListEntry pO1, JobListEntry pO2) {
			
			int result = pO1.getOrderKey().compareTo((pO2).getOrderKey());
			
			if(result == 0) {
				int o1Id = pO1.mJob.getId();
				int o2Id = pO2.mJob.getId();
				result = (o1Id < o2Id ? -1 : (o1Id == o2Id ? 0 : 1));
			}
			
			return result;
		}

	}

}
