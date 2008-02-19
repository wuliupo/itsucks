/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 23.04.2007
 */

package de.phleisch.app.itsucks.event;

/**
 * This is the interface of an EventFilter.
 * An EventFilter can be used to filter events which are dispatched to your observer.
 * 
 * @author olli
 *
 */
public interface EventFilter {

	/**
	 * Checks if the given event is accepted to be dispatched.
	 * 
	 * @param pEvent
	 * @return
	 */
	boolean isEventAccepted(Event pEvent);

}
