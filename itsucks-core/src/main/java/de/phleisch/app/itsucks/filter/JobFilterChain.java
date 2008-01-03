/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.11.2007
 */

package de.phleisch.app.itsucks.filter;

import java.util.List;

import de.phleisch.app.itsucks.job.Context;
import de.phleisch.app.itsucks.job.Job;

public interface JobFilterChain {

	public abstract void addJobFilter(JobFilter pJobFilter);

	public abstract void addJobFilter(List<JobFilter> pJobFilter);

	public abstract boolean removeJobFilter(JobFilter pJobFilter);

	public abstract List<JobFilter> getJobFilter();
	
	public abstract Job filterJob(Job pJob);
	
	public abstract Context getContext();

	public abstract void setContext(Context pContext);
}
