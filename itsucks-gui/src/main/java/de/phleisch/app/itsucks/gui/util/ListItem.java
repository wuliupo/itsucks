/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 01.03.2008
 */

package de.phleisch.app.itsucks.gui.util;

public class ListItem<T> implements Comparable< ListItem<T> >{

	protected String mName;
	protected T mValue;
	
	public ListItem(String pName) {
		mName = pName;
	}
	
	public ListItem(String pName, T pValue) {
		mName = pName;
		mValue = pValue;
	}
	
	public String getName() {
		return mName;
	}

	public void setName(String pName) {
		mName = pName;
	}

	public T getValue() {
		return mValue;
	}

	public void setValue(T pValue) {
		mValue = pValue;
	}

	public int compareTo(ListItem<T> pItem) {
		return mName.compareTo(pItem.getName());
	}

	public String toString() {
		return mName;
	}
	
}
