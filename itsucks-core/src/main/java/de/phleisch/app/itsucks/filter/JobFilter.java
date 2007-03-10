/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: JobFilter.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 */

package de.phleisch.app.itsucks.filter;

import de.phleisch.app.itsucks.Job;

/**
 * This interface is for a implementation which is used to filter a 
 * job when adding it to the job list. 
 * @author olli
 */
public interface JobFilter {

	public Job filter(Job pJob) throws Exception;
	public boolean supports(Job pJob);

}
