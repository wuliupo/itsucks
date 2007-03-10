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

	/**
	 * Returns true if this processor supports the given type of job.
	 * @param pJob
	 * @return
	 */
	public abstract boolean supports(Job pJob);
	
	/**
	 * Initializes the data processor. (Create buffers, open file handles etc.)
	 * @throws Exception
	 */
	public void init() throws Exception {}
	
	/**
	 * Processes the given data chunk.
	 * @param pBuffer
	 * @param pBytes
	 * @throws Exception
	 */
	public abstract void process(byte[] pBuffer, int pBytes) throws Exception;
	
	/**
	 * Shutdown the data processor. (Release buffers, file handles etc.)
	 * @throws Exception
	 */
	public void finish() throws Exception {}

	/**
	 * Supports this data processor resuming?
	 * @return true == yes
	 */
	public boolean canResume() {
		return false;
	}
	
	/**
	 * Resumes the processing at the given position.
	 * @param pByteOffset The offset position in bytes.
	 */
	public void resumeAt(long pByteOffset) {
		throw new IllegalArgumentException();
	}
	
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
