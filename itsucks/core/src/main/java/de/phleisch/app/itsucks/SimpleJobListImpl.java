/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details.
 *  
 * $Id: JobList.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimpleJobListImpl extends Observable implements Observer, JobList {

	private SortedSet<JobListEntry> mJobList;
	private Map<Job, JobListEntry> mJobBackReference;
	private int mJobIdSequence = 0;
	
	public SimpleJobListImpl() {
		super();
		
		JobListEntryComparator comparator = new JobListEntryComparator();
		mJobList = new TreeSet<JobListEntry>(comparator);
		mJobBackReference = new HashMap<Job, JobListEntry>();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJobList#addJob(de.phleisch.app.itsucks.Job)
	 */
	public void addJob(Job pJob) {
		synchronized (this) {
			pJob.setId(mJobIdSequence ++);
			pJob.addObserver(this);
			
			JobListEntry entry = new JobListEntry(pJob);
			
			boolean b = mJobList.add(entry);
			if(!b) {
				throw new IllegalStateException("Job could not be added!");
			}
			mJobBackReference.put(entry.mJob, entry);
			
			setChanged();
		}
		
		this.notifyObservers(
				new JobListNotification(NOTIFICATION_JOB_ADDED, pJob));
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IJobList#removeJob(de.phleisch.app.itsucks.Job)
	 */
	public void removeJob(Job pJob) {
		synchronized (this) {
			pJob.deleteObserver(this);
			
			JobListEntry entry = new JobListEntry(pJob);
			
			boolean b = mJobList.remove(new JobListEntry(pJob));
			if(!b) {
				throw new IllegalStateException("Job to be removed not found!");
			}
			mJobBackReference.remove(entry.mJob);
			
			setChanged();
		}
		
		this.notifyObservers(
				new JobListNotification(NOTIFICATION_JOB_REMOVED, pJob));
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
			Job job = mJobList.first().mJob;
			if(job.getState() == Job.STATE_OPEN) { 
				return job;
			}
			
		}
		
		return null;
	}

	/**
	 * Is called when a job in the list has changed.
	 */
	public void update(Observable pO, Object pArg) {
		
		/**
		 * When a job has changed, the ordering of the list must be refreshed
		 */
		
		if((Integer)pArg == Job.NOTIFICATION_CHANGE) {
			Job changedJob = (Job)pO;
			boolean b;
			
			synchronized (this) {
				
				//search the job in the back reference map to get it's entry object
				JobListEntry entry = mJobBackReference.get(changedJob);
				if(entry == null) throw new IllegalStateException("Job not found!");
				
				//remove the job entry, because the position in the list is now wrong
				b = mJobList.remove(entry);
				if(!b) throw new IllegalStateException("Changed Job could not be removed!");
				
				//reset the order key to reflect the changes of the job
				entry.resetOrderKey();
				
				//readd the job entry to order it correctly in
				b = mJobList.add(entry);
				if(!b) throw new IllegalStateException("Changed Job could not be added!");
			}
			
			this.notifyObservers(
					new JobListNotification(NOTIFICATION_JOB_CHANGED, changedJob));
			
		} else if((Integer)pArg == Job.NOTIFICATION_PROGRESS) {
			Job changedJob = (Job)pO;
			
			this.notifyObservers(
					new JobListNotification(NOTIFICATION_JOB_CHANGED, changedJob));
		}
	}
	

	private class JobListEntry {
		
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
		
	}

	private class JobListEntryComparator implements Comparator<JobListEntry> {

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
