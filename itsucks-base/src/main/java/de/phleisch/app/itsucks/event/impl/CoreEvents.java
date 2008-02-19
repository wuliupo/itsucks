/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 23.04.2007
 */

package de.phleisch.app.itsucks.event.impl;


/**
 * This class provides a list of available core events.
 * 
 * @author olli
 *
 */
public class CoreEvents {

	public static class ConstEvent extends SimpleEvent {

		private ConstEvent(int pType, int pCategory) {
			super(pType, pCategory);
		}
	}
	
	public final static int EVENT_CATEGORY_EVENTDISPATCHER	= 150;
	
	/**
	 * Core events
	 */
	public final static int EVENT_CATEGORY_DISPATCHER	= 200;
	
	/**
	 * Events send by the jobmanager.
	 */
	public final static int EVENT_CATEGORY_JOBMANAGER 	= 300;
	
	/**
	 * Events send by a job.
	 */
//	public final static int EVENT_CATEGORY_JOB 			= 400;
	
	/**
	 * Events send by batch.
	 */
	public final static int EVENT_CATEGORY_BATCH		= 500;
	
	
	public final static ConstEvent 
		EVENT_EVENTDISPATCHER_START = new ConstEvent(1501, EVENT_CATEGORY_EVENTDISPATCHER);
	
	public final static ConstEvent 
		EVENT_EVENTDISPATCHER_FINISH = new ConstEvent(1502, EVENT_CATEGORY_EVENTDISPATCHER);
	
	/**
	 * This event is fired when the job dispatcher started
	 */
	public final static ConstEvent 
		EVENT_DISPATCHER_START = new ConstEvent(2001, EVENT_CATEGORY_DISPATCHER);

	/**
	 * This event is fired when the job dispatcher paused
	 */
	public final static ConstEvent 
		EVENT_DISPATCHER_PAUSE = new ConstEvent(2002, EVENT_CATEGORY_DISPATCHER);

	/**
	 * This event is fired when the job dispatcher unpaused
	 */	
	public final static ConstEvent 
		EVENT_DISPATCHER_UNPAUSE = new ConstEvent(2003, EVENT_CATEGORY_DISPATCHER);

	/**
	 * This event is fired when the job dispatcher stopped/finished
	 */
	public final static ConstEvent 
		EVENT_DISPATCHER_FINISH = new ConstEvent(2004, EVENT_CATEGORY_DISPATCHER);

	/**
	 * This event is fired when a new job is added to the JobManager
	 */
	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_ADDED = new ConstEvent(3001, EVENT_CATEGORY_JOBMANAGER);
	
	/**
	 * This event is fired when a new job was filtered by the JobManager
	 */
	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_FILTERED = new ConstEvent(3002, EVENT_CATEGORY_JOBMANAGER);

	/**
	 * This event is fired when a job was removed from the JobManager
	 */
	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_REMOVED = new ConstEvent(3003, EVENT_CATEGORY_JOBMANAGER);
	
	/**
	 * This event is fired when a job has changed (status, priority etc.)
	 */
	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_CHANGED = new ConstEvent(3004, EVENT_CATEGORY_JOBMANAGER);
	
	
	public final static ConstEvent 
		EVENT_BATCH_START = new ConstEvent(5001, EVENT_CATEGORY_BATCH);

	public final static ConstEvent 
		EVENT_BATCH_FINISH = new ConstEvent(5002, EVENT_CATEGORY_BATCH);

	
}
