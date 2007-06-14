/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

/**
 * This interface must be implemented to receive dispatched events.
 * 
 * @author olli
 *
 */
public interface EventObserver {

	/**
	 * This method is called when a event is dispatched to this class.
	 * It should finish as fast as possible, if not, the event dispatching thread
	 * is blocked.
	 * 
	 * @param pEvent
	 */
	void processEvent(Event pEvent);

}
