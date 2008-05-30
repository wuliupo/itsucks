package de.phleisch.app.itsucks.event.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.EventFilter;
import de.phleisch.app.itsucks.event.EventObserver;

public class SynchronEventDispatcherImpl implements EventDispatcher {

	private static Log mLog = LogFactory.getLog(SynchronEventDispatcherImpl.class);
	
	private Object mRegisteredObserverLock = new Object();
	
	private volatile Set<EventObserverConfig> mRegisteredObserver =
		new HashSet<EventObserverConfig>();

	public void init() {
	}

	public void shutdown() {
	}
	
	public void registerObserver(EventObserver pObserver) {
		registerObserver(pObserver, null);
	}

	public void registerObserver(EventObserver pObserver, EventFilter pFilter) {
		synchronized (mRegisteredObserverLock) {
			Set<EventObserverConfig> newList = new HashSet<EventObserverConfig>(mRegisteredObserver);
			newList.add(new EventObserverConfig(pObserver, pFilter));
			mRegisteredObserver = newList;
		}
	}

	public void unregisterObserver(EventObserver pObserver) {
		synchronized (mRegisteredObserverLock) {
			Set<EventObserverConfig> newList = new HashSet<EventObserverConfig>(mRegisteredObserver);
			newList.remove(new EventObserverConfig(pObserver, null));
			mRegisteredObserver = newList;
		}
	}	
	
	public void fireEvent(Event pEvent) {
		
		mLog.debug("Got event: " + pEvent);
		
		Set<EventObserverConfig> observerCopy;
		
		//get all observer which will receive this event, no copy is required here, 
		//because a change in this list always implies a new list instance
		synchronized (mRegisteredObserverLock) {
			observerCopy = mRegisteredObserver;
		}
		
		if(observerCopy.size() == 0) {
			return;
		}
		
		EventFilter filter;
		for (EventObserverConfig config : mRegisteredObserver) {

			filter = config.getFilter();
			if(filter == null || (filter != null && filter.isEventAccepted(pEvent))) {
				
				try {
					config.getObserver().processEvent(pEvent);
				} catch(RuntimeException ex) {
					mLog.error("Error dispatching event: " + pEvent 
							+ " to observer: " 
							+ config.getObserver(), ex);
				}
			}
	
		}
	}

}
