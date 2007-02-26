/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobManager;

public abstract class DataProcessor {

	protected DataRetriever mDataRetriever;
	protected Job mJob;
	protected JobManager mJobManager;
	
	public DataProcessor() {
		super();
	}

	public abstract boolean supports(Job pJob);
	public void init() throws Exception {}
	public abstract void process(byte[] pBuffer, int pBytes) throws Exception ;
	public void finish() throws Exception {}

	public void setDataRetriever(DataRetriever pDataRetriever) {
		mDataRetriever = pDataRetriever;
	}
	
	public void setJobManager(JobManager pJobManager) {
		mJobManager = pJobManager;
	}

	public Job getJob() {
		return mJob;
	}

	public void setJob(Job pJob) {
		mJob = pJob;
	}

	
}
