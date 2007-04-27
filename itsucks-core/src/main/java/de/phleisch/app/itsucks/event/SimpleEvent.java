/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

public class SimpleEvent implements Event {

	private int mCategory;
	private int mType;
	
	public SimpleEvent(Event pEvent) {
		mCategory = pEvent.getCategory();
		mType = pEvent.getType();
	}
	
	public SimpleEvent(int pType, int pCategory) {
		mCategory = pCategory;
		mType = pType;
	}
	
	public int getCategory() {
		return mCategory;
	}

	public int getType() {
		return mType;
	}

	public String toString() {
		return "Event: " + this.getClass().getName() 
			+ " / Type: " + getType() + " / Category: " + getCategory(); 
	}
	
}
