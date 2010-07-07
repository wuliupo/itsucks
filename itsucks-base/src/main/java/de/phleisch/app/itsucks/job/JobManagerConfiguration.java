/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.07.2010
 */

package de.phleisch.app.itsucks.job;

public class JobManagerConfiguration {
	public static final String CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION = 
		"JobManagerConfiguration";
	
	private boolean mDropIgnoredJobs = false;
	private boolean mDropFinishedJobs = false;
	
	public boolean isDropIgnoredJobs() {
		return mDropIgnoredJobs;
	}
	public void setDropIgnoredJobs(boolean pDropIgnoredJobs) {
		mDropIgnoredJobs = pDropIgnoredJobs;
	}
	public boolean isDropFinishedJobs() {
		return mDropFinishedJobs;
	}
	public void setDropFinishedJobs(boolean pDropFinishedJobs) {
		mDropFinishedJobs = pDropFinishedJobs;
	}
	
}
