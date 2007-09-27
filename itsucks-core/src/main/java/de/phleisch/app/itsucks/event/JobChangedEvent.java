/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.04.2007
 */

package de.phleisch.app.itsucks.event;

import java.beans.PropertyChangeEvent;

import de.phleisch.app.itsucks.Job;

/**
 * Implementation of an Event fired by jobs.
 * 
 * @author olli
 *
 */
public class JobChangedEvent extends JobEvent {

	private PropertyChangeEvent mPropertyChangeEvent;
	
	public JobChangedEvent(Event pEvent, Job pJob) {
		super(pEvent, pJob);
	}
	
	public JobChangedEvent(int pType, int pCategory, Job pJob) {
		super(pType, pCategory, pJob);
	}

	public PropertyChangeEvent getPropertyChangeEvent() {
		return mPropertyChangeEvent;
	}

	public void setPropertyChangeEvent(PropertyChangeEvent pPropertyChangeEvent) {
		mPropertyChangeEvent = pPropertyChangeEvent;
	}

}
