/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: WorkerThread.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WorkerThread implements Runnable {

	private static Log mLog = LogFactory.getLog(WorkerThread.class);
	
	public final static int CMD_SHUTDOWN = 0;
	public final static int CMD_PROCESS_JOB = 1;
	public final static int CMD_RETURN_TO_POOL = 2;
	
	private final static long CMD_POLL_INTERVAL = 50; 
	
	private List<Integer> mCommandQueue = new ArrayList<Integer>();
	private Job mJob;
	private boolean mShutdown = false;
	private WorkerPool mPool;
	
	private Thread mThread; 
	
	public WorkerThread(WorkerPool pWorkerPool, String pName) {
		mPool = pWorkerPool;
		mThread = new Thread(this, pName);
		mThread.setDaemon(true);
	}
	
	public void abort() {
		mJob.abort();
		mThread.interrupt();
	}

	public void run() {
		
		try {
			waitForWork();
		} catch (InterruptedException e) {
			mLog.error("Worker Thread Interrupted", e);
		}
	}

	public void start() {
		mThread.start();
	}
	
	public void join() throws InterruptedException {
		mThread.join();
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
				
				if(mJob.getState() == Job.STATE_IN_PROGRESS) {
					mJob.setState(Job.STATE_ERROR);
					mLog.error("Job does not finish correctly, state after processing is still 'in progress': " + mJob);
				}
				
				
				mLog.info("Finished working on job: " + mJob);
				mJob = null;
				break;
		
			case CMD_RETURN_TO_POOL:
				mPool.returnWorker(this);
				break;
				
			default:
				throw new IllegalArgumentException("Unknown Command given: '" + cmd + "'");
		}
	}
	
	public void addCommand(int pCmd) {
		if(mShutdown) {
			throw new IllegalStateException("Shutdown in progress!");
		}
		
		mCommandQueue.add(pCmd);
	}
	
	public Job getJob() {
		return mJob;
	}

	public void setJob(Job pJob) {
		mJob = pJob;
	}
	
}
