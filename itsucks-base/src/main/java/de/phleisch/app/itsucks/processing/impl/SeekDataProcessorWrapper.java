/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 11.06.2007
 */

package de.phleisch.app.itsucks.processing.impl;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.ProcessingException;

/**
 * This is data processor wrapper which encapsulates another data processor
 * and skips a given count of bytes before giving the data to the encapsulates
 * processor. This is useful for resuming a file to skip the bytes which are already on 
 * the disk.
 * 
 * @author olli
 *
 */
public class SeekDataProcessorWrapper implements DataProcessor {

	protected DataProcessor mDataProcessor;

	protected long mSeekPosition;

	private DataProcessorChain mChain;
	
	public SeekDataProcessorWrapper(DataProcessor pDataProcessor, long pSeekPosition) {
		mDataProcessor = pDataProcessor;
		mSeekPosition = pSeekPosition;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#finish()
	 */
	public void finish() {
		mDataProcessor.finish();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#init()
	 */
	public void init() throws ProcessingException {
		mDataProcessor.init();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#abort()
	 */
	public void abort() {
		mDataProcessor.abort();
	}	
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#rollback()
	 */
	public void rollback() {
		mDataProcessor.rollback();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(byte[], int)
	 */
	public DataChunk process(DataChunk pDataChunk) throws ProcessingException {
		
		if(mChain.getProcessedBytes() >= mSeekPosition) {
			
			return mDataProcessor.process(pDataChunk);
			
		} else if(mChain.getProcessedBytes() + pDataChunk.getSize() > mSeekPosition) {
			
			//ok, create a buffer containg the slice we need
			int offset = (int)(mSeekPosition - mChain.getProcessedBytes());
			
			byte[] slice = new byte[pDataChunk.getSize() - offset];
			System.arraycopy(pDataChunk.getData(), offset, slice, 0, slice.length);
			
			DataChunk result = mDataProcessor.process(new DataChunk(slice, slice.length, false));
			
			//copy the result back
			byte mergedResult[] = new byte[offset + result.getSize()];
			
			System.arraycopy(pDataChunk.getData(), 0, mergedResult, 0, offset);
			System.arraycopy(result.getData(), 0, mergedResult, offset, result.getSize());
			
			return new DataChunk(mergedResult, mergedResult.length, pDataChunk.isComplete());
			
		} else {
			
			// do nothing
			return pDataChunk;
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#resumeAt(long)
	 */
	public void resumeAt(long pByteOffset) {
		mDataProcessor.resumeAt(pByteOffset);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#setProcessorChain(de.phleisch.app.itsucks.processing.DataProcessorChain)
	 */
	public void setProcessorChain(DataProcessorChain pChain) {
		mChain = pChain;
		mDataProcessor.setProcessorChain(pChain);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return mDataProcessor.supports(pJob);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#getInfo()
	 */
	public DataProcessorInfo getInfo() {
		return mDataProcessor.getInfo();
	}
	
}
