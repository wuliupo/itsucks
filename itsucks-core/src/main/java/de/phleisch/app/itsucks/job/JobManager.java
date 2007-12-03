/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 21.11.2007
 */

package de.phleisch.app.itsucks.job;

import de.phleisch.app.itsucks.event.EventSource;


public interface JobManager extends EventSource {

	public abstract void addJob(Job pJob);

	public abstract boolean removeJob(Job pJob);

	public abstract Job getNextOpenJob();

	public abstract JobList getJobList();

	public abstract void setJobList(JobList pJobList);

	public abstract Context getContext();

	public abstract void setContext(Context pContext);
}