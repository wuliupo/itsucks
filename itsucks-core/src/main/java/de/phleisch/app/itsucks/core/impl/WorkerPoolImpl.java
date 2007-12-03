/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: WorkerPool.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.core.WorkerPool;
import de.phleisch.app.itsucks.core.WorkerThread;
import de.phleisch.app.itsucks.job.Job;

/**
 * Default implementation of the WorkerPool.
 * 
 * @author olli
 */
public class WorkerPoolImpl implements WorkerPool {

	private static final int DEFAULT_POOL_SIZE = 3;

	private static Log mLog = LogFactory.getLog(WorkerPoolImpl.class);
	
	private Stack<WorkerThread> mFreeWorker = new Stack<WorkerThread>();
	private List<WorkerThread> mBusyWorker = new ArrayList<WorkerThread>();
	private int mWorkerIdSequence = 0;
	
	private boolean mInitialized;
	private int mDesiredSize;
	private int mRealSize;
	
	public WorkerPoolImpl() {
		this(DEFAULT_POOL_SIZE);
	}
	
	public WorkerPoolImpl(int pSize) {
		super();
		mRealSize = 0; 
		mDesiredSize = pSize;
		mInitialized = false;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#initialize()
	 */
	public synchronized void initialize() {
		if(mInitialized) return;
		
		updateSize();
		mInitialized = true;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#shutdown()
	 */
	public synchronized void shutdown() {
		if(!mInitialized) return; 
		
		decreasePool(mRealSize);
		mRealSize = mFreeWorker.size() + mBusyWorker.size();
		
		if(mRealSize > 0) {
			throw new IllegalStateException("Could not free all worker threads!");
		}
		
		mInitialized = false;
		
		//notify all threads waiting for an free worker
		this.notifyAll();
	}
	
	/**
	 * Checks if an pool resize is necessary.
	 */
	private synchronized void updateSize() {
		
		if(mRealSize != mDesiredSize) {
			
			if(mDesiredSize > mRealSize) {
				//real size is too small, increase to desired size
				increasePool(mDesiredSize - mRealSize);
			} else {
				//real size is too big, try to decrease to desired size
				decreasePool(mRealSize - mDesiredSize);
			}
		}
		
		mRealSize = mFreeWorker.size() + mBusyWorker.size();
		
		this.notifyAll();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#setSize(int)
	 */
	public synchronized void setSize(int pSize) {
		
		if(pSize == mDesiredSize) return;
		mDesiredSize = pSize;
		
		updateSize();
	}
	
	
	/**
	 * Increases the pool by the given size.
	 * @param pCount
	 */
	private synchronized void increasePool(int pCount) {
		mLog.info("Increase worker pool by " + pCount + " elements.");
		
		for (int i = 0; i < pCount; i++) {
			WorkerThreadImpl worker = new WorkerThreadImpl(this, "Worker: " + mWorkerIdSequence ++);
			worker.start();
			mFreeWorker.push(worker);
		}
	}
	
	/**
	 * Tries to shrink the pool by the given size.
	 * @param pCount
	 * @return
	 */
	private synchronized int decreasePool(int pCount) {
		mLog.info("Decrease worker pool by " + pCount + " elements.");
		
		int freedElements = 0;
		
		for(Iterator<WorkerThread> it = mFreeWorker.iterator(); it.hasNext(); ) {
			
			if(freedElements >= pCount) break;
			
			WorkerThread workerThread = it.next();
			stopWorker(workerThread);
			it.remove();
			
			freedElements ++;
		}
		
		return freedElements;
	}

	/**
	 * Stops the given the worker and wait for the thread to end.
	 * @param pWorkerThread
	 */
	private void stopWorker(WorkerThread pWorkerThread) {
		
		pWorkerThread.addCommand(WorkerThread.CMD_SHUTDOWN);
		try {
			pWorkerThread.join(0);
		} catch (InterruptedException e) {
			mLog.error("Got interrupted while waiting for worker thread to join!", e);
			throw new RuntimeException("Got interrupted while waiting for worker thread to join!", e);
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#getFreeWorker()
	 */
	public synchronized WorkerThread getFreeWorker() {
		WorkerThread worker = null;
		
		if(!mInitialized) {
			throw new RuntimeException("Pool not initialized!");
		}
		
		if(mFreeWorker.size() > 0) {
			worker = mFreeWorker.pop();
			mLog.debug("Move worker into busy list: " + worker);
			mBusyWorker.add(worker);
		}
		
		return worker;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#returnWorker(de.phleisch.app.itsucks.WorkerThread)
	 */
	public synchronized void returnWorker(WorkerThread pWorker) {
		mLog.debug("Return worker into free list: " + pWorker);
		
		if(mBusyWorker.contains(pWorker)) {
			mBusyWorker.remove(pWorker);
			mFreeWorker.push(pWorker);
		} else {
			throw new IllegalArgumentException("Worker is not known as busy!");
		}

		updateSize();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#waitForFreeWorker()
	 */
	public WorkerThread waitForFreeWorker() throws InterruptedException {
		
		WorkerThread worker = getFreeWorker();
		
		while(worker == null) {
			
			synchronized (this) {
				if(worker == null) {
					//wait for an change in the worker pool
					this.wait();
				}
			}
			worker = getFreeWorker();
		}
		
		return worker;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#dispatchJob(de.phleisch.app.itsucks.Job)
	 */
	public void dispatchJob(Job job) throws InterruptedException {
		WorkerThread worker = waitForFreeWorker();
		
		job.setState(Job.STATE_ASSIGNED);
		worker.setJob(job);
		worker.addCommand(WorkerThread.CMD_PROCESS_JOB);
		worker.addCommand(WorkerThread.CMD_RETURN_TO_POOL);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#getBusyWorkerCount()
	 */
	public int getBusyWorkerCount() {
		return mBusyWorker.size();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.IWorkerPool#abortBusyWorker()
	 */
	public void abortBusyWorker() {
		
		//send first info to all worker
		sendAbortToAllBusyWorker();
		
		//lets leave the worker some time to shut down
		Thread.yield();

		//check if all threads are finished and send them the abort signal again if not.
		while(mBusyWorker.size() > 0) {

			sendAbortToAllBusyWorker();
			
			//lets leave the worker some time to shut down
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		
	}
	
	private synchronized void sendAbortToAllBusyWorker() {
		
		for (WorkerThread worker : mBusyWorker) {
			
			if(mBusyWorker.contains(worker)) {
				worker.abort();
			}
		}
		
	}
}
