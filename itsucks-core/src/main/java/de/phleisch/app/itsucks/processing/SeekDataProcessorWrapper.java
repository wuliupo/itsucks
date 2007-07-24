/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 11.06.2007
 */

package de.phleisch.app.itsucks.processing;

import de.phleisch.app.itsucks.Job;

/**
 * This is data processor wrapper which encapsulates another data processor
 * and skips a given count of bytes before giving the data to the encapsulates
 * processor. This is useful for fileresuming to skip the bytes which are already on 
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
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#canResume()
	 */
	public boolean canResume() {
		return mDataProcessor.canResume();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#finish()
	 */
	public void finish() throws Exception {
		mDataProcessor.finish();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#init()
	 */
	public void init() throws Exception {
		mDataProcessor.init();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#needsDataAsWholeChunk()
	 */
	public boolean needsDataAsWholeChunk() {
		return mDataProcessor.needsDataAsWholeChunk();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(byte[], int)
	 */
	public byte[] process(byte[] pBuffer, int pBytes) throws Exception {
		
		if(mChain.getProcessedBytes() >= mSeekPosition) {
			
			return mDataProcessor.process(pBuffer, pBytes);
			
		} else if(mChain.getProcessedBytes() + pBytes > mSeekPosition) {
			
			//ok, create a buffer containg the slice we need
			int offset = (int)(mSeekPosition - mChain.getProcessedBytes());
			
			byte[] slice = new byte[pBytes - offset];
			System.arraycopy(pBuffer, offset, slice, 0, slice.length);
			
			byte[] result = mDataProcessor.process(slice, slice.length);
			
			//copy the result back
			byte mergedResult[] = new byte[offset + slice.length];
			
			System.arraycopy(pBuffer, 0, mergedResult, 0, offset);
			System.arraycopy(result, 0, mergedResult, offset, result.length);
			
			return mergedResult;
			
		} else {
			
			// do nothing
			return pBuffer;
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
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#isConsumer()
	 */
	public boolean isConsumer() {
		return mDataProcessor.isConsumer();
	}

}
