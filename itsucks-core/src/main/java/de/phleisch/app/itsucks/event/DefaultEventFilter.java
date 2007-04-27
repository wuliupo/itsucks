/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.04.2007
 */

package de.phleisch.app.itsucks.event;

import java.util.List;

public class DefaultEventFilter implements EventFilter {

	public static enum Behavior {
		AcceptAll,
		AcceptOnlyAllowed;
	}

	public void setEventBehavior(Behavior pBehavior) {
		
	}
	
	public void addAllowedEvent(int pAllowedEvent) {
		
	}
	
	public void removeAllowedEvent(int pAllowedEvent) {
		
	}
	
	public void setAllowedEvents(List<Integer> pAllowedEvents) {
		
	}
	

	public void setFamilyBehavior(Behavior pBehavior) {
		
	}

	public void addAllowedFamily(int pAllowedEvent) {
		
	}
	
	public void removeAllowedFamily(int pAllowedEvent) {
		
	}
	
	public void setAllowedFamily(List<Integer> pAllowedEvents) {
		
	}

	public boolean isEventAccepted(Event pEvent) {
		return true;
	}

	
	
	
}
