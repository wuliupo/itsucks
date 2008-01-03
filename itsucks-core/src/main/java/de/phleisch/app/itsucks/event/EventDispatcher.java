/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.11.2007
 */

package de.phleisch.app.itsucks.event;

public interface EventDispatcher {

	/**
	 * Initializes the Event dispatcher and starts the dispatch thread
	 */
	public abstract void init();

	/**
	 * Shutdown the Event dispatcher.
	 */
	public abstract void shutdown();

	/**
	 * Fires an event and dispatches it to all registered observers.
	 * @param pEvent
	 */
	public abstract void fireEvent(final Event pEvent);

	/**
	 * Registers an new observer. All events are dispatched to this observer.
	 * @param pObserver
	 */
	public abstract void registerObserver(EventObserver pObserver);

	/**
	 * Registers an new observer. All events are filtered by the given event filter.
	 * @param pObserver
	 * @param pFilter
	 */
	public abstract void registerObserver(EventObserver pObserver,
			EventFilter pFilter);

	/**
	 * Unregisters the given observer.
	 * @param pObserver
	 */
	public abstract void unregisterObserver(EventObserver pObserver);

}