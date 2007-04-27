/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 23.04.2007
 */

package de.phleisch.app.itsucks.event;

public class CoreEvents {

	private static class ConstEvent implements Event {

		private int mType;
		private int mFamily;
		
		public ConstEvent(int pType, int pFamily) {
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
	
	public final static int EVENT_CATEGORY_CORE 		= 100;
	public final static int EVENT_CATEGORY_JOBMANAGER 	= 200;
	public final static int EVENT_CATEGORY_JOB 			= 300;
	
	
	public final static ConstEvent 
		EVENT_DISPATCHER_START = new ConstEvent(1001, EVENT_CATEGORY_CORE);

	public final static ConstEvent 
		EVENT_DISPATCHER_PAUSE = new ConstEvent(1002, EVENT_CATEGORY_CORE);

	public final static ConstEvent 
		EVENT_DISPATCHER_UNPAUSE = new ConstEvent(1003, EVENT_CATEGORY_CORE);

	public final static ConstEvent 
		EVENT_DISPATCHER_FINISH = new ConstEvent(1004, EVENT_CATEGORY_CORE);

	
	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_ADDED = new ConstEvent(2001, EVENT_CATEGORY_JOBMANAGER);
	
	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_FILTERED = new ConstEvent(2002, EVENT_CATEGORY_JOBMANAGER);

	public final static ConstEvent 
		EVENT_JOBMANAGER_JOB_REMOVED = new ConstEvent(2003, EVENT_CATEGORY_JOBMANAGER);
	
	
	public final static ConstEvent 
		EVENT_JOB_START = new ConstEvent(3001, EVENT_CATEGORY_JOB);
	
	public final static ConstEvent 
		EVENT_JOB_CHANGED = new ConstEvent(3002, EVENT_CATEGORY_JOB);
	
	public final static ConstEvent 
		EVENT_JOB_FINISHED = new ConstEvent(3003, EVENT_CATEGORY_JOB);
	
	public final static ConstEvent 
		EVENT_JOB_ERROR = new ConstEvent(3004, EVENT_CATEGORY_JOB);
	
}
