/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 16.12.2007
 */

package de.phleisch.app.itsucks.event.impl;

import java.util.ArrayList;
import java.util.List;

import de.phleisch.app.itsucks.event.DirectEventSource;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;

public class SimpleDirectEventSource implements DirectEventSource {

	private List<EventObserver> mRegisteredObservers = 
		new ArrayList<EventObserver>(); 
	
	public void fireEvent(Event pEvent) {

		//create a copy from the event observer list
		EventObserver[] observer;
		synchronized (mRegisteredObservers) {
			observer = new EventObserver[mRegisteredObservers.size()];
			mRegisteredObservers.toArray(observer);
		}
		
		//delegate the event
		for (int i = 0; i < observer.length; i++) {
			observer[i].processEvent(pEvent);
		}
		
	}

	public void registerObserver(EventObserver pObserver) {
		
		synchronized (mRegisteredObservers) {
			if(!mRegisteredObservers.contains(pObserver)) {
				mRegisteredObservers.add(pObserver);
			}
		}
	}

	public void unregisterObserver(EventObserver pObserver) {

		synchronized (mRegisteredObservers) {
			mRegisteredObservers.remove(pObserver);
		}
		
	}

}
