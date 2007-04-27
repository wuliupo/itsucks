/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 31.01.2007
 */

package de.phleisch.app.itsucks;

import java.util.Observer;

/**
 * This is a generic interface for 'JobList'. 
 * A job list manages and order the jobs to be process by the Dispatcher.
 * 
 * It also offers an interface to listen changes in the list structure.  
 * These message are from the type <code>JobListNotification</code>. 
 * 
 * @author olli
 *
 */
public interface JobList {

	public static final int NOTIFICATION_JOB_ADDED  = 1000;
	public static final int NOTIFICATION_JOB_REMOVED = 2000;
	public static final int NOTIFICATION_JOB_CHANGED = 3000;
	
	public abstract void addJob(Job pJob);

	public abstract boolean removeJob(Job pJob);

	public abstract void clear();

	public abstract Job getNextOpenJob();
	
	
	public void addObserver(Observer o);
	public void deleteObserver(Observer o);

}