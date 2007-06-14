/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: Dispatcher.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 *
 */

package de.phleisch.app.itsucks;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.event.CoreEvents;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.filter.JobFilter;

/**
 * The dispatcher is the central class to start an job.
 * It contains a JobManager which holds and manages all Jobs and a Worker
 * Pool which contains a thread pool to do the work.
 * 
 * @author olli
 *
 */
public class Dispatcher implements ApplicationContextAware {
	
	@SuppressWarnings("unused")
	private ApplicationContext mContext;
	
	private JobManager mJobManager;
	private WorkerPool mWorkerPool;
	private EventDispatcher mEventManager;
	
	private int mDispatchDelay = 0;
	private boolean mRunning;
	private boolean mStop;

	private boolean mPause;
	
	private static Log mLog = LogFactory.getLog(Dispatcher.class);
	
	public Dispatcher() {
		super();
	}
	
	/**
	 * Start processing the jobs in the job list and 
	 * delegate it to free worker threads.
	 * Returns when all jobs are done.
	 * @throws Exception
	 */
	public void processJobs() throws Exception{

		synchronized(this) {
			if(isRunning()) {
				return;
			} else {
				setRunning(true);
			}
		}
		
		mLog.info("Start processing jobs");
		mEventManager.fireEvent(CoreEvents.EVENT_EVENTDISPATCHER_CMD_START);
		mEventManager.fireEvent(CoreEvents.EVENT_DISPATCHER_START);
		
		startup();
		
		Job job;
		while(true) {
			
			if(mPause) {
				doPauseLoop();
			}
			
			
			if(mStop) { //check for stop event
				job = null;
			} else {
				job = mJobManager.getNextOpenJob();
			}
			
			if(job == null) {
				
				if(mWorkerPool.getBusyWorkerCount() > 0) {
					//stop dispatcher only when all working threads finished working
					Thread.sleep(100);
				} else {
					//shutdown dispatcher
					break;
				}
				
			} else {
				mWorkerPool.dispatchJob(job);
				
				if(mDispatchDelay > 0) {
					Thread.sleep(mDispatchDelay);
				}
				
			}
			
		}
		
		shutdown();
		setRunning(false);
		
		mLog.info("Finished processing jobs");
		mEventManager.fireEvent(CoreEvents.EVENT_DISPATCHER_FINISH);
		mEventManager.fireEvent(CoreEvents.EVENT_EVENTDISPATCHER_CMD_STOP);
	}

	private void doPauseLoop() throws InterruptedException {
		
		mEventManager.fireEvent(CoreEvents.EVENT_DISPATCHER_PAUSE);
		
		while(mPause) {
			synchronized (this) {
				this.wait(); // wait until unpause notify
			}
		}
		
		mEventManager.fireEvent(CoreEvents.EVENT_DISPATCHER_UNPAUSE);
	}

	/**
	 * Set the flag to stop the dispatcher
	 */
	public void stop() {
		unpause();
		mStop = true;
		mWorkerPool.abortBusyWorker();
	}
	
	/**
	 * Pause assigning new jobs to working threads.
	 */
	public void pause() {
		mPause = true;
	}
	
	/**
	 * Resume assigning new jobs to working threads.
	 */
	public void unpause() {
		if(mPause) {
			mPause = false;
			synchronized (this) {
				this.notify();
			}
		}
	}
	
	public boolean isPaused() {
		return mPause;
	}
	
	private void startup() {
		mWorkerPool.initialize();
	}
	
	private void shutdown() {
		mWorkerPool.shutdown();
	}

	public JobManager getJobManager() {
		return mJobManager;
	}

	public void setJobManager(JobManager pJobManager) {
		mJobManager = pJobManager;
		mEventManager = mJobManager.getEventManager();
	}

	public WorkerPool getWorkerPool() {
		return mWorkerPool;
	}

	public void setWorkerPool(WorkerPool pWorkerPool) {
		mWorkerPool = pWorkerPool;
	}
	
	public void setApplicationContext(ApplicationContext pContext) throws BeansException {
		mContext = pContext;
	}

	public EventDispatcher getEventManager() {
		return mEventManager;
	}
	
	/**
	 * Gets the waiting time between starting two jobs. 
	 * @param pDispatchDelay
	 */
	public int getDispatchDelay() {
		return mDispatchDelay;
	}

	/**
	 * Sets the waiting time between starting two jobs. 
	 * @param pDispatchDelay
	 */
	public void setDispatchDelay(int pDispatchDelay) {
		mDispatchDelay = pDispatchDelay;
	}

	public boolean isRunning() {
		return mRunning;
	}

	private void setRunning(boolean pRunning) {
		mRunning = pRunning;
	}

	//Delegate methods for easy use

	public void addJob(Job pJob) {
		mJobManager.addJob(pJob);
	}

	public void addJobFilter(JobFilter pJobFilter) {
		mJobManager.addJobFilter(pJobFilter);
	}

	public void addJobFilter(List<JobFilter> pJobFilter) {
		mJobManager.addJobFilter(pJobFilter);
	}

}
