/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 10.06.2007
 */

package de.phleisch.app.itsucks.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobManager;
import de.phleisch.app.itsucks.io.DataRetriever;

/**
 * This is an implementation of an processor chain.
 * 
 * @author olli
 *
 */
public class ProcessorChainImpl implements DataProcessorChain {

	protected List<DataProcessor> mDataProcessors = new ArrayList<DataProcessor>();
	
	protected DataRetriever mDataRetriever;
	protected Job mJob;
	protected JobManager mJobManager;
	
	protected boolean mInitialized = false;
	private boolean mStreamingEnabled;
	
	private byte[] mBufferedData = null;
	
	private long mProcessedBytes;
	
	public ProcessorChainImpl() {
		
	}
	
	public ProcessorChainImpl(List<DataProcessor> pProcessorsForJob) {
		addDataProcessor(pProcessorsForJob);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#addDataProcessor(java.util.List)
	 */
	public void addDataProcessor(List<DataProcessor> pProcessorsForJob) {
		
		for (DataProcessor processor : pProcessorsForJob) {
			addDataProcessor(processor);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#addDataProcessor(de.phleisch.app.itsucks.processing.DataProcessor)
	 */
	public void addDataProcessor(DataProcessor pDataProcessor) {
		
		mDataProcessors.add(pDataProcessor);
		pDataProcessor.setProcessorChain(this);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#replaceDataProcessor(de.phleisch.app.itsucks.processing.DataProcessor, de.phleisch.app.itsucks.processing.DataProcessor)
	 */
	public void replaceDataProcessor(DataProcessor pOldDataProcessor, DataProcessor pNewDataProcessor) {
		
		int index = mDataProcessors.indexOf(pOldDataProcessor);
		if(index < 0) {
			throw new IllegalArgumentException("DataProcessor to be replaced is not existing.");
		}
		mDataProcessors.set(index, pNewDataProcessor);
		pNewDataProcessor.setProcessorChain(this);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#getDataProcessors()
	 */
	public List<DataProcessor> getDataProcessors() {
		return new ArrayList<DataProcessor>(mDataProcessors);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#init()
	 */
	public void init() throws Exception {
		
		if(mInitialized) return;
		
		mProcessedBytes = 0;
		
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			processor.init();
		}

		//check if any processor needs the data as whole chunk
		mStreamingEnabled = true;
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			
			//when this processor needs the data as whole chung, disable streaming
			if(processor.needsDataAsWholeChunk()) {
				mStreamingEnabled = false;
				break;
			}
		}
		
		mInitialized = true;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#finish()
	 */
	public void finish() throws Exception {
		
		if(!mInitialized) return;
		
		if(!mStreamingEnabled && mBufferedData != null) {
			dispatchChunk(mBufferedData, mBufferedData.length);
			mProcessedBytes += mBufferedData.length;
			mBufferedData = null;
		}
		
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			
			processor.finish();
		}
		
		mInitialized = false;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#process(byte[], int)
	 */
	public void process(byte[] pBuffer, int pBytes) throws Exception {
		
		if(!mInitialized) {
			throw new IllegalStateException("Chain not initialized.");
		}
		
		if(mStreamingEnabled) {
			dispatchChunk(pBuffer, pBytes);
			
			mProcessedBytes += pBytes;
			
		} else {
			appendChunk(pBuffer, pBytes);
		}
		
	}

	private void dispatchChunk(byte[] pBuffer, int pBytes) throws Exception {
		
		byte[] data = pBuffer;
		
		//run through the data processor list
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			data = processor.process(data, pBytes);
		}
		
	}
	
	private void appendChunk(byte[] pBuffer, int pBytes) {
		
		if(mBufferedData == null) {
			
			mBufferedData = new byte[pBytes];
			System.arraycopy(pBuffer, 0, mBufferedData, 0, pBytes);
			
		} else {
			
			byte[] mergedChunk = new byte[mBufferedData.length + pBytes];
			System.arraycopy(mBufferedData, 0, mergedChunk, 0, mBufferedData.length);
			System.arraycopy(pBuffer, 0, mergedChunk, mBufferedData.length, pBytes);
			
			mBufferedData = mergedChunk;
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#canResume()
	 */
	public boolean canResume() {
		
		boolean resumePossible = true;
		for (DataProcessor processor : mDataProcessors) {
			
			//this processor can't resume, abort
			if(!processor.canResume()) {
				resumePossible = false;
				break;
			}
		}
		
		return resumePossible;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#resumeAt(long)
	 */
	public void resumeAt(long pResumeOffset) {
		
		for (DataProcessor processor : mDataProcessors) {
			processor.resumeAt(pResumeOffset);
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#setDataRetriever(de.phleisch.app.itsucks.io.DataRetriever)
	 */
	public void setDataRetriever(DataRetriever pDataRetriever) {
		mDataRetriever = pDataRetriever;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#getDataRetriever()
	 */
	public DataRetriever getDataRetriever() {
		return mDataRetriever;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#setJobManager(de.phleisch.app.itsucks.JobManager)
	 */
	public void setJobManager(JobManager pJobManager) {
		mJobManager = pJobManager;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#getJobManager()
	 */
	public JobManager getJobManager() {
		return mJobManager;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#setJob(de.phleisch.app.itsucks.Job)
	 */
	public void setJob(Job pJob) {
		mJob = pJob;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#getJob()
	 */
	public Job getJob() {
		return mJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#size()
	 */
	public int size() {
		return mDataProcessors.size();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#getProcessedBytes()
	 */
	public long getProcessedBytes() {
		return mProcessedBytes;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessorChain#containsConsumer()
	 */
	public boolean containsConsumer() {
		
		boolean hasConsumer = false;
		
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			if(it.next().isConsumer()) {
				hasConsumer = true;
				break;
			}
		}
		
		return hasConsumer;
	}
	
}
