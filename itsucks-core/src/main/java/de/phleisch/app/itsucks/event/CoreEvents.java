/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 23.04.2007
 */

package de.phleisch.app.itsucks.event;

/**
 * This class provides a list of available core events.
 * 
 * @author olli
 *
 */
public class CoreEvents {

	public static class ConstEvent implements Event {

		private final int mType;
		private final int mFamily;
		
		private ConstEvent(int pType, int pFamily) {
			mType = pType;
			mFamily = pFamily;
		}
		
		public int getCategory() {
			return mFamily;
		}

		public int getType() {
			return mType;
		}
		
	}
	
	/**
	 * This category is used for special internal commands, 
	 * like start/stop/pause/resume the event manager.
	 * 
	 * These events will not be dispatched to the event observer.
	 */
	public final static int EVENT_CATEGORY_SYSTEM_CMD	= 100;
	
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
	public final static int EVENT_CATEGORY_JOB 			= 400;
	
	
	/**
	 * System command to start the event dispatcher. 
	 */
	public final static ConstEvent 
		EVENT_EVENTDISPATCHER_CMD_START = new ConstEvent(1001, EVENT_CATEGORY_SYSTEM_CMD);
	
	/**
	 * System command to stop the event dispatcher. 
	 */
	public final static ConstEvent 
		EVENT_EVENTDISPATCHER_CMD_STOP = new ConstEvent(1002, EVENT_CATEGORY_SYSTEM_CMD);
	
	
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
		EVENT_JOB_CHANGED = new ConstEvent(4002, EVENT_CATEGORY_JOB);
	
}
