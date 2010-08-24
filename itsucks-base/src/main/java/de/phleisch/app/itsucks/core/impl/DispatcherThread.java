/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 * Created on 03.03.2006
 *
 */

package de.phleisch.app.itsucks.core.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.core.WorkerPool;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobManager;

/**
 * Wrapper around the dispatcher to run the job processing in an new thread.
 * Use the join method to wait for the dispatcher thread to finish. 
 * 
 * @author olli
 */
public class DispatcherThread implements Dispatcher {

	private static Log mLog = LogFactory.getLog(DispatcherThread.class);
	private static int mCount = 0;
	
	private Thread mOwnThread = null;
	private InternalDispatcherThread mInternalDispatcherThread = null;
	private Dispatcher mDispatcher = null;
	
	private class InternalDispatcherThread implements Runnable {

		public void run() {
			try {
				mDispatcher.processJobs();
			} catch (Exception e) {
				mLog.error("Error running dispatcher", e);
			}
			
		}
		
	}

	@Inject
	public void setDispatcher(Dispatcher pDispatcher) {
		mDispatcher = pDispatcher;
	}
	
	public Dispatcher getDispatcher() {
		return mDispatcher;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.Dispatcher#processJobs()
	 */
	@Override
	public synchronized void processJobs() {
		if(mOwnThread != null && mOwnThread.isAlive()) {
			throw new IllegalArgumentException("Dispatcher Thread already running!");
		}
		
		mInternalDispatcherThread = new InternalDispatcherThread();
		mOwnThread = new Thread(mInternalDispatcherThread);
		mOwnThread.setName("DispatcherThread-" + ++mCount);
		mOwnThread.start();
	}

	public synchronized void join() throws InterruptedException {
		if(mOwnThread != null && mOwnThread.isAlive()) {
			mOwnThread.join();
		}
	}

	
	public void setName(String pName) {
		mDispatcher.setName(pName);
	}

	public String getName() {
		return mDispatcher.getName();
	}

	public boolean isRunning() {
		return mDispatcher.isRunning();
	}

	public void stop() {
		mDispatcher.stop();
	}

	public void pause() {
		mDispatcher.pause();
	}

	public void unpause() {
		mDispatcher.unpause();
	}

	public boolean isPaused() {
		return mDispatcher.isPaused();
	}

	public JobManager getJobManager() {
		return mDispatcher.getJobManager();
	}

	public WorkerPool getWorkerPool() {
		return mDispatcher.getWorkerPool();
	}

	public EventDispatcher getEventManager() {
		return mDispatcher.getEventManager();
	}

	public EventContext getContext() {
		return mDispatcher.getContext();
	}

	public int getDispatchDelay() {
		return mDispatcher.getDispatchDelay();
	}

	public void setDispatchDelay(int pDispatchDelay) {
		mDispatcher.setDispatchDelay(pDispatchDelay);
	}

	public void addJob(Job pJob) {
		mDispatcher.addJob(pJob);
	}

	public void addJobFilter(JobFilter pJobFilter) {
		mDispatcher.addJobFilter(pJobFilter);
	}

	public void addJobFilter(List<JobFilter> pJobFilter) {
		mDispatcher.addJobFilter(pJobFilter);
	}

}
