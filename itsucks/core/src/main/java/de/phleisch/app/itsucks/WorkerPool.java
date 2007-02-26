/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: WorkerPool.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WorkerPool {

	private static Log mLog = LogFactory.getLog(WorkerThread.class);
	
	private Stack<WorkerThread> mFreeWorker = new Stack<WorkerThread>();
	private List<WorkerThread> mBusyWorker = new ArrayList<WorkerThread>();
	private int mWorkerIdSequence = 0;
	
	private boolean initialized;
	private int mSize;
	
	public WorkerPool(int pSize) {
		super();
		mSize = pSize;
		initialized = false;
	}
	
	public synchronized void initialize() {
		if(initialized) return;
		
		increasePool(mSize);
		initialized = true;
	}
	
	public synchronized void shutdown() {
		if(!initialized) return; 
		
		decreasePool();
		if(mFreeWorker.size() > 0 || mBusyWorker.size() > 0) {
			throw new IllegalStateException("Could not free all worker threads!");
		}
		
		initialized = false;
	}
	
	private void increasePool(int pSize) {
		mLog.debug("Increase worker pool by " + pSize + " elements.");
		
		for (int i = 0; i < pSize; i++) {
			WorkerThread worker = new WorkerThread(this, "Worker: " + mWorkerIdSequence ++);
			worker.start();
			mFreeWorker.push(worker);
		}
	}
	
	private void decreasePool() {
		mLog.debug("Decrease worker pool");
		
		for(Enumeration<WorkerThread> enu = mFreeWorker.elements(); enu.hasMoreElements(); ) {
			WorkerThread workerThread = enu.nextElement();
			
			workerThread.addCommand(WorkerThread.CMD_SHUTDOWN);
			try {
				workerThread.join();
			} catch (InterruptedException e) {
				mLog.error("Got interrupted while waiting for worker thread to join!", e);
				throw new RuntimeException("Got interrupted while waiting for worker thread to join!", e);
			}
		}
		
		mFreeWorker.clear();
	}

	public synchronized WorkerThread getFreeWorker() {
		WorkerThread worker = null;
		
		if(mFreeWorker.size() > 0) {
			worker = mFreeWorker.pop();
			mLog.debug("Move worker into busy list: " + worker);
			mBusyWorker.add(worker);
		}
		
		return worker;
	}

	public synchronized void returnWorker(WorkerThread pWorker) {
		mLog.debug("Return worker into free list: " + pWorker);
		
		if(mBusyWorker.contains(pWorker)) {
			mBusyWorker.remove(pWorker);
			mFreeWorker.push(pWorker);
		} else {
			throw new IllegalArgumentException("Worker is not known as busy!");
		}
	}

	public WorkerThread waitForFreeWorker() throws InterruptedException {
		
		WorkerThread worker = getFreeWorker();
		
		while(worker == null) {
			Thread.sleep(100);
			worker = getFreeWorker();
		}
		
		return worker;
	}

	public void dispatchJob(Job job) throws InterruptedException {
		WorkerThread worker = waitForFreeWorker();
		
		job.setState(Job.STATE_ASSIGNED);
		worker.setJob(job);
		worker.addCommand(WorkerThread.CMD_PROCESS_JOB);
		worker.addCommand(WorkerThread.CMD_RETURN_TO_POOL);
	}
	
	public int getBusyWorkerCount() {
		return mBusyWorker.size();
	}
}
