/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.12.2007
 */

package de.phleisch.app.itsucks.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.dispatcher.DispatcherEvent;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.event.impl.SimpleDirectEventSource;

public class DispatcherBatch extends SimpleDirectEventSource {

	private static Log mLog = LogFactory.getLog(DispatcherBatch.class);
	private static int mBatchThreadCount = 0;
	
	private BatchThread mInternalThread;
	private List<DispatcherListElement> mJobList;
	private int mJobsFinished;
	private int mMaxConcurrentDispatcher;
	
	private volatile boolean mBatchRunning;
	private volatile int mDispatcherRunning;
	
	private final Object mSyncMutex = new Object(); 
	
	public DispatcherBatch() {
		mJobList = new ArrayList<DispatcherListElement>();
		mMaxConcurrentDispatcher = 1; //Default is 1
		
		reset();
	}
	
	private static class DispatcherListElement {
		
		enum State {
			OPEN, RUNNING, FINISHED
		}
		
		private final DispatcherThread mDispatcher;
		private State mState = State.OPEN;
		
		public DispatcherListElement(DispatcherThread pDispatcher) {
			mDispatcher = pDispatcher;
		}
		
		@Override
		public boolean equals(Object pObj) {
			return mDispatcher.equals(pObj);
		}
		@Override
		public int hashCode() {
			return mDispatcher.hashCode();
		}
		public State getState() {
			return mState;
		}
		public void setState(State pState) {
			mState = pState;
		}
		public DispatcherThread getDispatcher() {
			return mDispatcher;
		}
		
	}
	
	private class BatchThread extends Thread {

		private DispatcherListener mDispatcherListener = new DispatcherListener();
		
		@Override
		public void run() {
			
			mBatchRunning = true;
			mDispatcherRunning = 0;
			
			fireEvent(CoreEvents.EVENT_BATCH_START);
			
			mLog.info("Batch dispatcher start.");
			
			try {
				watchJobs();
				joinAllJobs();
			} catch(Exception e) {
				mLog.error("Error running batch", e);
			} finally {
				mBatchRunning = false;
				fireEvent(CoreEvents.EVENT_BATCH_FINISH);
			}
			
			mLog.info("Batch dispatcher finished.");
		}

		protected void watchJobs() {
			while(true) {
				
				synchronized(mSyncMutex) {
					
					if(mJobsFinished >= mJobList.size()) {
						mLog.info("Dispatcher Batch finished.");
						break;
					}
					
					if(mDispatcherRunning < mMaxConcurrentDispatcher 
							&& (mJobList.size() - mJobsFinished) > mDispatcherRunning) {
						
						startNextDispatcher();
						continue;
					}
					
					try {
						mLog.debug("Wait for Notify.");
						mSyncMutex.wait();
						mLog.debug("Received Notify.");
					} catch (InterruptedException e) {}
					
				}
			}
		}

		protected void joinAllJobs() throws InterruptedException {
			mLog.debug("Wait for all dispatcher to join.");
			
			synchronized(mSyncMutex) {
				for (DispatcherListElement itelement : mJobList) {
					itelement.getDispatcher().join();
				}
			}
		}
		
		protected void startNextDispatcher() {

			DispatcherThread dispatcher = null;
			synchronized(mSyncMutex) {
			
				for (DispatcherListElement element : mJobList) {
					
					if(element.getState().equals(DispatcherListElement.State.OPEN)) {
						
						//update state and inform the model of the change
						element.setState(DispatcherListElement.State.RUNNING);
						
						//increment the dispatcher running count
						mDispatcherRunning++;
						
						dispatcher = element.getDispatcher();
						
						//only one item per call
						break;
					}
				}
			}
			
			if(dispatcher != null) {
					
				mLog.debug("Start Dispatcher: " + dispatcher);
				
				//register observer
				dispatcher.getEventManager().registerObserver(mDispatcherListener);
				
				// start dispatcher thread
				try {
					dispatcher.processJobs();
				
				} catch (Exception e) {
					mLog.error("Error starting dispatcher thread", e);
				}
				
//				// wait till dispatcher is starting
//				for (int i = 0; i < 10 && !dispatcher.isRunning(); i++) {
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {}
//				}
			} else {
				throw new IllegalStateException("Try to start non existing dispatcher!");
			}
		}
		

		protected void finishDispatcher(Dispatcher pDispatcher) {
			mLog.debug("Finish dispatcher: " + pDispatcher);

			DispatcherListElement element = null;
			
			synchronized(mSyncMutex) {
				
				for (DispatcherListElement itelement : mJobList) {
					if(itelement.getDispatcher() == pDispatcher) {
						element = itelement;
						break;
					}
				}
				
				if(element == null) {
					throw new IllegalArgumentException("Given Dispatcher not registered.");
				}
				
				element.setState(DispatcherListElement.State.FINISHED);
				
				mJobsFinished++;
				
				//decrement the dispatcher running count
				mDispatcherRunning--;

				//deregister observer
				element.getDispatcher().getEventManager().unregisterObserver(
						mDispatcherListener);
				
				mLog.debug("Notify thread: " + mInternalThread.getName());
				
				//wake up batch thread
				mSyncMutex.notify();
			}
		}
		
		protected class DispatcherListener implements EventObserver {

			public void processEvent(Event pEvent) {
				
				if(pEvent.equals(CoreEvents.EVENT_DISPATCHER_START)) {
					mLog.debug("Got event dispatcher is starting.");
				} else if(pEvent.equals(CoreEvents.EVENT_DISPATCHER_FINISH)) {
					mLog.debug("Got event dispatcher is stopping.");
					finishDispatcher(((DispatcherEvent)pEvent).getDispatcher());
				}
			}
		}
	}
	
	public void start() {
		
		if(mBatchRunning) {
			throw new IllegalStateException("Batch already running!");
		}
		
		mInternalThread.start();
	}
	
	public void join() throws InterruptedException {
		mInternalThread.join();
	}
	
	public void addDispatcher(DispatcherThread pDispatcher) {
		
		synchronized (mSyncMutex) {
			
			if(mBatchRunning) {
				throw new IllegalStateException("Could not add Job while batch is running!");
			}
			
			for (DispatcherListElement itelement : mJobList) {
				if(itelement.getDispatcher() == pDispatcher) {
					throw new IllegalArgumentException("Dispatcher already added!");
				}
			}
			
			mJobList.add(new DispatcherListElement(pDispatcher));
		}
	}

	public void reset() {
		
		synchronized(mSyncMutex) {
			
			if(mBatchRunning) {
				throw new IllegalStateException("Could not reset Batch while running!");
			}
			
			mJobsFinished = 0;
			
			for (DispatcherListElement element : mJobList) {
				element.setState(DispatcherListElement.State.OPEN);
			}
			
			mInternalThread = new BatchThread();
			mInternalThread.setName("BatchThread-" + ++mBatchThreadCount);
		}
	}

	public int getMaxConcurrentDispatcher() {
		return mMaxConcurrentDispatcher;
	}

	public void setMaxConcurrentDispatcher(int pMaxConcurrentDispatcher) {
		mMaxConcurrentDispatcher = pMaxConcurrentDispatcher;
	}

	public int getJobsFinished() {
		return mJobsFinished;
	}

	public boolean isBatchRunning() {
		return mBatchRunning;
	}
	
}
