/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 11.06.2007
 */

package de.phleisch.app.itsucks.processing;

import java.util.List;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobManager;
import de.phleisch.app.itsucks.io.DataRetriever;

public interface DataProcessorChain {

	public abstract void addDataProcessor(List<DataProcessor> pProcessorsForJob);

	public abstract void addDataProcessor(DataProcessor pDataProcessor);

	public abstract void replaceDataProcessor(DataProcessor pOldDataProcessor,
			DataProcessor pNewDataProcessor);

	public abstract List<DataProcessor> getDataProcessors();

	public abstract void init() throws Exception;

	public abstract void finish() throws Exception;

	/**
	 * Processes the given data chunk.
	 * @param pBuffer
	 * @param pBytes
	 * @throws Exception
	 */
	public abstract void process(byte[] pBuffer, int pBytes) throws Exception;

	public abstract boolean canResume();

	public abstract void resumeAt(long pResumeOffset);

	public abstract void setDataRetriever(DataRetriever pDataRetriever);

	public abstract DataRetriever getDataRetriever();

	public abstract void setJobManager(JobManager pJobManager);

	public abstract JobManager getJobManager();

	public abstract void setJob(Job pJob);

	public abstract Job getJob();

	public abstract int size();

	public abstract long getProcessedBytes();

}