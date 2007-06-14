/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

/**
 * This is the interface for an single event.
 * 
 * @author olli
 *
 */
public interface Event {

	
	/**
	 * Returns the type of this event. 
	 * For an list of existing event id's, check the <code>CoreEvents</code> class.
	 * 
	 * @return
	 */
	int getType();

	/**
	 * Returns the category of this event. 
	 * For an list of existing categories, check the <code>CoreEvents</code> class.
	 * 
	 * @return
	 */
	int getCategory();

}
