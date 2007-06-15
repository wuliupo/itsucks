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

/**
 * This class holds an chain of data processors and tunnels the data
 * through all registered processors in the given order.
 * 
 * @author olli
 *
 */
public interface DataProcessorChain {

	/**
	 * Adds an list of data processors at the end of the chain.
	 * 
	 * @param pProcessorsForJob
	 */
	public abstract void addDataProcessor(List<DataProcessor> pProcessorsForJob);

	/**
	 * Adds an data processor at the end of the chain.
	 * 
	 * @param pDataProcessor
	 */
	public abstract void addDataProcessor(DataProcessor pDataProcessor);

	/**
	 * Replaces an data processor in the chain.
	 * 
	 * @param pOldDataProcessor
	 * @param pNewDataProcessor
	 */
	public abstract void replaceDataProcessor(DataProcessor pOldDataProcessor,
			DataProcessor pNewDataProcessor);

	/**
	 * Returns a list with all data processors in the chain.
	 * 
	 * @return
	 */
	public abstract List<DataProcessor> getDataProcessors();

	/**
	 * Initializes all the chain and all processors in it.
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;

	/**
	 * Finalizes all the chain and all processors in it.
	 * 
	 * @throws Exception
	 */
	public abstract void finish() throws Exception;

	/**
	 * Processes the given data chunk and dispatches it to the processors.
	 * 
	 * @param pBuffer
	 * @param pBytes
	 * @throws Exception
	 */
	public abstract void process(byte[] pBuffer, int pBytes) throws Exception;

	/**
	 * Checks if all processors in the chain are supporting
	 * resuming.
	 * 
	 * @return
	 */
	public abstract boolean canResume();

	/**
	 * Advises all processors to resume at the given position.
	 * 
	 * @param pResumeOffset
	 */
	public abstract void resumeAt(long pResumeOffset);

	/**
	 * Sets the data retriever.
	 * 
	 * @param pDataRetriever
	 */
	public abstract void setDataRetriever(DataRetriever pDataRetriever);

	/**
	 * Gets the data retriever.
	 * 
	 * @return
	 */
	public abstract DataRetriever getDataRetriever();

	/**
	 * Sets the job manager.
	 * 
	 * @param pJobManager
	 */
	public abstract void setJobManager(JobManager pJobManager);

	/**
	 * Gets the job manager.
	 * 
	 * @return
	 */
	public abstract JobManager getJobManager();

	/**
	 * Sets the job which the chain is assigned to.
	 * 
	 * @param pJob
	 */
	public abstract void setJob(Job pJob);

	/**
	 * Gets the job which the chain is assigned to.
	 * 
	 * @return
	 */
	public abstract Job getJob();

	/**
	 * Returns the count of registerd processors.
	 * 
	 * @return
	 */
	public abstract int size();

	/**
	 * Gets the already processed bytes in the chain.
	 * 
	 * @return
	 */
	public abstract long getProcessedBytes();

}