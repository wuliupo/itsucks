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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ListItem<T> other = (ListItem<T>) obj;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
			return false;
		return true;
	}

	public String toString() {
		return mName;
	}
	
}
