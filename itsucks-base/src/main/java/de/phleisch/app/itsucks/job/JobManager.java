/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 21.11.2007
 */

package de.phleisch.app.itsucks.job;

import java.util.List;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.event.EventSource;
import de.phleisch.app.itsucks.filter.JobFilter;


public interface JobManager extends EventSource {

	public abstract void addJob(Job pJob);

	public abstract boolean removeJob(Job pJob);

	public abstract Job getNextOpenJob();

	public abstract JobList getJobList();

	public abstract void setJobList(JobList pJobList);

	public abstract EventContext getContext();

	public abstract void setContext(EventContext pContext);
	
	public void addJobFilter(JobFilter pJobFilter);
	public void addJobFilter(List<JobFilter> pJobFilter);
	public boolean removeJobFilter(JobFilter pJobFilter);

}