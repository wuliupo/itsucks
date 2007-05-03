/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EventManager {

	private static Log mLog = LogFactory.getLog(EventManager.class);
	
	private boolean initialized = false;
	
	private Deque<Event> mEventDequeue = 
		new LinkedBlockingDeque<Event>();

	private Set<EventObserverConfig> mRegisteredObserver =
		new HashSet<EventObserverConfig>();
	
	private EventManagerThread mEventThread;
	private static int mEventManagerThreadCounter = 0;
	
	public EventManager() {
	}
	
	public synchronized void init() {
		if(initialized) return;
		
		mEventThread = new EventManagerThread();
		mEventThread.start();
		initialized = true;
	}
	
	public synchronized void shutdown() {
		if(!initialized) return;
		
		mEventThread.stopThread();
		mEventThread = null;
		initialized = false;
	}
	
	public void fireEvent(final Event pEvent) {
		mLog.debug("Got event: " + pEvent);
		
		//do not dispatch this event if this is an system cmd
		if(pEvent.getCategory() == CoreEvents.EVENT_CATEGORY_SYSTEM_CMD) {
			handleSystemCmd(pEvent);
			return;
		}
		

		if(initialized) {
		
			//start synchronize block to be sure event thread is not currently going to sleep.
			synchronized(mEventThread) {
				
				//add the event at the tail of the deque
				mEventDequeue.add(pEvent);
			
				//wake up the thread if it waits for new events
				mEventThread.notify();
			}
			
		} else {
			
			mEventDequeue.add(pEvent);
//			throw new RuntimeException("Event manager not initialized yet!");
			
		}
		

	}

	private void handleSystemCmd(final Event pEvent) {
		if(pEvent.getType() == CoreEvents.EVENT_MANAGER_CMD_START.getType()) {
			init();
		} else if(pEvent.getType() == CoreEvents.EVENT_MANAGER_CMD_STOP.getType()) {
			shutdown();
		}
	}

	public void registerObserver(EventObserver pObserver) {
		registerObserver(pObserver, null);
	}

	public void registerObserver(EventObserver pObserver, EventFilter pFilter) {
		
		synchronized (mRegisteredObserver) {
			mRegisteredObserver.add(new EventObserverConfig(pObserver, pFilter));
		}
	}
	
	public void unregisterObserver(EventObserver pObserver) {
		
		synchronized (mRegisteredObserver) {
			mRegisteredObserver.remove(new EventObserverConfig(pObserver, null));
		}
	}
	
	private class EventObserverConfig {
		
		private EventObserver mObserver;
		private EventFilter mFilter;
		
		public EventObserverConfig(EventObserver pObserver, EventFilter pFilter) {
			mObserver = pObserver;
			mFilter = pFilter;
		}

		public EventFilter getFilter() {
			return mFilter;
		}

		public EventObserver getObserver() {
			return mObserver;
		}

		@Override
		public int hashCode() {
			return mObserver.hashCode();
		}
	}
	
	private class EventManagerThread extends Thread {

		private boolean mStop;
		
		public EventManagerThread() {
			setDaemon(true);
			setName("EventManagerThread-" + ++mEventManagerThreadCounter);
		}
		
		@Override
		public void run() {
			mStop = false;

			try {
				processEvents();
			} catch(RuntimeException ex) {
				mLog.fatal("Event manager thread died unexpected.", ex);
				
				throw ex;
			}
			
			mLog.debug("Event manager thread shut down.");
		}

		private void processEvents() {
			
			Event event;
			
			while(!mStop) {
				
				event = mEventDequeue.pollFirst();
				if(event == null) {
					try {
						doWaitLoop();
					} catch (InterruptedException e) {
						mLog.warn(e, e);
					}
				} else {
					dispatchEvent(event);
				}
			}
			
		}

		private void doWaitLoop() throws InterruptedException {
			
			synchronized (this) {
				
				//wait only if the queue is really empty at this point
				if(mEventDequeue.size() == 0) {
					this.wait();
				}
			}
			
		}

		private void dispatchEvent(final Event pEvent) {
			
			synchronized (mRegisteredObserver) {
				
				EventFilter filter;
				for (EventObserverConfig config : mRegisteredObserver) {

					filter = config.getFilter();
					if(filter != null) {
						
						if(filter.isEventAccepted(pEvent)) {
							config.getObserver().processEvent(pEvent);
						}
						
					} else {
						config.getObserver().processEvent(pEvent);
					}
					
				}
			}
		}
		
		public void stopThread() {
			mStop = true;
			
			//wake up the thread if it waits for new events
			synchronized(mEventThread) {
				notify();
			}
		}
		
	}
	
}
