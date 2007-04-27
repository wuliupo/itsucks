/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EventManager {

	private static Log mLog = LogFactory.getLog(EventManager.class);
	
	private Deque<Event> mEventDequeue = 
		new LinkedBlockingDeque<Event>();
	
	
	public void fireEvent(Event pEvent) {
		mLog.debug("Got event: " + pEvent);
		
		//add the event at the tail of the deque
		//mEventDequeue.add(pEvent);
	}

	public void registerObserver(EventObserver pObserver) {
		
	}

	public void registerObserver(EventObserver pObserver, EventFilter pFilter) {
		
	}
	
	public void unregisterObserver(EventObserver pObserver) {
		
	}
	
}
