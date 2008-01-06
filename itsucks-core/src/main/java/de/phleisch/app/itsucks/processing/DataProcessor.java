/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 10.06.2007
 */

package de.phleisch.app.itsucks.processing;

import de.phleisch.app.itsucks.job.Job;

/**
 * A data processor is a single component in a processing chain which processes
 * the data which is lead through.
 * 
 * @author olli
 *
 */
public interface DataProcessor {

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
	public abstract void init() throws ProcessingException;
	
	/**
	 * Shutdown the data processor. (Release buffers, file handles etc.)
	 * @throws Exception
	 */
	public abstract void finish();

	/**
	 * Will be called when the chain is aborted.
	 * After calling abort, <code>finish</code> is also called.
	 */
	public abstract void abort();
	
	/**
	 * Will be called when the chain is unexpectly aborted.
	 * After calling rollback, <code>finish</code> is also called.
	 */
	public abstract void rollback();
	
	/**
	 * Supports this data processor resuming?
	 * @return true == yes
	 */
	public abstract boolean canResume();
	
	/**
	 * 'Consumes' (saves, parses etc.) this processor to the data or is it
	 * only an filter?
	 * If an chain contains no consumer, the data is not processed.
	 * @return true == yes
	 */
	public abstract boolean isConsumer();
	
	/**
	 * Resumes the processing at the given position.
	 * @param pByteOffset The offset position in bytes.
	 */
	public abstract void resumeAt(long pByteOffset);
	
	/**
	 * Processes the given data chunk.
	 * @param pBuffer
	 * @param pBytes
	 * @return The pBuffer pointer or a new pointer to changed data.
	 * @throws Exception
	 */
//	public abstract byte[] process(byte[] pBuffer, int pBytes) throws Exception;
	public abstract DataChunk process(DataChunk pDataChunk) throws ProcessingException;
	
	/**
	 * Asks the processor if it needs the data from the data retriever
	 * in one chunk and not in multiple pieces.
	 * 
	 * @return
	 */
	public abstract boolean needsDataAsWholeChunk();

	/**
	 * Sets the processor chain the processor is a part of.
	 * 
	 * @param pChain
	 */
	public abstract void setProcessorChain(DataProcessorChain pChain);
	
}
