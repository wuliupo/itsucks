/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 10.06.2007
 */

package de.phleisch.app.itsucks.processing;

import de.phleisch.app.itsucks.Job;

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
	public void init() throws Exception;
	
	/**
	 * Shutdown the data processor. (Release buffers, file handles etc.)
	 * @throws Exception
	 */
	public void finish() throws Exception;

	/**
	 * Supports this data processor resuming?
	 * @return true == yes
	 */
	public boolean canResume();
	
	/**
	 * Resumes the processing at the given position.
	 * @param pByteOffset The offset position in bytes.
	 */
	public void resumeAt(long pByteOffset);
	
	/**
	 * Processes the given data chunk.
	 * @param pBuffer
	 * @param pBytes
	 * @throws Exception
	 */
	public abstract byte[] process(byte[] pBuffer, int pBytes) throws Exception;
	
	public boolean needsDataAsWholeChunk();

	public abstract void setProcessorChain(DataProcessorChain pChain);
	
}
