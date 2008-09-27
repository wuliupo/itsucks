package de.phleisch.app.itsucks.event.impl;

import de.phleisch.app.itsucks.event.EventFilter;
import de.phleisch.app.itsucks.event.EventObserver;

public class EventObserverConfig {
	
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

	@Override
	public boolean equals(Object pObj) {
		if(pObj == null) {
			return false;
		}
		if (getClass() != pObj.getClass()) {
			return false;
		}
		
		EventObserverConfig config = (EventObserverConfig) pObj;
		return this.getObserver().equals(config.getObserver());
	}
}
