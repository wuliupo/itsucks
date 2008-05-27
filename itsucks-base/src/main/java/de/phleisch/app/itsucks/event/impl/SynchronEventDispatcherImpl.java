package de.phleisch.app.itsucks.event.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.EventFilter;
import de.phleisch.app.itsucks.event.EventObserver;

public class SynchronEventDispatcherImpl implements EventDispatcher {

	private static Log mLog = LogFactory.getLog(SynchronEventDispatcherImpl.class);
	
	private Set<EventObserverConfig> mRegisteredObserver =
		new HashSet<EventObserverConfig>();

	public void init() {
	}

	public void shutdown() {
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
	
	public void fireEvent(Event pEvent) {
		
		mLog.debug("Got event: " + pEvent);
		
		List<EventObserverConfig> observerCopy;
		
		//get all observer which will receive this event
		synchronized (mRegisteredObserver) {
			if(mRegisteredObserver.size() == 0) {
				return;
			}
			
			//create a local copy to hold synchronized part as small as possible
			observerCopy = new ArrayList<EventObserverConfig>(mRegisteredObserver.size());
			
			EventFilter filter;
			for (EventObserverConfig config : mRegisteredObserver) {

				filter = config.getFilter();
				if(filter != null) {
					
					if(filter.isEventAccepted(pEvent)) {
						observerCopy.add(config);
					}
					
				} else {
					observerCopy.add(config);
				}
				
			}
		} // end synchronized
		
		//dispatch the event to the found observer
		for (EventObserverConfig config : observerCopy) {
			
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
