/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event.impl;

import de.phleisch.app.itsucks.event.Event;

/**
 * An implementation of the <code>Event</code> interface.
 * 
 * @author olli
 *
 */
public class SimpleEvent implements Event {

	private int mCategory;
	private int mType;
	
	/**
	 * This constructor copies type and category from the given event.
	 * 
	 * @param pEvent
	 */
	public SimpleEvent(Event pEvent) {
		mCategory = pEvent.getCategory();
		mType = pEvent.getType();
	}
	
	public SimpleEvent(int pType) {
		this(pType, -1);
	}
	
	public SimpleEvent(int pType, int pCategory) {
		mCategory = pCategory;
		mType = pType;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.event.Event#getCategory()
	 */
	public int getCategory() {
		return mCategory;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.event.Event#getType()
	 */
	public int getType() {
		return mType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Event: " + this.getClass().getName() 
			+ " / Type: " + getType() + " / Category: " + getCategory(); 
	}
	
}
