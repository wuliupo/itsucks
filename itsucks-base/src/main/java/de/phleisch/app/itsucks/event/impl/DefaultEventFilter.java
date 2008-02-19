/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event.impl;

import java.util.Set;
import java.util.TreeSet;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventFilter;

/**
 * An simple event filter which can filter by category and type.
 * Per default every event is accepted.
 * 
 * @author olli
 *
 */
public class DefaultEventFilter implements EventFilter {

	private TreeSet<Integer> mEventType = null;
	private TreeSet<Integer> mCategoryFilter = null;

	/**
	 * Adds an allowed event type.
	 * @param pAllowedType
	 */
	public void addAllowedType(int pAllowedType) {
		if(mEventType == null) {
			mEventType = new TreeSet<Integer>();
		}
		mEventType.add(pAllowedType);
	}
	
	/**
	 * Removes an allowed type.
	 * @param pAllowedType
	 */
	public void removeAllowedType(int pAllowedType) {
		if(mEventType == null) return;
		mEventType.remove(pAllowedType);
		
		if(mEventType.size() == 0) {
			mEventType = null;
		}
	}
	
	/**
	 * Adds an list of allowed types
	 * @param pAllowedTypes
	 */
	public void setAllowedTypes(Set<Integer> pAllowedTypes) {
		if(mEventType == null) {
			mEventType = new TreeSet<Integer>();
		}
		mEventType.addAll(pAllowedTypes);
		
		if(mEventType.size() == 0) {
			mEventType = null;
		}
	}


	/**
	 * Adds an allowed category.
	 * @param pAllowedCategory
	 */
	public void addAllowedCategory(int pAllowedCategory) {
		if(mCategoryFilter == null) {
			mCategoryFilter = new TreeSet<Integer>();
		}
		mCategoryFilter.add(pAllowedCategory);
	}
	
	/**
	 * Removes an allowed category.
	 * @param pAllowedCategory
	 */
	public void removeAllowedCategory(int pAllowedCategory) {
		if(mCategoryFilter == null) return;
		mCategoryFilter.remove(pAllowedCategory);
		
		if(mCategoryFilter.size() == 0) {
			mCategoryFilter = null;
		}
	}
	
	/**
	 * Sets an list of allowed categories.
	 * @param pAllowedCategories
	 */
	public void setAllowedCategory(Set<Integer> pAllowedCategories) {
		if(mCategoryFilter == null) {
			mCategoryFilter = new TreeSet<Integer>();
		}
		mCategoryFilter.addAll(pAllowedCategories);
		
		if(mCategoryFilter.size() == 0) {
			mCategoryFilter = null;
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.event.EventFilter#isEventAccepted(de.phleisch.app.itsucks.event.Event)
	 */
	public boolean isEventAccepted(Event pEvent) {
		
		boolean accepted = true;
		
		if(mCategoryFilter != null) {
			accepted = mCategoryFilter.contains(pEvent.getCategory());
		}
		if(accepted && mEventType != null) {
			accepted = mEventType.contains(pEvent.getType());
		}
		
		return accepted;
	}
	
}
