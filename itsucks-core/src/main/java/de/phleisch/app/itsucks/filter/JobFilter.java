/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: JobFilter.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 */

package de.phleisch.app.itsucks.filter;

import de.phleisch.app.itsucks.job.Context;
import de.phleisch.app.itsucks.job.Job;

/**
 * This interface is for a implementation which is used to filter a 
 * job when adding it to the job list. 
 * @author olli
 */
public interface JobFilter {

	/**
	 * Asks the Filter if it supports the given job.
	 * This method is called before the filtering starts.
	 * It is not allowed to change the job.
	 * 
	 * @param pJob
	 * @return
	 */
	public boolean supports(Job pJob);	
	
	/**
	 * Filters the job.
	 * At this operation any modification can me made to the job like changing
	 * the priority, state, parameter etc.
	 * 
	 * @param pJob
	 * @return
	 * @throws Exception
	 */
	public Job filter(Job pJob) throws Exception;

	/**
	 * Sets the context the filter is used in.
	 * @param pContext
	 */
	public void setContext(Context pContext);
	
}
