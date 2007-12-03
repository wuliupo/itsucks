/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.04.2007
 */

package de.phleisch.app.itsucks.event.job;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.job.Job;

/**
 * Implementation of an Event which is fired by the job manager when an job was added.
 * 
 * @author olli
 *
 */
public class JobAddedEvent extends JobEvent {

	private int mInitialState;
	
	public JobAddedEvent(Event pEvent, Job pJob) {
		super(pEvent, pJob);
		
	}
	
	public JobAddedEvent(int pType, int pCategory, Job pJob) {
		super(pType, pCategory, pJob);
	}
	
	public int getInitialState() {
		return mInitialState;
	}

	public void setInitialState(int pInitialState) {
		mInitialState = pInitialState;
	}

}
