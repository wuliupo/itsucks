/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

import java.util.Set;
import java.util.TreeSet;

public class DefaultEventFilter implements EventFilter {

	private TreeSet<Integer> mEventFilter = null;
	private TreeSet<Integer> mCategoryFilter = null;

	public void addAllowedEvent(int pAllowedEvent) {
		if(mEventFilter == null) {
			mEventFilter = new TreeSet<Integer>();
		}
		mEventFilter.add(pAllowedEvent);
	}
	
	public void removeAllowedEvent(int pAllowedEvent) {
		if(mEventFilter == null) return;
		mEventFilter.remove(pAllowedEvent);
		
		if(mEventFilter.size() == 0) {
			mEventFilter = null;
		}
	}
	
	public void setAllowedEvents(Set<Integer> pAllowedEvents) {
		if(mEventFilter == null) {
			mEventFilter = new TreeSet<Integer>();
		}
		mEventFilter.addAll(pAllowedEvents);
		
		if(mEventFilter.size() == 0) {
			mEventFilter = null;
		}
	}


	public void addAllowedCategory(int pAllowedCategory) {
		if(mCategoryFilter == null) {
			mCategoryFilter = new TreeSet<Integer>();
		}
		mCategoryFilter.add(pAllowedCategory);
	}
	
	public void removeAllowedCategory(int pAllowedCategory) {
		if(mCategoryFilter == null) return;
		mCategoryFilter.remove(pAllowedCategory);
		
		if(mCategoryFilter.size() == 0) {
			mCategoryFilter = null;
		}
	}
	
	public void setAllowedCategory(Set<Integer> pAllowedCategories) {
		if(mCategoryFilter == null) {
			mCategoryFilter = new TreeSet<Integer>();
		}
		mCategoryFilter.addAll(pAllowedCategories);
		
		if(mCategoryFilter.size() == 0) {
			mCategoryFilter = null;
		}
	}

	public boolean isEventAccepted(Event pEvent) {
		
		boolean accepted = true;
		
		if(mCategoryFilter != null) {
			accepted = mCategoryFilter.contains(pEvent.getCategory());
		}
		if(accepted && mEventFilter != null) {
			accepted = mEventFilter.contains(pEvent.getType());
		}
		
		return accepted;
	}
	
}
