/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: WorkerThread.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.core.WorkerThread;
import de.phleisch.app.itsucks.core.WorkerPool;
import de.phleisch.app.itsucks.job.Job;


/**
 * This is a single worker to process a job.
 * It is manged by the WorkerPool.
 * 
 * The WorkerThread has an interface to add queued commands.
 * So multiple jobs or commands can be given to an workerthread which are
 * processed synchronously. 
 * 
 * @author olli
 *
 */
public class WorkerThreadImpl implements Runnable, WorkerThread {

	private static Log mLog = LogFactory.getLog(WorkerThreadImpl.class);
	
	private final static long CMD_POLL_INTERVAL = 50; 
	
	private List<Integer> mCommandQueue = new ArrayList<Integer>();
	private Job mJob;
	private boolean mShutdown = false;
	private WorkerPool mPool;
	
	private Thread mThread; 
	
	public WorkerThreadImpl(WorkerPool pWorkerPool, String pName) {
		mPool = pWorkerPool;
		mThread = new Thread(this, pName);
		mThread.setDaemon(true);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.core.impl.IWorkerThread#abort()
	 */
	public void abort() {
		if(mJob != null) {
			mJob.abort();
			mThread.interrupt();
		}
	}

	public void run() {
		
		try {
			waitForWork();
		} catch (InterruptedException e) {
			mLog.error("Worker Thread Interrupted", e);
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.core.impl.IWorkerThread#start()
	 */
	public void start() {
		mThread.start();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.core.impl.IWorkerThread#join(long)
	 */
	public void join(long pMillis) throws InterruptedException {
		mThread.join(pMillis);
	}
	
	private void waitForWork() throws InterruptedException {
		while(!mShutdown) {
			
			if(mCommandQueue.size() == 0) {
				try {
					Thread.sleep(CMD_POLL_INTERVAL);
				} catch(InterruptedException ex) {
					mLog.info("Worker Thread interrupted", ex);
				}
			} else {
				doWork();
			}
			
		}
	}

	private void doWork() {
		int cmd = mCommandQueue.get(0);
		mCommandQueue.remove(0);
		
		switch(cmd) {
		
			case CMD_SHUTDOWN:
				mShutdown = true;
				break;
				
			case CMD_PROCESS_JOB:
				mLog.info("Start working on job: " + mJob);
				mJob.setState(Job.STATE_IN_PROGRESS);
				
				try {
					mJob.run();
				} catch(Exception ex) {
					mJob.setState(Job.STATE_ERROR);
					mLog.error("Error executing job: " + mJob, ex);
				}
				
				mLog.info("Finished working on job: " + mJob);
				
				if(mJob.getState() == Job.STATE_IN_PROGRESS) {
					mJob.setState(Job.STATE_ERROR);
					mLog.error("Job does not finish correctly, state after processing is still 'in progress': " + mJob);
				}

				if(mJob.getState() == Job.STATE_OPEN) {
					mJob.setState(Job.STATE_ERROR);
					mLog.error("Job does not finish correctly, state after processing is 'open', please use state 'reopen' to retry an job: " + mJob);
				}

				if(mJob.getState() == Job.STATE_REOPEN) {
					mJob.setState(Job.STATE_OPEN);
					mLog.info("Reopened job: " + mJob);
				}
				
				mJob = null;
				break;
		
			case CMD_RETURN_TO_POOL:
				mPool.returnWorker(this);
				break;
				
			default:
				throw new IllegalArgumentException("Unknown Command given: '" + cmd + "'");
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.core.impl.IWorkerThread#addCommand(int)
	 */
	public void addCommand(int pCmd) {
		if(mShutdown) {
			throw new IllegalStateException("Shutdown in progress!");
		}
		
		mCommandQueue.add(pCmd);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.core.impl.IWorkerThread#getJob()
	 */
	public Job getJob() {
		return mJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.core.impl.IWorkerThread#setJob(de.phleisch.app.itsucks.job.Job)
	 */
	public void setJob(Job pJob) {
		mJob = pJob;
	}
	
}
