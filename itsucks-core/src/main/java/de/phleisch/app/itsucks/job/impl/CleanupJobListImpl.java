/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 01.01.2008
 */

package de.phleisch.app.itsucks.job.impl;

import java.beans.PropertyChangeEvent;

import de.phleisch.app.itsucks.job.Job;

public class CleanupJobListImpl extends SimpleJobListImpl {

	public CleanupJobListImpl() {
		super();
	}

	@Override
	protected void handleJobChanged(Job pChangedJob, PropertyChangeEvent pEvt) {
		super.handleJobChanged(pChangedJob, pEvt);
		
		if(Job.JOB_STATE_PROPERTY.equals(pEvt.getPropertyName()) &&
			pChangedJob.getState() == Job.STATE_FINISHED) {
			
			if(!removeJob(pChangedJob)) {
				throw new IllegalStateException("Could not remove job.");
			}
		}
	}
}
