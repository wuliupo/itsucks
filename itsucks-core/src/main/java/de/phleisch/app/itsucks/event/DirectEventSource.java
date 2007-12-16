/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 16.12.2007
 */

package de.phleisch.app.itsucks.event;

public interface DirectEventSource {

	/**
	 * Fires an event and dispatches it to all registered observers.
	 * @param pEvent
	 */
	public abstract void fireEvent(final Event pEvent);

	/**
	 * Registers an new observer. 
	 * @param pObserver
	 */
	public abstract void registerObserver(EventObserver pObserver);

	/**
	 * Unregisters the given observer.
	 * @param pObserver
	 */
	public abstract void unregisterObserver(EventObserver pObserver);
    
}
