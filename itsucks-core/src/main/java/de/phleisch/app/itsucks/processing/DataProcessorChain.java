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

public class DataProcessorChain {

	protected List<DataProcessor> mDataProcessors = new ArrayList<DataProcessor>();
	
	protected DataRetriever mDataRetriever;
	protected Job mJob;
	protected JobManager mJobManager;
	
	protected boolean mInitialized = false;
	private boolean mStreamingEnabled;
	
	private byte[] mBufferedData = null;
	
	public DataProcessorChain() {
		
	}
	
	public DataProcessorChain(List<DataProcessor> pProcessorsForJob) {
		addDataProcessor(pProcessorsForJob);
	}

	public void addDataProcessor(List<DataProcessor> pProcessorsForJob) {
		
		for (DataProcessor processor : pProcessorsForJob) {
			addDataProcessor(processor);
		}
	}
	
	public void addDataProcessor(DataProcessor pDataProcessor) {
		
		if(mInitialized) {
			throw new IllegalStateException("Chain is already initialized.");
		}
		
		pDataProcessor.setProcessorChain(this);
		
		mDataProcessors.add(pDataProcessor);
	}
	
	public List<DataProcessor> getDataProcessors() {
		return new ArrayList<DataProcessor>(mDataProcessors);
	}
	
	/**
	 * Processes the given data chunk.
	 * @param pBuffer
	 * @param pBytes
	 * @throws Exception
	 */
	public void process(byte[] pBuffer, int pBytes) throws Exception {
		
		if(!mInitialized) {
			throw new IllegalStateException("Chain not initialized.");
		}
		
		if(mStreamingEnabled) {
			dispatchChunk(pBuffer, pBytes);
		} else {
			appendChunk(pBuffer, pBytes);
		}
	}

	private void dispatchChunk(byte[] pBuffer, int pBytes) throws Exception {
		
		//run through the data processor list
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			processor.process(pBuffer, pBytes);
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
		}
	}

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

	public void resumeAt(long pResumeOffset) {
		
		for (DataProcessor processor : mDataProcessors) {
			processor.resumeAt(pResumeOffset);
		}
	}

	public void init() throws Exception {
		
		if(mInitialized) return;
		
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

	public void finish() throws Exception {
		
		if(!mInitialized) return;
		
		if(!mStreamingEnabled) {
			dispatchChunk(mBufferedData, mBufferedData.length);
			mBufferedData = null;
		}
		
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			
			processor.finish();
		}
		
		mInitialized = false;
	}
	

	public void setDataRetriever(DataRetriever pDataRetriever) {
		mDataRetriever = pDataRetriever;
	}
	
	public DataRetriever getDataRetriever() {
		return mDataRetriever;
	}
	
	public void setJobManager(JobManager pJobManager) {
		mJobManager = pJobManager;
	}
	
	public JobManager getJobManager() {
		return mJobManager;
	}
	
	public void setJob(Job pJob) {
		mJob = pJob;
	}
	
	public Job getJob() {
		return mJob;
	}

	public int size() {
		return mDataProcessors.size();
	}
	
}
