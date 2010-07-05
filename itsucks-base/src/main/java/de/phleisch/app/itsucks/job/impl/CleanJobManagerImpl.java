/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *  
 * $Id$
 * Created on 03.03.2006
 */ 

package de.phleisch.app.itsucks.job.impl;

import java.beans.PropertyChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.event.JobChangedEvent;

public class CleanJobManagerImpl extends FilterJobManagerImpl {
	
	private static Log mLog = LogFactory.getLog(CleanJobManagerImpl.class);
	
	private EventObserver mListObserver;

	public CleanJobManagerImpl() {
		super();
		mListObserver = new JobEventObserver();
	}

	public void setContext(EventContext pContext) {
		
		//deregister from old event dispatcher
		EventDispatcher oldEventDispatcher = getEventDispatcher();
		if(oldEventDispatcher != null) {
			oldEventDispatcher.unregisterObserver(mListObserver);
		}
		
		super.setContext(pContext);
		
		//register to new event dispatcher
		EventDispatcher newEventDispatcher = getEventDispatcher();
		if(newEventDispatcher != null) {
			newEventDispatcher.registerObserver(mListObserver);
		}
		
	}

	private class JobEventObserver implements EventObserver {

		public void processEvent(Event pEvent) {

			if (pEvent.equals(CoreEvents.EVENT_JOBMANAGER_JOB_CHANGED)) {

				JobChangedEvent jobChangedEvent = (JobChangedEvent) pEvent;
				
				PropertyChangeEvent propertyChangeEvent = jobChangedEvent
						.getPropertyChangeEvent();

				if (Job.JOB_STATE_PROPERTY.equals(propertyChangeEvent
						.getPropertyName())) {
					
					if((Integer)propertyChangeEvent.getNewValue() >= Job.STATE_CLOSED) {
					
						mLog.debug("Remove finished job: " + jobChangedEvent.getJob());
						
						//remove this job from the list
						removeJob(jobChangedEvent.getJob());
					
					}
					
				}

			} 
		}
	}

}
