/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.04.2007
 */

package de.phleisch.app.itsucks.event;

import de.phleisch.app.itsucks.Job;

public class JobEvent extends SimpleEvent {

	private Job mJob;
	
	public JobEvent(Event pEvent, Job pJob) {
		super(pEvent);
		
		mJob = pJob;
	}
	
	public JobEvent(int pType, int pCategory, Job pJob) {
		super(pType, pCategory);
		
		mJob = pJob;
	}

	public Job getJob() {
		return mJob;
	}

}
