/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.04.2007
 */

package de.phleisch.app.itsucks.plugin;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.event.CoreEvents;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.JobEvent;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.http.HttpMetadata;

public class RetryPlugin implements EventObserver {

	public void processEvent(Event pEvent) {

		if(pEvent.getType() == CoreEvents.EVENT_JOB_CHANGED.getType()) {
				
			JobEvent jobEvent = (JobEvent) pEvent;
			DownloadJob job = (DownloadJob) jobEvent.getJob();
			
			if(job.getState() == Job.STATE_FINISHED) {
			
				HttpMetadata metadata = (HttpMetadata) job.getMetadata();
				
				if(metadata.getStatusCode() == 503) {
					
					//reopen job
					job.setState(Job.STATE_OPEN);
				}
			}
			
		}
		
	}

}
