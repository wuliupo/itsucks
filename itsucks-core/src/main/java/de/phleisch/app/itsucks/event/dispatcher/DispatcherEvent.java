/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.04.2007
 */

package de.phleisch.app.itsucks.event.dispatcher;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.impl.SimpleEvent;

/**
 * Implementation of an Event fired by jobs.
 * 
 * @author olli
 *
 */
public class DispatcherEvent extends SimpleEvent {

	private Dispatcher mDispatcher;
	
	public DispatcherEvent(Event pEvent, Dispatcher pDispatcher) {
		super(pEvent);
		
		mDispatcher = pDispatcher;
	}
	
	public DispatcherEvent(int pType, int pCategory, Dispatcher pDispatcher) {
		super(pType, pCategory);
		
		mDispatcher = pDispatcher;
	}

	/**
	 * Returns the dispatcher associated to this event.
	 * @return
	 */
	public Dispatcher getDispatcher() {
		return mDispatcher;
	}

}
